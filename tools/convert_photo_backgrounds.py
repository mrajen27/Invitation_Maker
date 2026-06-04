#!/usr/bin/env python3
"""Resize designed invitation PNGs to 1080x1350 WebP assets for drawable-nodpi."""

from __future__ import annotations

import argparse
from pathlib import Path

from PIL import Image

TARGET_W = 1080
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


def convert_one(source: Path, dest: Path, quality: int) -> None:
    im = Image.open(source).convert("RGB")
    im = crop_center_cover(im, TARGET_RATIO)
    im = im.resize((TARGET_W, TARGET_H), Image.Resampling.LANCZOS)
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
        convert_one(src, dest, args.quality)
        print(f"{src.name} -> {dest.name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
