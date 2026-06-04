#!/usr/bin/env python3
"""
Build baby-shower photo-card source PNGs (1536×1024) from designed naming/engagement art.

Each theme is a recoloured variant with soft pastel overlays and balloon/star accents so
cards stay distinct until custom designer PNGs replace them in tools/bg_sources/.

    python3 tools/generate_babyshower_sources.py
    python3 tools/convert_photo_backgrounds.py --prefix babyshower
"""

from __future__ import annotations

import math
from pathlib import Path

from PIL import Image, ImageDraw, ImageEnhance, ImageFilter

SRC_DIR = Path(__file__).parent / "bg_sources"
OUT_SIZE = (1536, 1024)

# (output stem, base source file, color overlay RGBA, accent hex)
THEMES: list[tuple[str, str, tuple[int, int, int, int], str]] = [
    ("babyshower_01", "naming_01_source.png", (255, 210, 225, 42), "#E91E8C"),
    ("babyshower_02", "naming_02_source.png", (200, 225, 255, 38), "#1976D2"),
    ("babyshower_03", "naming_03_source.png", (255, 248, 210, 36), "#F9A825"),
    ("babyshower_04", "naming_04_source.png", (230, 215, 255, 40), "#7E57C2"),
    ("babyshower_05", "engagement_04_source.png", (215, 245, 230, 34), "#43A047"),
]


def tint(im: Image.Image, rgba: tuple[int, int, int, int]) -> Image.Image:
    overlay = Image.new("RGBA", im.size, rgba)
    base = im.convert("RGBA")
    return Image.alpha_composite(base, overlay).convert("RGB")


def draw_balloon(draw: ImageDraw.ImageDraw, cx: int, cy: int, r: int, fill: str, highlight: str) -> None:
    draw.ellipse((cx - r, cy - r, cx + r, cy + r), fill=fill)
    draw.ellipse((cx - r // 3, cy - r // 2, cx - r // 6, cy - r // 3), fill=highlight)
    draw.line((cx, cy + r, cx, cy + r + 28), fill=fill, width=2)


def draw_star(draw: ImageDraw.ImageDraw, cx: int, cy: int, size: int, fill: str) -> None:
    points = []
    for i in range(10):
        angle = i * 3.14159 / 5 - 3.14159 / 2
        radius = size if i % 2 == 0 else size // 2
        points.append((cx + radius * math.cos(angle), cy + radius * math.sin(angle)))
    draw.polygon(points, fill=fill)


def add_baby_accents(im: Image.Image, accent: str, index: int) -> Image.Image:
    out = im.copy()
    draw = ImageDraw.Draw(out)
    w, h = out.size
    balloons = [
        (int(w * 0.12), int(h * 0.10), 22),
        (int(w * 0.88), int(h * 0.12), 20),
        (int(w * 0.08), int(h * 0.22), 16),
        (int(w * 0.92), int(h * 0.20), 18),
    ]
    for i, (bx, by, br) in enumerate(balloons[: 2 + index % 2]):
        draw_balloon(draw, bx, by, br, accent, "#FFFFFF")
    for sx, sy, ss in [
        (int(w * 0.78), int(h * 0.08), 10),
        (int(w * 0.22), int(h * 0.07), 8),
        (int(w * 0.50), int(h * 0.06), 7),
    ][: 1 + index % 3]:
        draw_star(draw, sx, sy, ss, accent)
    return out.filter(ImageFilter.GaussianBlur(radius=0.3))


def build_one(stem: str, base_name: str, overlay: tuple[int, int, int, int], accent: str) -> None:
    src = SRC_DIR / base_name
    if not src.exists():
        raise FileNotFoundError(f"Missing base art: {src}")
    im = Image.open(src).convert("RGB")
    if im.size != OUT_SIZE:
        im = im.resize(OUT_SIZE, Image.Resampling.LANCZOS)
    im = tint(im, overlay)
    im = ImageEnhance.Color(im).enhance(1.08)
    im = ImageEnhance.Brightness(im).enhance(1.03)
    im = add_baby_accents(im, accent, int(stem.split("_")[1]))
    dest = SRC_DIR / f"{stem}_source.png"
    im.save(dest, format="PNG", optimize=True)
    print(f"Wrote {dest.name} (from {base_name})")


def main() -> int:
    SRC_DIR.mkdir(parents=True, exist_ok=True)
    for stem, base, overlay, accent in THEMES:
        build_one(stem, base, overlay, accent)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
