#!/usr/bin/env python3
"""Resize designed invitation PNGs to 1080x1350 WebP assets for drawable-nodpi."""

from __future__ import annotations

import argparse
from pathlib import Path

from PIL import Image, ImageDraw

TARGET_W = 1080
TEXT_BAND = (210, 442, 870, 1012)
TARGET_H = 1350


def sample_edge_color(im: Image.Image) -> tuple[int, int, int]:
    """Cream fill for vertical letterbox pads (from center panel)."""
    x, y = im.width // 2, im.height // 2
    patch = im.crop((x - 24, y - 24, x + 24, y + 24))
    pixels = list(patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata())
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def fit_portrait_card(im: Image.Image) -> Image.Image:
    """
    Scale landscape art to full card width (side borders stay visible).
    Letterbox top/bottom with cream sampled from the panel — no crop, no stretch.
    """
    w, h = im.size
    scale = TARGET_W / w
    new_w = TARGET_W
    new_h = max(1, int(h * scale))
    scaled = im.resize((new_w, new_h), Image.Resampling.LANCZOS)

    if new_h >= TARGET_H:
        top = (new_h - TARGET_H) // 2
        return scaled.crop((0, top, TARGET_W, top + TARGET_H))

    fill = sample_edge_color(scaled)
    out = Image.new("RGB", (TARGET_W, TARGET_H), fill)
    out.paste(scaled, (0, (TARGET_H - new_h) // 2))
    return out


def sample_panel_color(im: Image.Image) -> tuple[int, int, int]:
    x, y = im.width // 2, int(im.height * 0.36)
    patch = im.crop((x - 20, y - 20, x + 20, y + 20))
    pixels = list(patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata())
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def clear_placeholder_text(im: Image.Image) -> Image.Image:
    fill = sample_panel_color(im)
    out = im.copy()
    ImageDraw.Draw(out).rectangle(TEXT_BAND, fill=fill)
    return out


def convert_one(source: Path, dest: Path, quality: int, strip_text: bool) -> None:
    im = Image.open(source).convert("RGB")
    im = fit_portrait_card(im)
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
        convert_one(src, dest, args.quality, strip_text=False)
        print(f"{src.name} -> {dest.name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
