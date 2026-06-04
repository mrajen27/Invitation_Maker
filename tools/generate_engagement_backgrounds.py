#!/usr/bin/env python3
"""
Generate 1080×1350 engagement backgrounds matching the user reference board:
3 traditional (green toran, marigold royal, peacock lotus) + 2 modern (pink roses, sage botanical).
"""

from __future__ import annotations

import math
import random
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

W, H = 1080, 1350
OUT_DIR = Path(__file__).resolve().parent / "bg_sources"

# Inner photo opening (inside gold scalloped frame)
PHOTO = dict(left=252, top=118, right=828, bottom=518)
TEXT_BOTTOM = 1020
DECOR_FLOOR = 1005


def lerp(a: float, b: float, t: float) -> float:
    return a + (b - a) * t


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


def radial_wash(size: tuple[int, int], center: tuple[int, int], inner: tuple, outer: tuple, radius: int) -> Image.Image:
    img = Image.new("RGB", size)
    cx, cy = center
    px = img.load()
    for y in range(size[1]):
        for x in range(size[0]):
            d = math.hypot(x - cx, y - cy) / radius
            t = min(1.0, d)
            px[x, y] = tuple(int(lerp(inner[i], outer[i], t)) for i in range(3))
    return img


def paper_texture(base: Image.Image, strength: float = 0.05) -> Image.Image:
    rng = random.Random(99)
    noise = Image.new("RGB", base.size)
    npx = noise.load()
    for y in range(base.height):
        for x in range(base.width):
            n = rng.randint(-10, 10)
            npx[x, y] = (128 + n, 128 + n, 128 + n)
    noise = noise.filter(ImageFilter.GaussianBlur(1))
    return Image.blend(base, noise, strength)


def draw_center_panel(img: Image.Image, fill: tuple[int, int, int], alpha: int = 255) -> None:
    """Full-width cream panel for photo + text (reference layout)."""
    layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    d.rectangle([118, 72, W - 118, TEXT_BOTTOM], fill=fill + (alpha,))
    img.paste(layer, (0, 0), layer)


def scalloped_arch_mask(size: tuple[int, int]) -> Image.Image:
    """Mask for photo opening with scalloped top (like reference)."""
    mask = Image.new("L", size, 0)
    d = ImageDraw.Draw(mask)
    left, top, right, bottom = PHOTO["left"], PHOTO["top"], PHOTO["right"], PHOTO["bottom"]
    rise = (right - left) * 0.19
    # Base arch
    d.pieslice([left, top, right, top + rise * 2], 180, 0, fill=255)
    d.rectangle([left, top + rise, right, bottom], fill=255)
    # Scallops along top arch
    scallops = 11
    span = right - left
    for i in range(scallops):
        cx = left + span * (i + 0.5) / scallops
        r = span / (scallops * 2.1)
        d.ellipse([cx - r, top - r * 0.35, cx + r, top + r * 1.35], fill=255)
    return mask.filter(ImageFilter.GaussianBlur(0.6))


def draw_scalloped_gold_frame(img: Image.Image) -> None:
    """Gold scalloped frame around photo opening."""
    layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    gold = (212, 175, 72)
    gold_dark = (168, 128, 40)
    left, top, right, bottom = PHOTO["left"] - 14, PHOTO["top"] - 12, PHOTO["right"] + 14, PHOTO["bottom"] + 10
    rise = (right - left) * 0.19
    # Outer frame stroke
    for w, col in [(10, gold_dark), (6, gold)]:
        d.arc([left, top, right, top + rise * 2], 180, 0, fill=col + (255,), width=w)
        d.rectangle([left, top + rise, right, bottom], outline=col + (255,), width=w)
    # Scalloped bumps
    scallops = 11
    span = right - left
    for i in range(scallops):
        cx = left + span * (i + 0.5) / scallops
        r = span / (scallops * 2.05)
        d.arc([cx - r, top - r * 0.5, cx + r, top + r * 1.5], 200, 340, fill=gold + (255,), width=5)
    img.paste(layer, (0, 0), layer)


def marigold_garland(d: ImageDraw.ImageDraw, y: int, x0: int, x1: int, scale: float = 1.0) -> None:
    x = x0
    i = 0
    while x < x1:
        r = int((11 + (i % 4) * 3) * scale)
        col = [(245, 124, 0), (255, 193, 7), (239, 108, 0)][i % 3]
        d.ellipse([x - r, y - r, x + r, y + r], fill=col)
        d.ellipse([x - r // 2, y - r // 2, x + r // 2, y + r // 2], fill=(255, 213, 79))
        x += int(28 * scale)
        i += 1


def jasmine_garland(d: ImageDraw.ImageDraw, points: list[tuple[float, float]]) -> None:
    for x, y in points:
        for k in range(-2, 3):
            d.ellipse([x + k * 5 - 2, y - 3, x + k * 5 + 2, y + 3], fill=(255, 255, 252))


def draw_diya(d: ImageDraw.ImageDraw, cx: int, cy: int, scale: float = 1.0) -> None:
    s = scale
    d.ellipse([cx - 22 * s, cy, cx + 22 * s, cy + 14 * s], fill=(255, 193, 7))
    d.polygon(
        [(cx, cy - 28 * s), (cx - 16 * s, cy - 6 * s), (cx + 16 * s, cy - 6 * s)],
        fill=(255, 235, 150),
    )
    d.rectangle([cx - 5 * s, cy + 12 * s, cx + 5 * s, cy + 38 * s], fill=(121, 85, 72))


def draw_kalash(d: ImageDraw.ImageDraw, cx: int, base_y: int, scale: float = 1.0) -> None:
    s = scale
    d.ellipse([cx - 55 * s, base_y - 20 * s, cx + 55 * s, base_y + 8 * s], fill=(218, 165, 32))
    d.rectangle([cx - 38 * s, base_y - 78 * s, cx + 38 * s, base_y - 18 * s], fill=(255, 215, 90))
    d.ellipse([cx - 44 * s, base_y - 92 * s, cx + 44 * s, base_y - 58 * s], fill=(190, 140, 28))
    d.ellipse([cx - 16 * s, base_y - 112 * s, cx + 16 * s, base_y - 82 * s], fill=(121, 85, 55))
    d.polygon(
        [(cx, base_y - 125 * s), (cx - 28 * s, base_y - 95 * s), (cx - 4 * s, base_y - 98 * s)],
        fill=(56, 142, 60),
    )
    d.polygon(
        [(cx, base_y - 125 * s), (cx + 28 * s, base_y - 95 * s), (cx + 4 * s, base_y - 98 * s)],
        fill=(67, 160, 71),
    )


def draw_peacock(d: ImageDraw.ImageDraw, cx: int, base_y: int) -> None:
    # Body
    d.ellipse([cx - 35, base_y - 120, cx + 35, base_y - 40], fill=(25, 118, 210))
    d.ellipse([cx - 20, base_y - 145, cx + 20, base_y - 105], fill=(21, 101, 192))
    # Neck
    d.ellipse([cx - 12, base_y - 175, cx + 12, base_y - 130], fill=(13, 71, 161))
    d.ellipse([cx - 8, base_y - 195, cx + 8, base_y - 165], fill=(255, 193, 7))
    # Tail fan
    for i, ang in enumerate([2.4, 2.7, 3.0, 3.3, 3.6, 2.1, 1.8]):
        r = 130 + i * 8
        x1 = cx + math.cos(ang) * 40
        y1 = base_y - 80
        x2 = cx + math.cos(ang) * r
        y2 = base_y - 20 - i * 6
        col = [(0, 105, 92), (0, 137, 123), (46, 125, 50), (200, 230, 33)][i % 4]
        d.polygon([(x1, y1), (x2, y2), (x2 + 18, y2 + 30)], fill=col)
        d.ellipse([x2 - 14, y2 + 10, x2 + 14, y2 + 38], fill=(129, 199, 132))


def draw_rose_cluster(d: ImageDraw.ImageDraw, cx: int, cy: int, pink: bool = True) -> None:
    cols = [(236, 128, 154), (244, 167, 185), (255, 205, 210)] if pink else [(200, 230, 210), (180, 220, 190)]
    for i in range(7):
        ang = i * 1.35
        x = cx + math.cos(ang) * (22 + i * 3)
        y = cy + math.sin(ang) * (18 + i * 2)
        r = 16 - i
        d.ellipse([x - r, y - r, x + r, y + r], fill=cols[i % len(cols)])


def string_lights(d: ImageDraw.ImageDraw, y: int, x0: int, x1: int) -> None:
    x = x0
    while x < x1:
        d.ellipse([x - 5, y - 5, x + 5, y + 5], fill=(255, 240, 180))
        d.line([(x, y), (x + 18, y + 4)], fill=(180, 180, 180), width=2)
        x += 36


def geo_lantern(d: ImageDraw.ImageDraw, cx: int, cy: int) -> None:
    d.polygon([(cx, cy - 40), (cx + 34, cy - 10), (cx + 28, cy + 32), (cx - 28, cy + 32), (cx - 34, cy - 10)], fill=(212, 175, 72))
    d.rectangle([cx - 18, cy - 5, cx + 18, cy + 22], fill=(255, 248, 220))
    d.ellipse([cx - 10, cy + 2, cx + 10, cy + 18], fill=(255, 224, 130))


def banana_leaf_side(d: ImageDraw.ImageDraw, side: str) -> None:
    sign = -1 if side == "left" else 1
    edge = 0 if side == "left" else W
    pts = []
    for t in range(0, 101, 3):
        y = 60 + t * 10
        x = edge + sign * (30 + 55 * math.sin(t / 18) + t * 0.35)
        pts.append((x, y))
    pts += [(edge, H), (edge, 50)]
    d.polygon(pts, fill=(27, 94, 32))
    d.line(pts[:35], fill=(19, 78, 26), width=3)


def design_01_green_toran() -> Image.Image:
    """Traditional: cream card, banana leaf, jasmine, diyas, kalash corner."""
    img = paper_texture(vertical_gradient((W, H), (252, 248, 238), (245, 238, 225)))
    draw_center_panel(img, (253, 248, 238))
    draw_scalloped_gold_frame(img)
    d = ImageDraw.Draw(img)
    banana_leaf_side(d, "left")
    banana_leaf_side(d, "right")
    jasmine_garland(d, [(x, 88 + abs(math.sin(x / 55)) * 12) for x in range(150, W - 150, 28)])
    draw_diya(d, 175, 620, 1.1)
    draw_diya(d, W - 175, 620, 1.1)
    draw_kalash(d, 200, H - 55, 0.95)
    d.rectangle([0, DECOR_FLOOR, W, H], fill=(240, 234, 220))
    return img


def design_02_marigold_royal() -> Image.Image:
    """Traditional: maroon, heavy marigold top, hanging lamps."""
    img = vertical_gradient((W, H), (58, 12, 18), (42, 8, 14))
    draw_center_panel(img, (250, 242, 232))
    draw_scalloped_gold_frame(img)
    d = ImageDraw.Draw(img)
    marigold_garland(d, 48, 90, W - 90, 1.15)
    marigold_garland(d, 78, 95, W - 120, 1.0)
    for lx in (140, W - 140):
        d.line([(lx, 60), (lx, 200)], fill=(198, 152, 58), width=3)
        d.ellipse([lx - 16, 200, lx + 16, 232], fill=(255, 213, 79))
    # Corner plants / thali
    d.ellipse([70, H - 140, 170, H - 40], fill=(34, 100, 45))
    d.ellipse([120, H - 120, 200, H - 50], fill=(56, 130, 60))
    d.ellipse([W - 200, H - 130, W - 80, H - 35], fill=(180, 120, 50))
    d.ellipse([W - 150, H - 110, W - 90, H - 60], fill=(220, 180, 90))
    return img


def design_03_peacock_lotus() -> Image.Image:
    """Traditional: cream floral vines, peacock BR, kalash & lotus."""
    img = paper_texture(vertical_gradient((W, H), (255, 252, 247), (252, 245, 238)))
    draw_center_panel(img, (255, 251, 246))
    draw_scalloped_gold_frame(img)
    d = ImageDraw.Draw(img)
    for side in (0, W):
        for y in range(90, 420, 32):
            x = side + (18 if side == 0 else -18)
            d.ellipse([x - 10, y, x + 10, y + 18], fill=(244, 143, 177) if y % 64 else (255, 255, 255))
    draw_peacock(d, W - 175, H - 70)
    draw_kalash(d, 240, H - 60, 0.85)
    for lx in (320, 380, 440):
        d.ellipse([lx - 28, H - 75, lx + 28, H - 20], fill=(236, 128, 154))
    return img


def design_04_pink_lantern() -> Image.Image:
    """Modern: pink wash, string lights, roses, geo lanterns."""
    img = paper_texture(radial_wash((W, H), (W // 2, 380), (255, 236, 242), (252, 218, 228), 780))
    draw_center_panel(img, (255, 246, 248))
    draw_scalloped_gold_frame(img)
    d = ImageDraw.Draw(img)
    string_lights(d, 95, 130, W - 130)
    draw_rose_cluster(d, 165, H - 95, True)
    draw_rose_cluster(d, W - 165, H - 95, True)
    geo_lantern(d, 200, H - 115)
    geo_lantern(d, W - 200, H - 115)
    d.line([(140, H - 55), (W - 140, H - 55)], fill=(212, 175, 72), width=2)
    return img


def design_05_sage_botanical() -> Image.Image:
    """Modern: sage green, branches, bulbs, lantern & candle ring."""
    img = paper_texture(radial_wash((W, H), (W // 2, 400), (232, 242, 228), (210, 228, 210), 800))
    draw_center_panel(img, (238, 245, 234))
    draw_scalloped_gold_frame(img)
    d = ImageDraw.Draw(img)
    for x in range(80, W - 80, 50):
        d.line([(x, 70), (x + 20, 110)], fill=(120, 150, 100), width=3)
        d.ellipse([x + 14, 108, x + 28, 122], fill=(160, 190, 130))
    string_lights(d, 110, 100, W - 110)
    for cx in (150, W - 150):
        geo_lantern(d, cx, H - 110)
    d.ellipse([W // 2 - 55, H - 75, W // 2 + 55, H + 15], outline=(212, 175, 72), width=4)
    d.ellipse([W // 2 - 22, H - 48, W // 2 + 22, H - 8], fill=(255, 235, 160))
    draw_rose_cluster(d, 170, H - 100, False)
    draw_rose_cluster(d, W - 170, H - 100, False)
    return img


def save_design(img: Image.Image, name: str) -> Path:
    path = OUT_DIR / f"{name}_source.png"
    img.save(path, format="PNG", optimize=True)
    return path


def main() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    designs = [
        ("engagement_01", design_01_green_toran),
        ("engagement_02", design_02_marigold_royal),
        ("engagement_03", design_03_peacock_lotus),
        ("engagement_04", design_04_pink_lantern),
        ("engagement_05", design_05_sage_botanical),
    ]
    for name, fn in designs:
        print(f"Wrote {save_design(fn(), name)}")


if __name__ == "__main__":
    main()
