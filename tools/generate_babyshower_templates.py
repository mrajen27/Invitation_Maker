#!/usr/bin/env python3
"""
Generate native 1080×1350 baby-shower photo-card sources.

Each template uses a distinct engagement landscape master with a pastel grade,
composed on a continuous cream canvas (full-bleed portrait — no letterbox).

    python3 tools/generate_babyshower_templates.py
    python3 tools/convert_photo_backgrounds.py --prefix babyshower
"""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

from PIL import Image, ImageDraw, ImageEnhance

from generate_new_photo_cards import (
    SRC,
    TARGET_H,
    TARGET_W,
    build_fresh_card,
    sample_color,
)

LANDSCAPE = (1536, 1024)


@dataclass(frozen=True)
class BabyTheme:
    stem: str
    landscape_stem: str
    layout_ref: str
    rgb_mul: tuple[float, float, float]
    overlay: tuple[int, int, int, int]
    cream: tuple[int, int, int]
    top_h: int
    bottom_h: int
    side_w: int
    bottom_mode: str
    side_end_ratio: float = 0.52
    prepare_center: bool = False


THEMES: tuple[BabyTheme, ...] = (
    BabyTheme(
        "babyshower_01",
        "engagement_04",
        "naming_01",
        (1.08, 0.92, 0.96),
        (255, 210, 225, 42),
        (255, 248, 250),
        168,
        228,
        140,
        "landscape",
    ),
    BabyTheme(
        "babyshower_02",
        "engagement_03",
        "engagement_03",
        (0.88, 0.96, 1.12),
        (200, 225, 255, 40),
        (248, 252, 255),
        172,
        198,
        128,
        "landscape",
    ),
    BabyTheme(
        "babyshower_03",
        "engagement_01",
        "engagement_01",
        (0.94, 1.06, 0.98),
        (230, 255, 235, 36),
        (252, 252, 246),
        172,
        198,
        128,
        "landscape",
    ),
    BabyTheme(
        "babyshower_04",
        "engagement_02",
        "engagement_02",
        (1.02, 0.94, 1.10),
        (230, 215, 255, 44),
        (252, 248, 255),
        178,
        210,
        132,
        "reference",
    ),
    BabyTheme(
        "babyshower_05",
        "engagement_05",
        "engagement_05",
        (1.10, 1.02, 0.92),
        (255, 235, 215, 38),
        (255, 252, 244),
        178,
        210,
        132,
        "reference",
        prepare_center=True,
    ),
)


def grade_channel(value: float, mul: float) -> int:
    return max(0, min(255, int(value * mul)))


def pastel_grade(im: Image.Image, rgb_mul: tuple[float, float, float], overlay: tuple[int, int, int, int]) -> Image.Image:
    base = im.convert("RGB")
    pixels = base.load()
    rw, gw, bw = rgb_mul
    for y in range(base.height):
        for x in range(base.width):
            r, g, b = pixels[x, y]
            pixels[x, y] = (grade_channel(r, rw), grade_channel(g, gw), grade_channel(b, bw))
    base = ImageEnhance.Color(base).enhance(1.08)
    base = ImageEnhance.Brightness(base).enhance(1.05)
    base = ImageEnhance.Contrast(base).enhance(0.92)
    tint = Image.new("RGBA", base.size, overlay)
    return Image.alpha_composite(base.convert("RGBA"), tint).convert("RGB")


def load_landscape(stem: str) -> Image.Image:
    backup = SRC / f"{stem}_source_landscape_backup.png"
    flat = SRC / f"{stem}_source.png"
    if backup.exists():
        im = Image.open(backup).convert("RGB")
    elif flat.exists() and flat.stat().st_size > 0:
        im = Image.open(flat).convert("RGB")
        if im.size != LANDSCAPE and im.size != (TARGET_W, TARGET_H):
            pass
        elif im.size == (TARGET_W, TARGET_H):
            raise FileNotFoundError(f"Need landscape backup for {stem}, only portrait found")
    else:
        raise FileNotFoundError(f"No landscape art for {stem}")
    if im.size != LANDSCAPE:
        im = im.resize(LANDSCAPE, Image.Resampling.LANCZOS)
    return im


def prepare_landscape(im: Image.Image, theme: BabyTheme) -> Image.Image:
    if not theme.prepare_center:
        return im
    out = im.copy()
    cream = sample_color(im, im.width // 2, int(im.height * 0.35))
    draw = ImageDraw.Draw(out)
    draw.rectangle((int(im.width * 0.14), int(im.height * 0.52), int(im.width * 0.86), im.height), fill=cream)
    return out


def build_one(theme: BabyTheme) -> Image.Image:
    landscape = pastel_grade(load_landscape(theme.landscape_stem), theme.rgb_mul, theme.overlay)
    landscape = prepare_landscape(landscape, theme)
    ref_path = SRC / f"{theme.layout_ref}_source.png"
    if not ref_path.exists():
        raise FileNotFoundError(f"Layout reference missing: {ref_path}")
    ref = Image.open(ref_path).convert("RGB")
    return build_fresh_card(
        landscape,
        ref,
        top_h=theme.top_h,
        bottom_h=theme.bottom_h,
        side_w=theme.side_w,
        side_end_ratio=theme.side_end_ratio,
        bottom_mode=theme.bottom_mode,
        cream=theme.cream,
    )


def main() -> int:
    SRC.mkdir(parents=True, exist_ok=True)
    for theme in THEMES:
        out = build_one(theme)
        dest = SRC / f"{theme.stem}_source.png"
        out.save(dest, format="PNG", optimize=True)
        print(f"baby shower → {dest.name} ({TARGET_W}×{TARGET_H})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
