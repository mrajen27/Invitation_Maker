#!/usr/bin/env python3
"""
Generate portrait photo-card sources.

engagement_05 / naming_05: illustrated landscape masters (see import_05_landscape_masters.py).
naming_01: landscape compositing maintenance.

    python3 tools/import_05_landscape_masters.py   # after updating master PNGs
    python3 tools/generate_new_photo_cards.py      # rebuild _05 from backups
    python3 tools/convert_photo_backgrounds.py --prefix engagement
    python3 tools/convert_photo_backgrounds.py --prefix naming
"""

from __future__ import annotations

import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

TOOLS = Path(__file__).parent
sys.path.insert(0, str(TOOLS))

from build_portrait_sources import landscape_to_portrait, sample_cream

TARGET_W = 1080
TARGET_H = 1350
SRC = TOOLS / "bg_sources"


def sample_color(im: Image.Image, x: int, y: int) -> tuple[int, int, int]:
    patch = im.crop((x - 18, y - 18, x + 18, y + 18))
    pixels = list(
        patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata()
    )
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def add_parchment(base: Image.Image, strength: float = 0.028) -> Image.Image:
    noise = Image.effect_noise(base.size, 26).convert("L")
    noise = noise.filter(ImageFilter.GaussianBlur(radius=0.9))
    overlay = Image.merge("RGB", (noise, noise, noise))
    return Image.blend(base, overlay, strength)


def scale_width(im: Image.Image, width: int = TARGET_W) -> Image.Image:
    s = width / im.width
    return im.resize((width, max(1, int(im.height * s))), Image.Resampling.LANCZOS)


def flatten_center_panel(im: Image.Image) -> Image.Image:
    out = im.copy()
    w, h = out.size
    cream = sample_cream(out)
    ImageDraw.Draw(out).rectangle((int(w * 0.10), int(h * 0.20), int(w * 0.90), int(h * 0.76)), fill=cream)
    return out


def portrait_from_landscape_backup(stem: str) -> Image.Image:
    backup = SRC / f"{stem}_source_landscape_backup.png"
    if not backup.exists():
        raise FileNotFoundError(f"Missing landscape backup: {backup}")
    landscape = Image.open(backup).convert("RGB")
    if landscape.size != (1536, 1024):
        landscape = landscape.resize((1536, 1024), Image.Resampling.LANCZOS)
    return landscape_to_portrait(flatten_center_panel(landscape))


def build_fresh_card(
    decor_landscape: Image.Image,
    layout_ref: Image.Image,
    *,
    top_h: int,
    bottom_h: int,
    side_w: int,
    side_end_ratio: float = 0.54,
    bottom_mode: str = "landscape",
    top_crop_y: int = 0,
    cream: tuple[int, int, int] | None = None,
) -> Image.Image:
    ref = layout_ref.convert("RGB")
    scaled = scale_width(decor_landscape)
    sw, sh = scaled.size

    if cream is None:
        cream = sample_color(ref, ref.width // 2, int(ref.height * 0.42))

    canvas = add_parchment(Image.new("RGB", (TARGET_W, TARGET_H), cream))
    mid_top = top_h
    mid_bot = TARGET_H - bottom_h

    top_src = scaled.crop((0, top_crop_y, sw, top_h))
    if top_src.height < top_h:
        top_src = scaled.crop((0, 0, sw, top_h))
    canvas.paste(top_src, (0, 0))

    side_end = int(sh * side_end_ratio)
    if side_end > mid_top:
        mid_h = mid_bot - mid_top
        left = scaled.crop((0, mid_top, side_w, side_end)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        right = scaled.crop((sw - side_w, mid_top, sw, side_end)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        canvas.paste(left, (0, mid_top))
        canvas.paste(right, (TARGET_W - side_w, mid_top))

    if bottom_mode == "reference":
        canvas.paste(ref.crop((0, TARGET_H - bottom_h, TARGET_W, TARGET_H)), (0, TARGET_H - bottom_h))
    else:
        foot = scaled.crop((0, sh - bottom_h, sw, sh))
        if foot.height < bottom_h:
            foot = scaled.crop((0, sh - int(sh * 0.30), sw, sh))
        foot = foot.resize((TARGET_W, bottom_h), Image.Resampling.LANCZOS)
        canvas.paste(foot, (0, TARGET_H - bottom_h))

    return canvas


def build_engagement_05() -> Image.Image:
    return portrait_from_landscape_backup("engagement_05")


def build_naming_05() -> Image.Image:
    return portrait_from_landscape_backup("naming_05")


def build_naming_01() -> Image.Image:
    ref = Image.open(SRC / "naming_02_source.png")
    land = Image.open(SRC / "naming_01_source_landscape_backup.png").convert("RGB")
    w, h = land.size
    draw = ImageDraw.Draw(land)
    cream = (252, 244, 234)
    draw.rectangle((int(w * 0.18), int(h * 0.08), int(w * 0.82), int(h * 0.48)), fill=cream)
    return build_fresh_card(
        land,
        ref,
        top_h=168,
        bottom_h=240,
        side_w=148,
        side_end_ratio=0.48,
        bottom_mode="landscape",
        cream=cream,
    )


def main() -> int:
    jobs = [
        ("engagement_05", build_engagement_05),
        ("naming_05", build_naming_05),
    ]
    for stem, fn in jobs:
        out = fn()
        if out.size != (TARGET_W, TARGET_H):
            raise SystemExit(f"{stem} bad size {out.size}")
        path = SRC / f"{stem}_source.png"
        out.save(path, format="PNG", optimize=True)
        print(f"illustrated portrait → {path.name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
