#!/usr/bin/env python3
"""Resize designed invitation PNGs to 1080x1350 WebP assets for drawable-nodpi."""

from __future__ import annotations

import argparse
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

TARGET_W = 1080
# Cream band where the app draws event copy (below arch photo opening).
TEXT_BAND = (210, 442, 870, 1012)
TARGET_H = 1350
TARGET_RATIO = TARGET_W / TARGET_H


def sample_edge_color(im: Image.Image) -> tuple[int, int, int]:
    """Cream fill fallback (from center panel)."""
    x, y = im.width // 2, im.height // 2
    patch = im.crop((x - 24, y - 24, x + 24, y + 24))
    pixels = list(patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata())
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def stretch_band_to_height(band: Image.Image, height: int) -> Image.Image:
    """Vertically scale a decorative strip to fill a letterbox pad."""
    if height <= 0:
        return band
    if height <= band.height:
        return band.crop((0, band.height - height, band.width, band.height))
    stretched = band.resize((band.width, height), Image.Resampling.LANCZOS)
    return stretched.filter(ImageFilter.GaussianBlur(radius=0.35))


def fit_portrait_card(im: Image.Image) -> Image.Image:
    """
    Scale to full card width (keeps side border art), then fill top/bottom to 1350px
    by extending the decorative edge strips — not center-crop and not flat cream bars.
    """
    w, h = im.size
    scale = TARGET_W / w
    new_w = TARGET_W
    new_h = max(1, int(h * scale))
    scaled = im.resize((new_w, new_h), Image.Resampling.LANCZOS)

    if new_h >= TARGET_H:
        top = (new_h - TARGET_H) // 2
        return scaled.crop((0, top, TARGET_W, top + TARGET_H))

    pad_total = TARGET_H - new_h
    pad_top = pad_total // 2
    pad_bottom = pad_total - pad_top

    # Top/bottom décor bands from the scaled art (toran, florals, kalash row).
    top_band_h = max(48, int(new_h * 0.14))
    bottom_band_h = max(48, int(new_h * 0.14))
    top_band = scaled.crop((0, 0, new_w, top_band_h))
    bottom_band = scaled.crop((0, new_h - bottom_band_h, new_w, new_h))

    fill = sample_edge_color(scaled)
    out = Image.new("RGB", (TARGET_W, TARGET_H), fill)

    top_fill = stretch_band_to_height(top_band, pad_top)
    bottom_fill = stretch_band_to_height(bottom_band, pad_bottom)

    out.paste(top_fill, (0, 0))
    out.paste(scaled, (0, pad_top))
    out.paste(bottom_fill, (0, pad_top + new_h))
    return out


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
