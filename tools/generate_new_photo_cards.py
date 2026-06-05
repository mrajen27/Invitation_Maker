#!/usr/bin/env python3
"""
Generate unified 1080×1350 portrait photo-card sources for _05 templates.

Each card is built from ONE landscape master only (no footer/top borrowed from
other templates — avoids the “two designs merged” seam).

    python3 tools/generate_new_photo_cards.py
    python3 tools/convert_photo_backgrounds.py --prefix engagement
    python3 tools/convert_photo_backgrounds.py --prefix naming
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

TARGET_W = 1080
TARGET_H = 1350
SRC = Path(__file__).parent / "bg_sources"

# Match tools/build_portrait_sources.py band geometry.
TOP_BAND_RATIO = 0.235
BOTTOM_BAND_RATIO = 0.275
SIDE_COL_RATIO = 0.122


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


def band_heights(scaled_h: int) -> tuple[int, int, int]:
    top_h = max(96, int(scaled_h * TOP_BAND_RATIO))
    bottom_h = max(96, int(scaled_h * BOTTOM_BAND_RATIO))
    side_w = max(72, int(TARGET_W * SIDE_COL_RATIO))
    return top_h, bottom_h, side_w


def unified_portrait_from_landscape(landscape: Image.Image) -> Image.Image:
    """
    Single-source portrait: top, stretched sides, and bottom from the same
    scaled landscape; center is flat cream (never stretched landscape middle).
    """
    scaled = scale_width(landscape)
    sw, sh = scaled.size
    top_h, bottom_h, side_w = band_heights(sh)

    cream = sample_color(scaled, sw // 2, int(sh * 0.42))
    canvas = add_parchment(Image.new("RGB", (TARGET_W, TARGET_H), cream))
    mid_top = top_h
    mid_bot = TARGET_H - bottom_h
    mid_h = mid_bot - mid_top

    canvas.paste(scaled.crop((0, 0, sw, top_h)), (0, 0))

    side_end = sh - bottom_h
    if side_end > mid_top and mid_h > 0:
        left = scaled.crop((0, mid_top, side_w, side_end)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        right = scaled.crop((sw - side_w, mid_top, sw, side_end)).resize(
            (side_w, mid_h), Image.Resampling.LANCZOS
        )
        canvas.paste(left, (0, mid_top))
        canvas.paste(right, (TARGET_W - side_w, mid_top))

    canvas.paste(scaled.crop((0, sh - bottom_h, sw, sh)).resize((TARGET_W, bottom_h), Image.Resampling.LANCZOS), (0, mid_bot))
    return canvas


def prepare_engagement_05_landscape(im: Image.Image) -> Image.Image:
    """Flatten embedded center kalash so the portrait center stays one cream field."""
    w, h = im.size
    out = im.copy()
    cream = sample_color(im, w // 2, int(h * 0.35))
    draw = ImageDraw.Draw(out)
    draw.rectangle((int(w * 0.14), int(h * 0.52), int(w * 0.86), int(h * 0.98)), fill=cream)
    return out


def prepare_naming_05_landscape(im: Image.Image) -> Image.Image:
    """Remove inner naming frame so portrait build has a clean cream panel."""
    w, h = im.size
    out = im.copy()
    cream = sample_color(im, w // 2, int(h * 0.42))
    draw = ImageDraw.Draw(out)
    draw.rectangle((int(w * 0.20), int(h * 0.10), int(w * 0.80), int(h * 0.50)), fill=cream)
    return out


def build_engagement_05() -> Image.Image:
    """Mango Leaf Gold — single mango/marigold landscape (toran, sides, kalash footer)."""
    land = prepare_engagement_05_landscape(
        Image.open(SRC / "engagement_05_source_landscape_backup.png").convert("RGB")
    )
    return unified_portrait_from_landscape(land)


def build_naming_05() -> Image.Image:
    """Tulsi Paladai Gold — single tulsi/sage landscape (toran, sides, paladai footer)."""
    land = prepare_naming_05_landscape(
        Image.open(SRC / "naming_05_source_landscape_backup.png").convert("RGB")
    )
    return unified_portrait_from_landscape(land)


def build_naming_01() -> Image.Image:
    """Jasmine Cradle Pink — single-source rebuild (optional maintenance)."""
    land = Image.open(SRC / "naming_01_source_landscape_backup.png").convert("RGB")
    w, h = land.size
    draw = ImageDraw.Draw(land)
    cream = (252, 244, 234)
    draw.rectangle((int(w * 0.18), int(h * 0.08), int(w * 0.82), int(h * 0.48)), fill=cream)
    return unified_portrait_from_landscape(land)


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
        print(f"unified design → {path.name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
