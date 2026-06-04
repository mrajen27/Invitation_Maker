#!/usr/bin/env python3
"""
Five traditional 1080×1350 engagement backgrounds:
decor on top / sides / bottom, one continuous cream panel for photo + text.
No scalloped arch, no round photo ring, no line between photo and text.
"""

from __future__ import annotations

import math
import random
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

W, H = 1080, 1350
OUT_DIR = Path(__file__).resolve().parent / "bg_sources"

# Continuous cream panel (photo upper portion, text flows below on same fill)
PANEL = dict(left=128, top=96, right=W - 128, bottom=1068)
# Photo slot — plain rectangle, no inner frame stroke at bottom
PHOTO = dict(left=192, top=148, right=W - 192, bottom=538)
TEXT_FLOOR = 1068
SIDE_INSET = 128


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


def paper_texture(base: Image.Image, strength: float = 0.045) -> Image.Image:
    rng = random.Random(42)
    noise = Image.new("RGB", base.size)
    npx = noise.load()
    for y in range(base.height):
        for x in range(base.width):
            n = rng.randint(-9, 9)
            npx[x, y] = (128 + n, 128 + n, 128 + n)
    noise = noise.filter(ImageFilter.GaussianBlur(1))
    return Image.blend(base, noise, strength)


def draw_continuous_panel(img: Image.Image, fill: tuple[int, int, int]) -> None:
    """Single cream panel for photo + text — no divider line."""
    layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    d.rectangle(
        [PANEL["left"], PANEL["top"], PANEL["right"], PANEL["bottom"]],
        fill=fill + (255,),
    )
    img.paste(layer, (0, 0), layer)


def draw_panel_outer_border(img: Image.Image, color: tuple[int, int, int], width: int = 3) -> None:
    layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    d.rectangle(
        [PANEL["left"], PANEL["top"], PANEL["right"], PANEL["bottom"]],
        outline=color + (255,),
        width=width,
    )
    img.paste(layer, (0, 0), layer)


def marigold_garland(d: ImageDraw.ImageDraw, y: int, x0: int, x1: int, scale: float = 1.0) -> None:
    x = x0
    i = 0
    while x < x1:
        r = int((10 + (i % 4) * 3) * scale)
        col = [(245, 124, 0), (255, 193, 7), (239, 108, 0)][i % 3]
        d.ellipse([x - r, y - r, x + r, y + r], fill=col)
        d.ellipse([x - r // 2, y - r // 2, x + r // 2, y + r // 2], fill=(255, 213, 79))
        x += int(26 * scale)
        i += 1


def jasmine_toran(d: ImageDraw.ImageDraw, y: int) -> None:
    for x in range(160, W - 160, 24):
        d.ellipse([x - 5, y - 4, x + 5, y + 4], fill=(255, 255, 252))
        d.ellipse([x - 3, y - 2, x + 3, y + 2], fill=(255, 250, 240))


def banana_leaf_side(d: ImageDraw.ImageDraw, side: str) -> None:
    sign = -1 if side == "left" else 1
    edge = PANEL["left"] if side == "left" else PANEL["right"]
    pts = []
    for t in range(0, 95, 2):
        y = PANEL["top"] + 20 + t * 9
        x = edge + sign * (8 + 42 * math.sin(t / 14) + t * 0.28)
        pts.append((x, y))
    pts += [(edge, PANEL["bottom"]), (edge, PANEL["top"])]
    d.polygon(pts, fill=(27, 94, 32))
    d.line(pts[:40], fill=(19, 78, 26), width=2)


def draw_diya(d: ImageDraw.ImageDraw, cx: int, cy: int, scale: float = 1.0) -> None:
    s = scale
    d.ellipse([cx - 20 * s, cy, cx + 20 * s, cy + 12 * s], fill=(255, 193, 7))
    d.polygon(
        [(cx, cy - 24 * s), (cx - 14 * s, cy - 5 * s), (cx + 14 * s, cy - 5 * s)],
        fill=(255, 235, 150),
    )


def draw_kalash(d: ImageDraw.ImageDraw, cx: int, base_y: int, scale: float = 1.0) -> None:
    s = scale
    d.ellipse([cx - 50 * s, base_y - 18 * s, cx + 50 * s, base_y + 6 * s], fill=(218, 165, 32))
    d.rectangle([cx - 34 * s, base_y - 72 * s, cx + 34 * s, base_y - 16 * s], fill=(255, 215, 90))
    d.ellipse([cx - 40 * s, base_y - 86 * s, cx + 40 * s, base_y - 54 * s], fill=(190, 140, 28))
    d.ellipse([cx - 14 * s, base_y - 104 * s, cx + 14 * s, base_y - 78 * s], fill=(121, 85, 55))


def draw_peacock_side(d: ImageDraw.ImageDraw, cx: int, base_y: int) -> None:
    d.ellipse([cx - 28, base_y - 95, cx + 28, base_y - 35], fill=(25, 118, 210))
    d.ellipse([cx - 10, base_y - 125, cx + 10, base_y - 95], fill=(13, 71, 161))
    for i, ang in enumerate([2.5, 2.85, 3.2, 2.15]):
        r = 95 + i * 12
        x2 = cx + math.cos(ang) * r
        y2 = base_y - 15 - i * 8
        d.polygon(
            [(cx, base_y - 60), (x2, y2), (x2 + 12, y2 + 22)],
            fill=[(0, 105, 92), (0, 137, 123), (46, 125, 50)][i % 3],
        )


def lotus_row(d: ImageDraw.ImageDraw, y: int, x_start: int, count: int) -> None:
    for i in range(count):
        cx = x_start + i * 72
        d.ellipse([cx - 26, y, cx + 26, y + 22], fill=(236, 128, 154))
        d.ellipse([cx - 14, y - 8, cx + 14, y + 8], fill=(255, 200, 210))


def rose_corner(d: ImageDraw.ImageDraw, cx: int, cy: int) -> None:
    for i in range(5):
        ang = i * 1.4
        x = cx + math.cos(ang) * (14 + i * 2)
        y = cy + math.sin(ang) * (12 + i * 2)
        r = 12 - i
        d.ellipse([x - r, y - r, x + r, y + r], fill=[(200, 60, 90), (230, 120, 140), (255, 190, 200)][i % 3])


def mango_leaf_cluster(d: ImageDraw.ImageDraw, cx: int, cy: int) -> None:
    for i in range(4):
        ang = -0.8 + i * 0.55
        x2 = cx + math.cos(ang) * 55
        y2 = cy + math.sin(ang) * 35
        d.line([(cx, cy), (x2, y2)], fill=(46, 125, 50), width=4)
        d.ellipse([x2 - 18, y2 - 10, x2 + 18, y2 + 10], fill=(67, 160, 71))


def top_band(d: ImageDraw.ImageDraw, color: tuple[int, int, int], height: int = 88) -> None:
    d.rectangle([0, 0, W, height], fill=color)


def bottom_band(d: ImageDraw.ImageDraw, color: tuple[int, int, int], height: int = 120) -> None:
    d.rectangle([0, H - height, W, H], fill=color)


def design_01_green_toran() -> Image.Image:
    img = paper_texture(vertical_gradient((W, H), (248, 244, 232), (235, 228, 215)))
    top_band(ImageDraw.Draw(img), (34, 85, 42))
    draw_continuous_panel(img, (253, 249, 240))
    draw_panel_outer_border(img, (180, 145, 55))
    d = ImageDraw.Draw(img)
    marigold_garland(d, 52, 100, W - 100, 1.0)
    jasmine_toran(d, 78)
    banana_leaf_side(d, "left")
    banana_leaf_side(d, "right")
    draw_diya(d, 200, H - 88, 1.0)
    draw_diya(d, W - 200, H - 88, 1.0)
    draw_kalash(d, 175, H - 42, 0.9)
    draw_kalash(d, W - 175, H - 42, 0.9)
    return img


def design_02_marigold_royal() -> Image.Image:
    img = vertical_gradient((W, H), (72, 14, 22), (48, 10, 16))
    top_band(ImageDraw.Draw(img), (88, 10, 18), 95)
    draw_continuous_panel(img, (252, 244, 234))
    draw_panel_outer_border(img, (212, 175, 72), 4)
    d = ImageDraw.Draw(img)
    marigold_garland(d, 42, 85, W - 85, 1.2)
    marigold_garland(d, 68, 95, W - 110, 0.95)
    for lx in (155, W - 155):
        d.line([(lx, 50), (lx, 175)], fill=(198, 152, 58), width=3)
        d.ellipse([lx - 14, 172, lx + 14, 200], fill=(255, 213, 79))
    d.rectangle([SIDE_INSET - 8, 200, SIDE_INSET + 35, TEXT_FLOOR], fill=(120, 20, 30))
    d.rectangle([W - SIDE_INSET - 35, 200, W - SIDE_INSET + 8, TEXT_FLOOR], fill=(120, 20, 30))
    draw_kalash(d, 220, H - 48, 0.85)
    draw_kalash(d, W - 220, H - 48, 0.85)
    return img


def design_03_peacock_lotus() -> Image.Image:
    img = paper_texture(vertical_gradient((W, H), (255, 252, 247), (248, 242, 232)))
    top_band(ImageDraw.Draw(img), (120, 25, 45), 82)
    draw_continuous_panel(img, (255, 251, 246))
    draw_panel_outer_border(img, (168, 128, 40))
    d = ImageDraw.Draw(img)
    marigold_garland(d, 58, 110, W - 110, 0.85)
    for y in range(130, 480, 38):
        for x in (PANEL["left"] + 22, PANEL["right"] - 22):
            d.ellipse([x - 8, y, x + 8, y + 14], fill=(244, 143, 177) if y % 76 else (255, 248, 240))
    draw_peacock_side(d, PANEL["right"] - 55, PANEL["bottom"] - 30)
    lotus_row(d, H - 95, 280, 6)
    draw_kalash(d, 200, H - 45, 0.8)
    return img


def design_04_rose_temple() -> Image.Image:
    img = paper_texture(vertical_gradient((W, H), (255, 246, 248), (252, 235, 240)))
    top_band(ImageDraw.Draw(img), (136, 18, 38), 86)
    draw_continuous_panel(img, (255, 248, 250))
    draw_panel_outer_border(img, (180, 120, 60))
    d = ImageDraw.Draw(img)
    marigold_garland(d, 55, 105, W - 105, 0.9)
    for side in ("left", "right"):
        x = PANEL["left"] + 18 if side == "left" else PANEL["right"] - 18
        for y in range(160, 520, 45):
            d.ellipse([x - 10, y, x + 10, y + 16], fill=(200, 50, 80))
    rose_corner(d, 195, H - 95)
    rose_corner(d, W - 195, H - 95)
    lotus_row(d, H - 88, 340, 5)
    draw_diya(d, 250, H - 82, 0.95)
    draw_diya(d, W - 250, H - 82, 0.95)
    return img


def design_05_mango_gold() -> Image.Image:
    img = paper_texture(vertical_gradient((W, H), (242, 248, 236), (228, 238, 222)))
    top_band(ImageDraw.Draw(img), (46, 90, 48), 84)
    draw_continuous_panel(img, (248, 252, 242))
    draw_panel_outer_border(img, (160, 130, 50))
    d = ImageDraw.Draw(img)
    marigold_garland(d, 54, 108, W - 108, 0.88)
    mango_leaf_cluster(d, PANEL["left"] + 48, 320)
    mango_leaf_cluster(d, PANEL["right"] - 48, 320)
    mango_leaf_cluster(d, 180, H - 100)
    mango_leaf_cluster(d, W - 180, H - 100)
    draw_diya(d, W // 2 - 120, H - 85, 0.9)
    draw_diya(d, W // 2 + 120, H - 85, 0.9)
    draw_kalash(d, W // 2, H - 42, 0.75)
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
        ("engagement_04", design_04_rose_temple),
        ("engagement_05", design_05_mango_gold),
    ]
    for name, fn in designs:
        print(f"Wrote {save_design(fn(), name)}")


if __name__ == "__main__":
    main()
