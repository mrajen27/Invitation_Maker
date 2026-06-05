#!/usr/bin/env python3
"""Remove inner photo-placeholder frame from naming_01 before portrait conversion."""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw

from build_portrait_sources import TARGET_H, TARGET_W, add_parchment_texture, landscape_to_portrait, sample_cream

SRC = Path(__file__).parent / "bg_sources"


def clear_inner_frame(im: Image.Image) -> Image.Image:
    """Erase the light rectangular placeholder on the landscape master."""
    w, h = im.size
    out = im.copy()
    cream = sample_cream(im)
    draw = ImageDraw.Draw(out)
    # Slightly oversized to cover antialiased frame strokes.
    draw.rectangle((int(w * 0.19), int(h * 0.09), int(w * 0.81), int(h * 0.47)), fill=cream)
    return out


def touch_up_portrait(im: Image.Image) -> Image.Image:
    """Remove inner frame lines and the light seam below the toran."""
    w, h = im.size
    out = im.copy()
    cream = sample_cream(out)
    draw = ImageDraw.Draw(out)
    # Placeholder frame (landscape coords stretched into portrait center).
    draw.rectangle((int(w * 0.19), int(h * 0.11), int(w * 0.81), int(h * 0.52)), fill=cream)
    # Horizontal rule directly under the top garland.
    draw.rectangle((int(w * 0.17), int(h * 0.125), int(w * 0.83), int(h * 0.205)), fill=cream)
    # Faint vertical rules along the cream panel.
    draw.rectangle((int(w * 0.205), int(h * 0.12), int(w * 0.225), int(h * 0.82)), fill=cream)
    draw.rectangle((int(w * 0.775), int(h * 0.12), int(w * 0.795), int(h * 0.82)), fill=cream)
    return out


def main() -> int:
    backup = SRC / "naming_01_source_landscape_backup.png"
    dest = SRC / "naming_01_source.png"
    if not backup.exists():
        raise SystemExit(f"Missing {backup}")

    landscape = clear_inner_frame(Image.open(backup).convert("RGB"))
    portrait = landscape_to_portrait(landscape)
    portrait = touch_up_portrait(portrait)
    if portrait.size != (TARGET_W, TARGET_H):
        raise SystemExit(f"Unexpected size {portrait.size}")

    portrait = add_parchment_texture(portrait, strength=0.02)
    portrait.save(dest, format="PNG", optimize=True)
    print(f"patched {dest.name} — inner frame removed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
