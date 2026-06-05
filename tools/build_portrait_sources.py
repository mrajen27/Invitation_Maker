#!/usr/bin/env python3
"""
Build native 1080×1350 portrait source PNGs from landscape designed art.

Fills the portrait canvas without letterboxing or mirrored décor seams:
  • Top toran / bottom lotus bands keep their designed height
  • Side borders stretch vertically (leaf/filigree patterns)
  • Center cream panel expands for photo + copy

    python3 tools/build_portrait_sources.py --prefix engagement --restore-landscape
    python3 tools/build_portrait_sources.py --prefix naming --restore-landscape
"""

from __future__ import annotations

import argparse
from pathlib import Path

from PIL import Image, ImageFilter

TARGET_W = 1080
TARGET_H = 1350

# Measured from designed landscape cards (1536×1024 → width-fit 1080×720).
TOP_BAND_RATIO = 0.235
BOTTOM_BAND_RATIO = 0.275
SIDE_COL_RATIO = 0.122


def sample_cream(im: Image.Image) -> tuple[int, int, int]:
    w, h = im.size
    patch = im.crop((w // 2 - 24, h // 2 - 24, w // 2 + 24, h // 2 + 24))
    pixels = list(
        patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata()
    )
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def add_parchment_texture(base: Image.Image, strength: float = 0.035) -> Image.Image:
    noise = Image.effect_noise(base.size, 32).convert("L")
    noise = noise.filter(ImageFilter.GaussianBlur(radius=1.2))
    overlay = Image.merge("RGB", (noise, noise, noise))
    return Image.blend(base, overlay, strength)


def landscape_to_portrait(im: Image.Image) -> Image.Image:
    w, h = im.size
    if w == TARGET_W and h == TARGET_H:
        return im

    scale = TARGET_W / w
    scaled_h = max(1, int(h * scale))
    scaled = im.resize((TARGET_W, scaled_h), Image.Resampling.LANCZOS)

    if scaled.height >= TARGET_H:
        top = (scaled.height - TARGET_H) // 2
        return scaled.crop((0, top, TARGET_W, top + TARGET_H))

    sw, sh = scaled.size
    side_w = max(72, int(sw * SIDE_COL_RATIO))
    top_h = max(96, int(sh * TOP_BAND_RATIO))
    bottom_h = max(96, int(sh * BOTTOM_BAND_RATIO))

    cream = sample_cream(scaled)
    canvas = Image.new("RGB", (TARGET_W, TARGET_H), cream)
    canvas = add_parchment_texture(canvas)

    mid_top = top_h
    mid_bottom = TARGET_H - bottom_h
    mid_h = mid_bottom - mid_top

    left_src = scaled.crop((0, top_h, side_w, sh - bottom_h))
    right_src = scaled.crop((sw - side_w, top_h, sw, sh - bottom_h))
    if left_src.height > 0 and mid_h > 0:
        left_strip = left_src.resize((side_w, mid_h), Image.Resampling.LANCZOS)
        right_strip = right_src.resize((side_w, mid_h), Image.Resampling.LANCZOS)
        canvas.paste(left_strip, (0, mid_top))
        canvas.paste(right_strip, (TARGET_W - side_w, mid_top))

    top_band = scaled.crop((0, 0, sw, top_h))
    canvas.paste(top_band, (0, 0))

    bottom_band = scaled.crop((0, sh - bottom_h, sw, sh))
    canvas.paste(bottom_band, (0, TARGET_H - bottom_h))
    center_src = scaled.crop((side_w, top_h, sw - side_w, sh - bottom_h))
    if center_src.height > 0 and mid_h > 0:
        center = center_src.resize((sw - 2 * side_w, mid_h), Image.Resampling.LANCZOS)
        canvas.paste(center, (side_w, mid_top))

    return canvas


def restore_from_backup(src: Path) -> bool:
    backup = src.with_name(src.stem + "_landscape_backup.png")
    if not backup.exists():
        return False
    Image.open(backup).convert("RGB").save(src, format="PNG", optimize=True)
    print(f"restore {src.name} from {backup.name}")
    return True


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-dir", type=Path, default=Path(__file__).parent / "bg_sources")
    parser.add_argument("--prefix", type=str, required=True)
    parser.add_argument(
        "--backup-landscape",
        action="store_true",
        help="Save original landscape file as *_landscape_backup.png before overwriting",
    )
    parser.add_argument(
        "--restore-landscape",
        action="store_true",
        help="Rebuild from *_landscape_backup.png when present",
    )
    args = parser.parse_args()

    for src in sorted(args.source_dir.glob(f"{args.prefix}_*_source.png")):
        if "_landscape_backup" in src.name:
            continue
        if src.name.endswith("_05_source.png"):
            print(f"skip {src.name} (use generate_portrait_05_templates.py)")
            continue

        if args.restore_landscape:
            restore_from_backup(src)

        im = Image.open(src).convert("RGB")
        if im.size == (TARGET_W, TARGET_H) and not args.restore_landscape:
            print(f"skip {src.name} (already {TARGET_W}×{TARGET_H})")
            continue

        if args.backup_landscape and im.size[0] > im.size[1]:
            backup = src.with_name(src.stem + "_landscape_backup.png")
            if not backup.exists():
                im.save(backup, format="PNG", optimize=True)
                print(f"backup {backup.name}")
            im = Image.open(backup if backup.exists() else src).convert("RGB")

        portrait = landscape_to_portrait(im)
        portrait.save(src, format="PNG", optimize=True)
        print(f"portrait {src.name} ({portrait.size[0]}×{portrait.size[1]})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
