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

from PIL import Image, ImageDraw, ImageFilter

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


FRAMELESS_SIDE_RATIO = 0.20
# Landscape margins: keep outer décor, replace inner panel before full-height stretch.
FRAMELESS_MARGIN_X = 0.17
FRAMELESS_MARGIN_TOP = 0.13
FRAMELESS_MARGIN_BOTTOM = 0.28


def soften_center_panel(landscape: Image.Image) -> Image.Image:
    """
    On the landscape master, replace the inner photo panel with sampled cream.
    Keeps top/side/bottom décor intact; removes illustrated frame boxes.
    """
    out = landscape.copy()
    w, h = out.size
    cream = sample_cream(out)
    x0 = int(w * FRAMELESS_MARGIN_X)
    x1 = int(w * (1.0 - FRAMELESS_MARGIN_X))
    y0 = int(h * FRAMELESS_MARGIN_TOP)
    y1 = int(h * (1.0 - FRAMELESS_MARGIN_BOTTOM))
    ImageDraw.Draw(out).rectangle((x0, y0, x1, y1), fill=cream)

    # Feather left/right panel edges into décor (removes hard vertical box lines).
    feather = max(12, int(w * 0.015))
    pixels = out.load()
    for y in range(y0, y1):
        for i in range(feather):
            t = (i + 1) / feather
            lx = x0 + i
            rx = x1 - 1 - i
            if lx > 0:
                src = pixels[lx - 1, y]
                pixels[lx, y] = tuple(int(src[c] * (1.0 - t) + cream[c] * t) for c in range(3))
            if rx < w - 1:
                src = pixels[rx + 1, y]
                pixels[rx, y] = tuple(int(src[c] * (1.0 - t) + cream[c] * t) for c in range(3))
    return out


def full_stretch_portrait(landscape: Image.Image) -> Image.Image:
    """Stretch prepared landscape to 1080×1350 — one continuous image, no band seams."""
    if landscape.size == (TARGET_W, TARGET_H):
        return landscape
    return landscape.resize((TARGET_W, TARGET_H), Image.Resampling.LANCZOS)


def normalize_center_on_scaled(
    scaled: Image.Image,
    side_w: int,
    top_h: int,
    bottom_h: int,
) -> Image.Image:
    """Replace inner panel on scaled art with flat cream (removes frame lines before stretch)."""
    out = scaled.copy()
    sw, sh = out.size
    cream = sample_cream(scaled)
    ImageDraw.Draw(out).rectangle((side_w, top_h, sw - side_w, sh - bottom_h), fill=cream)
    return out


def blend_vertical_seam(
    im: Image.Image,
    x: int,
    y0: int,
    y1: int,
    *,
    half_width: int = 8,
) -> None:
    """Soften the hard edge where side décor meets the center panel."""
    pixels = im.load()
    w, h = im.size
    y_start = max(0, y0)
    y_end = min(h, y1)
    span = max(1, 2 * half_width)
    for y in range(y_start, y_end):
        for dx in range(-half_width, half_width + 1):
            px = x + dx
            if px <= 0 or px >= w - 1:
                continue
            t = (dx + half_width) / span
            left = pixels[px - 1, y]
            right = pixels[min(px + 1, w - 1), y]
            pixels[px, y] = tuple(int(left[i] * (1.0 - t) + right[i] * t) for i in range(3))


def portrait_from_scaled_bands(
    scaled: Image.Image,
    *,
    side_w: int,
    top_h: int,
    bottom_h: int,
    feather_seams: bool = False,
) -> Image.Image:
    """Compose portrait from pre-scaled landscape bands; stretch center for continuity."""
    sw, sh = scaled.size
    cream = sample_cream(scaled)
    canvas = Image.new("RGB", (TARGET_W, TARGET_H), cream)

    mid_top = top_h
    mid_bottom = TARGET_H - bottom_h
    mid_h = mid_bottom - mid_top
    center_w = sw - 2 * side_w

    if mid_h > 0 and center_w > 0:
        left = scaled.crop((0, top_h, side_w, sh - bottom_h)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        right = scaled.crop((sw - side_w, top_h, sw, sh - bottom_h)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        center = scaled.crop((side_w, top_h, sw - side_w, sh - bottom_h)).resize(
            (center_w, mid_h), Image.Resampling.LANCZOS
        )
        canvas.paste(left, (0, mid_top))
        canvas.paste(center, (side_w, mid_top))
        canvas.paste(right, (TARGET_W - side_w, mid_top))

    canvas.paste(scaled.crop((0, 0, sw, top_h)), (0, 0))
    canvas.paste(scaled.crop((0, sh - bottom_h, sw, sh)), (0, mid_bottom))

    if feather_seams and mid_h > 0:
        blend_vertical_seam(canvas, side_w, mid_top, mid_bottom)
        blend_vertical_seam(canvas, TARGET_W - side_w, mid_top, mid_bottom)

    return add_parchment_texture(canvas, strength=0.025)


def sample_color_at(im: Image.Image, x: int, y: int) -> tuple[int, int, int]:
    x = max(0, min(im.width - 1, x))
    y = max(0, min(im.height - 1, y))
    patch = im.crop((x - 10, y - 10, x + 10, y + 10))
    pixels = list(
        patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata()
    )
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def prepare_master_for_portrait(landscape: Image.Image) -> Image.Image:
    """
    Remove illustrated inner frame/box by filling with colours sampled from décor
    edges, then feathering — blends into borders instead of a flat cream slab.
    """
    out = landscape.copy()
    w, h = out.size
    x0 = int(w * FRAMELESS_MARGIN_X)
    x1 = int(w * (1.0 - FRAMELESS_MARGIN_X))
    y0 = int(h * FRAMELESS_MARGIN_TOP)
    y1 = int(h * (1.0 - FRAMELESS_MARGIN_BOTTOM))
    mid_y = (y0 + y1) // 2
    mid_x = w // 2

    left_c = sample_color_at(out, x0 - 8, mid_y)
    right_c = sample_color_at(out, x1 + 8, mid_y)
    top_c = sample_color_at(out, mid_x, y0 - 8)
    bottom_c = sample_color_at(out, mid_x, min(h - 1, y1 + 8))
    fill = tuple((left_c[i] + right_c[i] + top_c[i] + bottom_c[i]) // 4 for i in range(3))

    ImageDraw.Draw(out).rectangle((x0, y0, x1, y1), fill=fill)

    feather = max(20, int(w * 0.025))
    pixels = out.load()
    for y in range(max(0, y0 - feather), min(h, y1 + feather)):
        for i in range(feather):
            t = (i + 1) / feather
            for x in (x0 + i, x1 - 1 - i):
                if 0 <= x < w:
                    ref_x = x - 1 if x < mid_x else x + 1
                    ref_x = max(0, min(w - 1, ref_x))
                    src = pixels[ref_x, y]
                    pixels[x, y] = tuple(int(src[c] * (1.0 - t) + fill[c] * t) for c in range(3))
    return out


def needs_gold_frame_clear(landscape: Image.Image) -> bool:
    """True when master art has prominent gold inner frame lines."""
    w, h = landscape.size
    pixels = landscape.load()
    gold = 0
    for y in range(int(h * 0.18), int(h * 0.78)):
        for x in range(int(w * 0.18), int(w * 0.82)):
            r, g, b = pixels[x, y]
            if r > 175 and g > 140 and b < 120:
                gold += 1
    return gold > 400


def replace_gold_pixels_in_center(portrait: Image.Image) -> Image.Image:
    """Remove residual gold frame lines without painting a flat box."""
    out = portrait.copy()
    pixels = out.load()
    w, h = out.size
    cream = sample_cream(out)
    for y in range(int(h * 0.12), int(h * 0.86)):
        for x in range(int(w * 0.12), int(w * 0.88)):
            r, g, b = pixels[x, y]
            if r > 165 and g > 125 and b < 115:
                pixels[x, y] = cream
    return out


def smooth_center_panel(portrait: Image.Image, *, strength: str = "light") -> Image.Image:
    """Smooth inner panel texture and feather edges — removes frame lines, not décor."""
    w, h = portrait.size
    if strength == "heavy":
        x0, x1 = int(w * 0.09), int(w * 0.91)
        y0, y1 = int(h * 0.09), int(h * 0.84)
        blur = 10.0
        mask_r = 82
    else:
        x0, x1 = int(w * 0.13), int(w * 0.87)
        y0, y1 = int(h * 0.12), int(h * 0.81)
        blur = 2.8
        mask_r = 40

    region = portrait.crop((x0, y0, x1, y1))
    smooth = region.filter(ImageFilter.SMOOTH_MORE).filter(ImageFilter.GaussianBlur(radius=blur))

    fixed = portrait.copy()
    fixed.paste(smooth, (x0, y0))

    mask = Image.new("L", (w, h), 0)
    ImageDraw.Draw(mask).rectangle((x0, y0, x1, y1), fill=255)
    mask = mask.filter(ImageFilter.GaussianBlur(radius=mask_r))
    return Image.composite(fixed, portrait, mask)


# Naming cards: short flat footer so cradle/diyas never overlap copy (photo ends ~y600, text to ~1105).
NAMING_SIDE_RATIO = 0.19
NAMING_TOP_RATIO = 0.125
NAMING_BOTTOM_RATIO = 0.12


def naming_portrait_from_landscape(im: Image.Image) -> Image.Image:
    """
    Stretch the natural cream center from the master; short flat bottom band only.
    No flat rectangle overlay — center crop is stretched vertically.
    """
    w, h = im.size
    if w == TARGET_W and h == TARGET_H:
        return add_parchment_texture(im, strength=0.02)

    scale = TARGET_W / w
    scaled_h = max(1, int(h * scale))
    scaled = im.resize((TARGET_W, scaled_h), Image.Resampling.LANCZOS)

    sw, sh = scaled.size
    side_w = max(68, int(sw * NAMING_SIDE_RATIO))
    top_h = max(80, int(sh * NAMING_TOP_RATIO))
    bottom_h = max(68, int(sh * NAMING_BOTTOM_RATIO))

    out = portrait_from_scaled_bands(
        scaled,
        side_w=side_w,
        top_h=top_h,
        bottom_h=bottom_h,
        feather_seams=True,
    )
    out = smooth_center_panel(out, strength="light")
    return add_parchment_texture(out, strength=0.02)


def frameless_portrait_from_landscape(
    im: Image.Image,
    *,
    stem: str = "",
    side_col_ratio: float | None = None,
    top_band_ratio: float | None = None,
    bottom_band_ratio: float | None = None,
) -> Image.Image:
    """Portrait build — naming uses band+stretch center; others use full stretch."""
    if stem in ("naming_01", "naming_05"):
        return naming_portrait_from_landscape(im)

    _ = (side_col_ratio, top_band_ratio, bottom_band_ratio)
    stretched = full_stretch_portrait(im)
    if stem == "engagement_05":
        stretched = replace_gold_pixels_in_center(stretched)
    heavy = stem.startswith("babyshower") or stem == "engagement_05"
    blended = smooth_center_panel(stretched, strength="heavy" if heavy else "light")
    return add_parchment_texture(blended, strength=0.02)


# Back-compat alias
unified_portrait_from_landscape = frameless_portrait_from_landscape


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
