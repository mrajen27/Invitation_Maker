#!/usr/bin/env python3
"""Procedural 1080×1350 photo-card art for engagement_05 and naming_05 (scratch designs)."""

from __future__ import annotations

import math
from typing import Sequence

from PIL import Image, ImageDraw, ImageFilter

TARGET_W = 1080
TARGET_H = 1350


def blend(a: tuple[int, int, int], b: tuple[int, int, int], t: float) -> tuple[int, int, int]:
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def vertical_gradient(size: tuple[int, int], top: tuple[int, int, int], bottom: tuple[int, int, int]) -> Image.Image:
    w, h = size
    img = Image.new("RGB", (w, h))
    px = img.load()
    for y in range(h):
        c = blend(top, bottom, y / max(1, h - 1))
        for x in range(w):
            px[x, y] = c
    return img


def add_texture(base: Image.Image, strength: float = 0.03) -> Image.Image:
    noise = Image.effect_noise(base.size, 28).convert("L")
    noise = noise.filter(ImageFilter.GaussianBlur(radius=1.0))
    overlay = Image.merge("RGB", (noise, noise, noise))
    return Image.blend(base, overlay, strength)


def draw_marigold(draw: ImageDraw.ImageDraw, cx: int, cy: int, r: int, petals: tuple[int, int, int], core: tuple[int, int, int]) -> None:
    for i in range(10):
        ang = i * math.tau / 10
        ex = cx + int(math.cos(ang) * r)
        ey = cy + int(math.sin(ang) * r * 0.72)
        draw.ellipse((ex - r // 2, ey - r // 3, ex + r // 2, ey + r // 3), fill=petals)
    draw.ellipse((cx - r // 3, cy - r // 3, cx + r // 3, cy + r // 3), fill=core)


def draw_lotus(draw: ImageDraw.ImageDraw, cx: int, cy: int, scale: float, fill: tuple[int, int, int]) -> None:
    r = int(14 * scale)
    for i in range(6):
        ang = math.pi / 2 + i * math.tau / 6
        ex = cx + int(math.cos(ang) * r * 1.4)
        ey = cy + int(math.sin(ang) * r)
        draw.pieslice((ex - r, ey - r, ex + r, ey + r), 200, 340, fill=fill)
    draw.ellipse((cx - r // 2, cy - r // 3, cx + r // 2, cy + r // 3), fill=blend(fill, (255, 220, 120), 0.35))


def draw_diya(draw: ImageDraw.ImageDraw, cx: int, cy: int, bowl: tuple[int, int, int], flame: tuple[int, int, int]) -> None:
    draw.pieslice((cx - 18, cy - 6, cx + 18, cy + 14), 0, 180, fill=bowl)
    draw.polygon([(cx, cy - 22), (cx - 7, cy - 8), (cx + 7, cy - 8)], fill=flame)
    draw.ellipse((cx - 4, cy - 26, cx + 4, cy - 18), fill=(255, 243, 180))


def draw_kundan_dots(draw: ImageDraw.ImageDraw, x0: int, y0: int, y1: int, x1: int, gold: tuple[int, int, int], step: int = 28) -> None:
    y = y0
    toggle = False
    while y < y1:
        for x in range(x0 + 8, x1 - 8, step):
            r = 5 if toggle else 4
            draw.ellipse((x - r, y - r, x + r, y + r), fill=gold, outline=(120, 90, 20))
        y += step
        toggle = not toggle


def draw_paisley_column(
    draw: ImageDraw.ImageDraw,
    x_center: int,
    y0: int,
    y1: int,
    body: tuple[int, int, int],
    gold: tuple[int, int, int],
) -> None:
    y = y0 + 20
    while y < y1 - 40:
        draw.ellipse((x_center - 22, y - 30, x_center + 22, y + 30), fill=body, outline=gold, width=2)
        draw.arc((x_center - 10, y - 8, x_center + 26, y + 24), 200, 320, fill=gold, width=2)
        y += 58


def draw_toran_chain(draw: ImageDraw.ImageDraw, y: int, colors: Sequence[tuple[int, int, int]]) -> None:
    x = 40
    i = 0
    while x < TARGET_W - 40:
        c = colors[i % len(colors)]
        draw_marigold(draw, x, y, 16, c, (255, 200, 60))
        if i % 2 == 0:
            draw.line([(x + 18, y), (x + 34, y + 6)], fill=(180, 140, 40), width=3)
        x += 36
        i += 1


def build_engagement_05() -> Image.Image:
    """Crimson Kundan Glow — ruby sides, gold kundan, rose-lotus toran (procedural)."""
    cream = (255, 251, 245)
    crimson = (107, 24, 48)
    crimson_deep = (72, 12, 32)
    gold = (212, 175, 55)
    blush = (232, 160, 168)
    petal = (255, 120, 90)

    canvas = Image.new("RGB", (TARGET_W, TARGET_H), cream)
    top_h, bottom_h, side_w = 178, 208, 128

    top = vertical_gradient((TARGET_W, top_h), crimson, crimson_deep)
    tdraw = ImageDraw.Draw(top)
    draw_toran_chain(tdraw, 52, (petal, (255, 193, 7), blush))
    for cx in (120, TARGET_W - 120):
        draw_lotus(tdraw, cx, 118, 1.1, blush)
    top = add_texture(top, 0.025)
    canvas.paste(top, (0, 0))

    side_h = TARGET_H - top_h - bottom_h
    side = vertical_gradient((side_w, side_h), crimson_deep, crimson)
    sdraw = ImageDraw.Draw(side)
    draw_kundan_dots(sdraw, 0, 0, side_h, side_w, gold)
    draw_paisley_column(sdraw, side_w // 2, 0, side_h, crimson, gold)
    canvas.paste(side, (0, top_h))
    canvas.paste(side.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (TARGET_W - side_w, top_h))

    bottom = vertical_gradient((TARGET_W, bottom_h), crimson_deep, crimson)
    bdraw = ImageDraw.Draw(bottom)
    bdraw.line([(0, 18), (TARGET_W, 18)], fill=gold, width=3)
    spacing = TARGET_W // 8
    for i in range(7):
        draw_diya(bdraw, spacing * (i + 1), 118, gold, (255, 200, 80))
    for cx in (90, TARGET_W - 90):
        draw_lotus(bdraw, cx, 52, 0.9, blush)
    bottom = add_texture(bottom, 0.025)
    canvas.paste(bottom, (0, TARGET_H - bottom_h))

    return add_texture(canvas, 0.02)


def draw_footprint_pair(draw: ImageDraw.ImageDraw, cx: int, cy: int, gold: tuple[int, int, int]) -> None:
    for dx in (-22, 22):
        draw.ellipse((cx + dx - 10, cy - 14, cx + dx + 10, cy + 6), fill=gold)
        for toe, off in enumerate((-8, -2, 4, 10)):
            draw.ellipse((cx + dx + off - 3, cy - 26 - toe * 2, cx + dx + off + 3, cy - 18 - toe * 2), fill=gold)


def draw_om_motif(draw: ImageDraw.ImageDraw, cx: int, cy: int, r: int, stroke: tuple[int, int, int]) -> None:
    draw.arc((cx - r, cy - r, cx + r, cy + r), 200, 340, fill=stroke, width=3)
    draw.arc((cx - r // 2, cy - r // 3, cx + r // 2, cy + r // 2), 20, 200, fill=stroke, width=3)
    draw.ellipse((cx - 4, cy + r // 3, cx + 4, cy + r // 3 + 8), fill=stroke)


def build_naming_05() -> Image.Image:
    """Sunset Vermillion Blessing — coral toran, vermillion sides, golden footprints (procedural)."""
    cream = (255, 252, 247)
    vermilion = (198, 40, 40)
    vermilion_deep = (142, 22, 28)
    saffron = (255, 152, 0)
    gold = (255, 193, 7)
    marigold_p = (255, 143, 0)

    canvas = Image.new("RGB", (TARGET_W, TARGET_H), cream)
    top_h, bottom_h, side_w = 168, 200, 118

    top = vertical_gradient((TARGET_W, top_h), vermilion, vermilion_deep)
    tdraw = ImageDraw.Draw(top)
    draw_toran_chain(tdraw, 48, (marigold_p, saffron, (255, 214, 120)))
    tdraw.line([(60, 130), (TARGET_W - 60, 130)], fill=gold, width=2)
    for cx in range(100, TARGET_W - 80, 140):
        draw_om_motif(tdraw, cx, 108, 22, gold)
    top = add_texture(top, 0.025)
    canvas.paste(top, (0, 0))

    side = vertical_gradient((side_w, TARGET_H - top_h - bottom_h), vermilion_deep, vermilion)
    sdraw = ImageDraw.Draw(side)
    y = 24
    while y < TARGET_H - top_h - bottom_h - 30:
        draw_marigold(sdraw, side_w // 2, y, 12, marigold_p, gold)
        sdraw.line([(8, y + 18), (side_w - 8, y + 18)], fill=gold, width=2)
        y += 52
    canvas.paste(side, (0, top_h))
    canvas.paste(side.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (TARGET_W - side_w, top_h))

    bottom = vertical_gradient((TARGET_W, bottom_h), vermilion_deep, vermilion)
    bdraw = ImageDraw.Draw(bottom)
    bdraw.rectangle((TARGET_W // 2 - 70, 36, TARGET_W // 2 + 70, 120), fill=saffron, outline=gold, width=2)
    bdraw.ellipse((TARGET_W // 2 - 24, 20, TARGET_W // 2 + 24, 52), fill=gold)
    draw_footprint_pair(bdraw, TARGET_W // 2, 150, gold)
    for cx in (140, TARGET_W - 140):
        draw_marigold(bdraw, cx, 70, 14, marigold_p, gold)
    bottom = add_texture(bottom, 0.025)
    canvas.paste(bottom, (0, TARGET_H - bottom_h))

    return add_texture(canvas, 0.02)
