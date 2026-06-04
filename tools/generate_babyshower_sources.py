#!/usr/bin/env python3
"""
Paint five original baby-shower photo-card backgrounds (1536×1024).

These are party-themed (balloons, bunting, clouds, gifts) — not recoloured naming art.

    python3 tools/generate_babyshower_sources.py
    python3 tools/convert_photo_backgrounds.py --prefix babyshower
"""

from __future__ import annotations

import math
import random
from pathlib import Path
from typing import Callable

from PIL import Image, ImageDraw, ImageFilter

OUT_SIZE = (1536, 1024)
SRC_DIR = Path(__file__).parent / "bg_sources"

# Cream panel for photo + text (matches engagement/naming card structure)
PANEL = (int(1536 * 0.18), int(1024 * 0.12), int(1536 * 0.82), int(1024 * 0.88))
CREAM = (252, 247, 238)


def _hex(h: str) -> tuple[int, int, int]:
    h = h.lstrip("#")
    return int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)


def paper_gradient(w: int, h: int, top: tuple[int, int, int], bottom: tuple[int, int, int]) -> Image.Image:
    im = Image.new("RGB", (w, h))
    px = im.load()
    for y in range(h):
        t = y / max(h - 1, 1)
        row = tuple(int(top[i] * (1 - t) + bottom[i] * t) for i in range(3))
        for x in range(w):
            n = random.randint(-4, 4)
            px[x, y] = tuple(max(0, min(255, c + n)) for c in row)
    return im.filter(ImageFilter.GaussianBlur(radius=0.6))


def fill_cream_panel(im: Image.Image, panel: tuple[int, int, int, int]) -> None:
    draw = ImageDraw.Draw(im)
    x0, y0, x1, y1 = panel
    for y in range(y0, y1):
        t = (y - y0) / max(y1 - y0 - 1, 1)
        shade = tuple(int(CREAM[i] * (0.97 + 0.03 * math.sin(t * math.pi)) + random.randint(-2, 2)) for i in range(3))
        draw.line([(x0, y), (x1, y)], fill=shade, width=1)


def draw_balloon(
    draw: ImageDraw.ImageDraw,
    cx: float,
    cy: float,
    r: float,
    fill: str,
    highlight: str = "#FFFFFF",
) -> None:
    draw.ellipse((cx - r, cy - r, cx + r, cy + r), fill=fill)
    draw.ellipse((cx - r * 0.35, cy - r * 0.45, cx - r * 0.1, cy - r * 0.2), fill=highlight)
    draw.line((cx, cy + r, cx + random.uniform(-6, 6), cy + r + 40), fill=fill, width=2)


def draw_bunting(
    draw: ImageDraw.ImageDraw,
    y: float,
    colors: list[str],
    w: int,
) -> None:
    rope_y = y
    draw.line([(w * 0.08, rope_y), (w * 0.92, rope_y)], fill="#C9A227", width=3)
    n = len(colors)
    for i, col in enumerate(colors):
        t = (i + 0.5) / n
        cx = w * 0.08 + (w * 0.84) * t
        pts = [(cx - 28, rope_y), (cx + 28, rope_y), (cx, rope_y + 52)]
        draw.polygon(pts, fill=col)


def draw_cloud(draw: ImageDraw.ImageDraw, cx: float, cy: float, scale: float, fill: str) -> None:
    r = 22 * scale
    for ox, oy in [(-r, 0), (0, -r * 0.4), (r, 0), (r * 0.5, r * 0.3), (-r * 0.5, r * 0.3)]:
        draw.ellipse((cx + ox - r, cy + oy - r, cx + ox + r, cy + oy + r), fill=fill)


def draw_star(draw: ImageDraw.ImageDraw, cx: float, cy: float, size: float, fill: str) -> None:
    pts = []
    for i in range(10):
        ang = i * math.pi / 5 - math.pi / 2
        rad = size if i % 2 == 0 else size * 0.42
        pts.append((cx + rad * math.cos(ang), cy + rad * math.sin(ang)))
    draw.polygon(pts, fill=fill)


def draw_rattle(draw: ImageDraw.ImageDraw, cx: float, cy: float, accent: str) -> None:
    draw.ellipse((cx - 18, cy - 38, cx + 18, cy - 2), fill=accent)
    draw.rectangle((cx - 5, cy - 2, cx + 5, cy + 28), fill="#E8C872")
    draw.ellipse((cx - 14, cy + 20, cx + 14, cy + 48), fill=accent, outline="#C9A227", width=2)


def draw_booties(draw: ImageDraw.ImageDraw, cx: float, cy: float, fill: str) -> None:
    for dx in (-22, 10):
        draw.ellipse((cx + dx - 16, cy - 10, cx + dx + 16, cy + 18), fill=fill)
        draw.ellipse((cx + dx - 10, cy + 10, cx + dx + 22, cy + 28), fill=fill)


def draw_gift(draw: ImageDraw.ImageDraw, cx: float, cy: float, body: str, ribbon: str) -> None:
    draw.rectangle((cx - 32, cy - 20, cx + 32, cy + 28), fill=body)
    draw.rectangle((cx - 6, cy - 20, cx + 6, cy + 28), fill=ribbon)
    draw.rectangle((cx - 32, cy - 4, cx + 32, cy + 8), fill=ribbon)
    draw.ellipse((cx - 14, cy - 32, cx - 2, cy - 18), fill=ribbon)
    draw.ellipse((cx + 2, cy - 32, cx + 14, cy - 18), fill=ribbon)


def draw_teddy(draw: ImageDraw.ImageDraw, cx: float, cy: float) -> None:
    brown = "#A1887F"
    draw.ellipse((cx - 38, cy - 28, cx + 38, cy + 42), fill=brown)
    draw.ellipse((cx - 48, cy - 48, cx - 18, cy - 18), fill=brown)
    draw.ellipse((cx + 18, cy - 48, cx + 48, cy - 18), fill=brown)
    draw.ellipse((cx - 10, cy - 8, cx - 2, cy), fill="#3E2723")
    draw.ellipse((cx + 2, cy - 8, cx + 10, cy), fill="#3E2723")
    draw.ellipse((cx - 4, cy + 6, cx + 4, cy + 14), fill="#5D4037")


def side_balloon_column(draw: ImageDraw.ImageDraw, x: float, colors: list[str], h: int) -> None:
    ys = [int(h * 0.18), int(h * 0.32), int(h * 0.48), int(h * 0.64)]
    for i, y in enumerate(ys):
        draw_balloon(draw, x, y, 20 + (i % 3) * 4, colors[i % len(colors)])


def build_card_01(im: Image.Image) -> None:
    """Pink balloon bunting — party garland, confetti sides."""
    w, h = im.size
    draw = ImageDraw.Draw(im)
    draw_bunting(draw, h * 0.06, ["#F48FB1", "#F8BBD9", "#FFFFFF", "#F06292", "#FCE4EC"], w)
    side_balloon_column(draw, w * 0.06, ["#EC407A", "#F48FB1", "#F8BBD9", "#F06292"], h)
    side_balloon_column(draw, w * 0.94, ["#F06292", "#F8BBD9", "#EC407A", "#F48FB1"], h)
    for _ in range(40):
        x, y = random.randint(0, w), random.randint(0, h)
        s = random.randint(3, 7)
        draw.ellipse((x, y, x + s, y + s), fill=random.choice(["#F8BBD9", "#FCE4EC", "#F48FB1"]))
    draw_rattle(draw, w * 0.5, h * 0.92, "#EC407A")
    draw_booties(draw, w * 0.38, h * 0.90, "#F48FB1")
    draw_booties(draw, w * 0.62, h * 0.90, "#F06292")


def build_card_02(im: Image.Image) -> None:
    """Blue cloud dream — sky palette, stars, teddy."""
    w, h = im.size
    draw = ImageDraw.Draw(im)
    draw_cloud(draw, w * 0.35, h * 0.08, 1.2, "#FFFFFF")
    draw_cloud(draw, w * 0.55, h * 0.06, 1.0, "#E3F2FD")
    draw_cloud(draw, w * 0.72, h * 0.10, 0.9, "#FFFFFF")
    for sx in [0.12, 0.22, 0.88, 0.78]:
        for sy in [0.2, 0.35, 0.55, 0.7]:
            draw_star(draw, w * sx, h * sy, random.randint(6, 12), "#FFD54F")
    draw_balloon(draw, w * 0.08, h * 0.25, 24, "#42A5F5")
    draw_balloon(draw, w * 0.92, h * 0.28, 22, "#64B5F6")
    draw_balloon(draw, w * 0.07, h * 0.55, 18, "#90CAF9")
    draw_balloon(draw, w * 0.93, h * 0.52, 20, "#1E88E5")
    draw_teddy(draw, w * 0.5, h * 0.88)


def build_card_03(im: Image.Image) -> None:
    """Mint gold confetti — mint border, gifts & cupcakes."""
    w, h = im.size
    draw = ImageDraw.Draw(im)
    draw_bunting(draw, h * 0.07, ["#A5D6A7", "#FFF9C4", "#FFD54F", "#C8E6C9", "#FFF59D"], w)
    draw.rectangle((0, 0, w, int(h * 0.11)), fill="#C8E6C9")
    draw.rectangle((0, int(h * 0.89), w, h), fill="#A5D6A7")
    for x in [0.1, 0.9]:
        for y in [0.22, 0.4, 0.58]:
            draw_balloon(draw, w * x, h * y, 18, random.choice(["#66BB6A", "#FFD54F", "#AED581"]))
    draw_gift(draw, w * 0.42, h * 0.86, "#81C784", "#FFD54F")
    draw_gift(draw, w * 0.58, h * 0.86, "#FFD54F", "#66BB6A")
    # cupcake
    cx, cy = w * 0.5, h * 0.90
    draw.polygon([(cx - 30, cy), (cx + 30, cy), (cx + 20, cy + 28), (cx - 20, cy + 28)], fill="#A1887F")
    draw.ellipse((cx - 34, cy - 22, cx + 34, cy + 6), fill="#F48FB1")


def build_card_04(im: Image.Image, sky: tuple[int, int, int] = (237, 228, 248)) -> None:
    """Lavender starlight — moon, stars, bottle & rattle."""
    w, h = im.size
    draw = ImageDraw.Draw(im)
    mx, my = w * 0.5, h * 0.09
    draw.ellipse((mx - 45, my - 45, mx + 45, my + 45), fill="#FFF9C4")
    draw.ellipse((mx - 20, my - 50, mx + 50, my + 20), fill=sky)
    for _ in range(55):
        draw_star(
            draw,
            random.randint(int(w * 0.05), int(w * 0.95)),
            random.randint(int(h * 0.05), int(h * 0.25)),
            random.randint(4, 9),
            random.choice(["#FFD54F", "#FFFFFF", "#E1BEE7"]),
        )
    side_balloon_column(draw, w * 0.07, ["#CE93D8", "#BA68C8", "#E1BEE7", "#AB47BC"], h)
    side_balloon_column(draw, w * 0.93, ["#AB47BC", "#E1BEE7", "#CE93D8", "#9575CD"], h)
    draw_rattle(draw, w * 0.46, h * 0.90, "#7E57C2")
    # bottle
    bx = w * 0.56
    draw.rectangle((bx - 8, h * 0.84, bx + 8, h * 0.94), fill="#E1BEE7", outline="#7E57C2", width=2)
    draw.ellipse((bx - 12, h * 0.82, bx + 12, h * 0.88), fill="#FFFFFF")


def build_card_05(im: Image.Image) -> None:
    """Peach ribbon cascade — coral/peach ribbons, heart balloons."""
    w, h = im.size
    draw = ImageDraw.Draw(im)
    for i in range(8):
        x = w * (0.1 + i * 0.1)
        draw.line([(x, 0), (x + 30, h * 0.14)], fill="#FFAB91", width=5)
    draw_bunting(draw, h * 0.08, ["#FFCCBC", "#FFAB91", "#FF8A65", "#FFFFFF", "#FF7043"], w)
    draw.ellipse((w * 0.08, h * 0.3, w * 0.08 + 36, h * 0.3 + 32), fill="#FF8A65")
    draw.polygon(
        [(w * 0.08 + 18, h * 0.3 + 32), (w * 0.08 + 6, h * 0.3 + 48), (w * 0.08 + 30, h * 0.3 + 48)],
        fill="#FF8A65",
    )
    for x in [0.92, 0.08]:
        draw_balloon(draw, w * x, h * 0.22, 26, "#FF7043")
        draw_balloon(draw, w * x, h * 0.48, 20, "#FFAB91")
    draw_booties(draw, w * 0.5, h * 0.89, "#FF8A65")
    for dx in [-60, 0, 60]:
        draw.ellipse((w * 0.5 + dx - 8, h * 0.78, w * 0.5 + dx + 8, h * 0.78 + 14), fill="#FFCCBC")


THEMES: list[tuple[str, tuple[int, int, int], tuple[int, int, int], Callable[..., None]]] = [
    ("babyshower_01", (255, 235, 245), (252, 228, 236), build_card_01),
    ("babyshower_02", (225, 240, 255), (210, 230, 250), build_card_02),
    ("babyshower_03", (232, 245, 233), (220, 237, 222), build_card_03),
    ("babyshower_04", (237, 228, 248), (225, 215, 240), build_card_04),
    ("babyshower_05", (255, 243, 235), (252, 232, 220), build_card_05),
]


def build_one(stem: str, top: tuple[int, int, int], bottom: tuple[int, int, int], painter: Callable[..., None]) -> None:
    random.seed(int(stem.split("_")[1]) * 7919)
    w, h = OUT_SIZE
    im = paper_gradient(w, h, top, bottom)
    fill_cream_panel(im, PANEL)
    if painter is build_card_04:
        painter(im, top)
    else:
        painter(im)
  # Keep décor out of central cream photo band
    draw = ImageDraw.Draw(im)
    x0, y0, x1, y1 = PANEL
    photo_bottom = int(y0 + (y1 - y0) * 0.48)
    draw.rectangle((x0 + 8, y0 + 8, x1 - 8, photo_bottom), fill=CREAM)
    dest = SRC_DIR / f"{stem}_source.png"
    im.save(dest, format="PNG", optimize=True)
    print(f"Wrote {dest.name}")


def main() -> int:
    SRC_DIR.mkdir(parents=True, exist_ok=True)
    for stem, top, bottom, painter in THEMES:
        build_one(stem, top, bottom, painter)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
