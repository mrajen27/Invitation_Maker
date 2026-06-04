#!/usr/bin/env python3
"""Resize designed invitation PNGs to 1080x1350 WebP assets for drawable-nodpi."""

from __future__ import annotations

import argparse
from pathlib import Path

from PIL import Image, ImageDraw

TARGET_W = 1080
# Cream band where the app draws event copy (below arch photo opening).
# Plain band where the app draws copy (keeps décor at top/bottom edges only).
TEXT_BAND = (210, 442, 870, 1012)
TARGET_H = 1350
TARGET_RATIO = TARGET_W / TARGET_H


def crop_center_cover(im: Image.Image, target_ratio: float) -> Image.Image:
    w, h = im.size
    current = w / h
    if current > target_ratio:
        new_w = int(h * target_ratio)
        left = (w - new_w) // 2
        return im.crop((left, 0, left + new_w, h))
    new_h = int(w / target_ratio)
    top = (h - new_h) // 2
    return im.crop((0, top, w, top + new_h))


def sample_panel_color(im: Image.Image) -> tuple[int, int, int]:
    """Pick a neutral fill from the panel beside the arch (avoids gold frame pixels)."""
    x, y = im.width // 2, int(im.height * 0.36)
    patch = im.crop((x - 20, y - 20, x + 20, y + 20))
    pixels = list(patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata())
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def clear_placeholder_text(im: Image.Image) -> Image.Image:
    """Remove AI placeholder wording so only app-rendered text appears."""
    fill = sample_panel_color(im)
    out = im.copy()
    ImageDraw.Draw(out).rectangle(TEXT_BAND, fill=fill)
    return out


def convert_one(source: Path, dest: Path, quality: int, strip_text: bool) -> None:
    im = Image.open(source).convert("RGB")
    im = crop_center_cover(im, TARGET_RATIO)
    im = im.resize((TARGET_W, TARGET_H), Image.Resampling.LANCZOS)
    if strip_text:
        im = clear_placeholder_text(im)
    dest.parent.mkdir(parents=True, exist_ok=True)
    im.save(dest, format="WEBP", quality=quality, method=6)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-dir", type=Path, default=Path(__file__).parent / "bg_sources")
    parser.add_argument(
        "--dest-dir",
        type=Path,
        default=Path(__file__).resolve().parents[1] / "app/src/main/res/drawable-nodpi",
    )
    parser.add_argument("--quality", type=int, default=88)
    parser.add_argument("--prefix", type=str, default="engagement")
    args = parser.parse_args()

    for src in sorted(args.source_dir.glob(f"{args.prefix}_*_source.png")):
        stem = src.stem.replace("_source", "")
        dest = args.dest_dir / f"bg_{stem}.webp"
        convert_one(src, dest, args.quality, strip_text=args.prefix == "engagement")
        print(f"{src.name} -> {dest.name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
