#!/usr/bin/env python3
"""Generate 1080×1350 engagement invitation backgrounds (3 traditional + 2 modern)."""

from __future__ import annotations

import math
import random
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

W, H = 1080, 1350
OUT_DIR = Path(__file__).resolve().parent / "bg_sources"

# Shared layout: arch photo top, clean cream text band, décor confined to bottom strip
ARCH = dict(left=278, top=108, right=802, bottom=448)
TEXT_TOP = 500
TEXT_BOTTOM = 905
DECOR_TOP = 940


def lerp(a: float, b: float, t: float) -> float:
    return a + (b - a) * t


def rgb(r: int, g: int, b: int) -> tuple[int, int, int]:
    return (r, g, b)


def vertical_gradient(size: tuple[int, int], top: tuple[int, int, int], bottom: tuple[int, int, int]) -> Image.Image:
    w, h = size
    img = Image.new("RGB", (w, h))
    px = img.load()
    for y in range(h):
        t = y / max(h - 1, 1)
        c = tuple(int(lerp(top[i], bottom[i], t)) for i in range(3))
        for x in range(w):
            px[x, y] = c
    return img


def add_noise(img: Image.Image, strength: int = 8) -> Image.Image:
    rng = random.Random(42)
    overlay = Image.new("RGB", img.size)
    px = overlay.load()
    base = img.load()
    for y in range(img.height):
        for x in range(img.width):
            n = rng.randint(-strength, strength)
            b = base[x, y]
            px[x, y] = tuple(max(0, min(255, c + n)) for c in b)
    return Image.blend(img, overlay, 0.06)


def cream_arch_panel(
    base: Image.Image,
    fill: tuple[int, int, int] = (247, 240, 228),
    text_fill: tuple[int, int, int] | None = None,
) -> None:
    """Cream arch for photo; optional separate fill for text band."""
    layer = Image.new("RGBA", base.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    left, top, right, bottom = ARCH["left"], ARCH["top"], ARCH["right"], ARCH["bottom"]
    rise = (right - left) * 0.22
    text_color = text_fill or fill
    d.pieslice([left, top, right, top + rise * 2], 180, 0, fill=fill + (255,))
    d.rectangle([left, top + rise, right, bottom], fill=fill + (255,))
    d.rectangle([left, bottom, right, TEXT_BOTTOM], fill=text_color + (255,))
    base.paste(layer, (0, 0), layer)


def draw_gold_line(draw: ImageDraw.ImageDraw, xy: tuple, width: int = 3, color: tuple[int, int, int] = (198, 152, 58)) -> None:
    draw.line(xy, fill=color, width=width)


def marigold_chain(d: ImageDraw.ImageDraw, y: int, x0: int, x1: int, step: int = 34) -> None:
    colors = [(245, 124, 0), (255, 179, 0), (230, 81, 0)]
    x = x0
    i = 0
    while x < x1:
        r = 14 + (i % 3) * 2
        d.ellipse([x - r, y - r, x + r, y + r], fill=colors[i % 3])
        d.ellipse([x - r + 4, y - r + 4, x + r - 4, y + r - 4], fill=(255, 213, 79))
        x += step
        i += 1


def jasmine_chain(d: ImageDraw.ImageDraw, y: int, x0: int, x1: int) -> None:
    x = x0
    while x < x1:
        for dx in range(0, 28, 7):
            d.ellipse([x + dx - 3, y - 4, x + dx + 3, y + 2], fill=(255, 255, 250))
        x += 32


def draw_kolam_band(d: ImageDraw.ImageDraw, cy: int, radius: int, color: tuple[int, int, int]) -> None:
    cx = W // 2
    for ring in range(6, radius, 14):
        d.ellipse([cx - ring, cy - ring, cx + ring, cy + ring], outline=color, width=2)
    petals = 12
    for i in range(petals):
        ang = 2 * math.pi * i / petals
        x1 = cx + math.cos(ang) * (radius * 0.35)
        y1 = cy + math.sin(ang) * (radius * 0.35)
        x2 = cx + math.cos(ang) * radius
        y2 = cy + math.sin(ang) * radius
        d.line([(x1, y1), (x2, y2)], fill=color, width=2)


def draw_kalash(d: ImageDraw.ImageDraw, cx: int, base_y: int) -> None:
    d.ellipse([cx - 70, base_y - 30, cx + 70, base_y + 10], fill=(218, 165, 32))
    d.rectangle([cx - 48, base_y - 95, cx + 48, base_y - 25], fill=(255, 215, 90))
    d.ellipse([cx - 55, base_y - 110, cx + 55, base_y - 70], fill=(205, 150, 30))
    d.ellipse([cx - 22, base_y - 135, cx + 22, base_y - 95], fill=(139, 90, 43))
    # mango leaves
    d.polygon([(cx, base_y - 150), (cx - 35, base_y - 115), (cx - 5, base_y - 120)], fill=(56, 142, 60))
    d.polygon([(cx, base_y - 150), (cx + 35, base_y - 115), (cx + 5, base_y - 120)], fill=(67, 160, 71))


def draw_vilakku_pair(d: ImageDraw.ImageDraw, y: int) -> None:
    for cx in (200, W - 200):
        d.ellipse([cx - 28, y - 10, cx + 28, y + 30], fill=(255, 193, 7))
        d.rectangle([cx - 8, y + 28, cx + 8, y + 70], fill=(121, 85, 72))
        d.ellipse([cx - 18, y - 35, cx + 18, y - 5], fill=(255, 235, 120))


def draw_gopuram(d: ImageDraw.ImageDraw, cx: int, base_y: int, color: tuple[int, int, int]) -> None:
    tiers = [(140, 55), (110, 48), (82, 42), (58, 38)]
    y = base_y
    for w, h in tiers:
        d.polygon([(cx, y - h), (cx - w // 2, y), (cx + w // 2, y)], fill=color)
        y -= h - 6


def bottom_strip_mask(img: Image.Image, y0: int, color: tuple[int, int, int]) -> None:
    d = ImageDraw.Draw(img)
    d.rectangle([0, y0, W, H], fill=color)


def save_design(img: Image.Image, name: str) -> Path:
    path = OUT_DIR / f"{name}_source.png"
    img.save(path, format="PNG", optimize=True)
    return path


def design_traditional_gopuram() -> Image.Image:
    img = vertical_gradient((W, H), (62, 12, 18), (120, 28, 34))
    img = add_noise(img, 6)
    cream_arch_panel(img, (248, 236, 220))
    d = ImageDraw.Draw(img)
    # Side panels
    d.rectangle([0, 0, 120, H], fill=(48, 10, 14))
    d.rectangle([W - 120, 0, W, H], fill=(48, 10, 14))
    draw_gopuram(d, 95, 320, (180, 140, 50))
    draw_gopuram(d, W - 95, 320, (180, 140, 50))
    marigold_chain(d, 82, 130, W - 130)
    marigold_chain(d, ARCH["bottom"] + 14, 170, W - 170, 36)
    draw_gold_line(d, [(100, ARCH["top"] - 6), (W - 100, ARCH["top"] - 6)], 4)
    draw_gold_line(d, [(140, TEXT_BOTTOM + 12), (W - 140, TEXT_BOTTOM + 12)], 2)
    bottom_strip_mask(img, DECOR_TOP, (72, 16, 22))
    d = ImageDraw.Draw(img)
    draw_kolam_band(d, H - 175, 120, (255, 230, 200))
    draw_kalash(d, W // 2, H - 95)
    d.text((W // 2 - 80, H - 28), "ॐ", fill=(198, 152, 58))
    return img


def design_traditional_jasmine() -> Image.Image:
    img = vertical_gradient((W, H), (18, 72, 42), (34, 96, 56))
    cream_arch_panel(img, (250, 245, 235))
    d = ImageDraw.Draw(img)
    # Banana leaf side curves
    for side, sign in ((0, 1), (W, -1)):
        pts = []
        for t in range(0, 101, 2):
            y = 80 + t * 11
            x = side + sign * (40 + 25 * math.sin(t / 12))
            pts.append((x, y))
        if len(pts) > 2:
            d.polygon(pts + [(side, H), (side, 80)], fill=(27, 94, 32))
    jasmine_chain(d, 88, 100, W - 100)
    jasmine_chain(d, ARCH["bottom"] + 5, 140, W - 140)
    d.rectangle([0, DECOR_TOP, W, H], fill=(22, 78, 38))
    draw_vilakku_pair(d, H - 130)
    draw_kolam_band(d, H - 200, 90, (230, 255, 230))
    return img


def design_traditional_saffron_kolam() -> Image.Image:
    img = vertical_gradient((W, H), (255, 183, 77), (255, 248, 225))
    cream_arch_panel(img, (255, 252, 245))
    d = ImageDraw.Draw(img)
    for x in range(60, W, 80):
        d.ellipse([x - 8, 70, x + 8, 95], fill=(198, 152, 58))
        d.rectangle([x - 3, 95, x + 3, 115], fill=(160, 120, 40))
    marigold_chain(d, 100, 120, W - 120, 36)
    d.rectangle([0, DECOR_TOP, W, H], fill=(230, 120, 40))
    d = ImageDraw.Draw(img)
    draw_kolam_band(d, H - 190, 130, (255, 255, 255))
    draw_kalash(d, W // 2, H - 100)
    draw_vilakku_pair(d, H - 140)
    return img


def design_modern_blush() -> Image.Image:
    img = vertical_gradient((W, H), (255, 248, 245), (252, 236, 240))
    cream_arch_panel(img, (255, 252, 250))
    d = ImageDraw.Draw(img)
    # Thin gold frame
    margin = 48
    draw_gold_line(d, [(margin, margin), (W - margin, margin)], 2, (210, 170, 120))
    draw_gold_line(d, [(margin, H - margin), (W - margin, H - margin)], 2)
    draw_gold_line(d, [(margin, margin), (margin, H - margin)], 2)
    draw_gold_line(d, [(W - margin, margin), (W - margin, H - margin)], 2)
    # Corner botanicals
    for cx, cy in [(margin + 40, margin + 50), (W - margin - 40, margin + 50)]:
        for i in range(6):
            ang = i * 1.05
            r = 35 + i * 6
            x = cx + math.cos(ang) * r
            y = cy + math.sin(ang) * r
            d.ellipse([x - 18, y - 12, x + 18, y + 12], fill=(244, 194, 203))
    # Minimal bottom — single line + rings
    d.line([(200, H - 120), (W - 200, H - 120)], fill=(210, 170, 120), width=2)
    d.ellipse([W // 2 - 42, H - 105, W // 2 - 18, H - 81], outline=(198, 152, 58), width=3)
    d.ellipse([W // 2 + 18, H - 105, W // 2 + 42, H - 81], outline=(198, 152, 58), width=3)
    return img


def design_modern_midnight() -> Image.Image:
    img = vertical_gradient((W, H), (13, 20, 48), (26, 35, 78))
    cream_arch_panel(img, fill=(238, 242, 255), text_fill=(32, 42, 96))
    img = img.convert("RGB")
    d = ImageDraw.Draw(img)
    # Art-deco corners
    gold = (255, 215, 130)
    for ox, oy, sx, sy in [
        (60, 60, 1, 1),
        (W - 60, 60, -1, 1),
        (60, H - 60, 1, -1),
        (W - 60, H - 60, -1, -1),
    ]:
        d.line([(ox, oy), (ox + 120 * sx, oy)], fill=gold, width=3)
        d.line([(ox, oy), (ox, oy + 90 * sy)], fill=gold, width=3)
    # Bokeh
    rng = random.Random(7)
    for _ in range(35):
        x = rng.randint(0, W)
        y = rng.randint(DECOR_TOP, H)
        r = rng.randint(8, 28)
        alpha = rng.randint(30, 90)
        bokeh = Image.new("RGBA", (r * 2, r * 2), (255, 230, 180, alpha))
        img.paste(bokeh, (x - r, y - r), bokeh)
    d = ImageDraw.Draw(img)
    d.rectangle([0, DECOR_TOP, W, H], fill=(18, 26, 58))
    d.line([(160, H - 150), (W - 160, H - 150)], fill=gold, width=2)
    for cx in range(180, W - 140, 90):
        d.ellipse([cx - 10, H - 175, cx + 10, H - 155], fill=(255, 235, 150))
    return img


def main() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    designs = [
        ("engagement_01", design_traditional_gopuram),
        ("engagement_02", design_traditional_jasmine),
        ("engagement_03", design_traditional_saffron_kolam),
        ("engagement_04", design_modern_blush),
        ("engagement_05", design_modern_midnight),
    ]
    for name, fn in designs:
        img = fn()
        path = save_design(img, name)
        print(f"Wrote {path}")


if __name__ == "__main__":
    main()
