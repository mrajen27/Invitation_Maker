#!/usr/bin/env python3
"""
Generate fresh 1080×1350 portrait photo-card sources.

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


def sample_color(im: Image.Image, x: int, y: int) -> tuple[int, int, int]:
    patch = im.crop((x - 18, y - 18, x + 18, y + 18))
    pixels = list(
        patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata()
    )
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def sample_cream_from_ref(ref: Image.Image) -> tuple[int, int, int]:
    return sample_color(ref, ref.width // 2, int(ref.height * 0.42))


def add_parchment(base: Image.Image, strength: float = 0.028) -> Image.Image:
    noise = Image.effect_noise(base.size, 26).convert("L")
    noise = noise.filter(ImageFilter.GaussianBlur(radius=0.9))
    overlay = Image.merge("RGB", (noise, noise, noise))
    return Image.blend(base, overlay, strength)


def scale_width(im: Image.Image, width: int = TARGET_W) -> Image.Image:
    s = width / im.width
    return im.resize((width, max(1, int(im.height * s))), Image.Resampling.LANCZOS)


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
    """
    New portrait card: décor from landscape, continuous cream center (never stretched
    landscape middle — avoids frames, boxes, and seam lines).
    """
    ref = layout_ref.convert("RGB")
    scaled = scale_width(decor_landscape)
    sw, sh = scaled.size

    if cream is None:
        cream = sample_cream_from_ref(ref)

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
        foot = scaled.crop((0, sh - int(sh * 0.30), sw, sh))
        foot = foot.resize((TARGET_W, bottom_h), Image.Resampling.LANCZOS)
        canvas.paste(foot, (0, TARGET_H - bottom_h))

    return canvas


def prepare_engagement_05_landscape(im: Image.Image) -> Image.Image:
    """Remove embedded center kalash panel so portrait build stays one cream field."""
    w, h = im.size
    out = im.copy()
    cream = sample_color(im, w // 2, int(h * 0.35))
    draw = ImageDraw.Draw(out)
    draw.rectangle((int(w * 0.14), int(h * 0.52), int(w * 0.86), int(h * 0.98)), fill=cream)
    return out


def prepare_naming_05_landscape(im: Image.Image) -> Image.Image:
    w, h = im.size
    out = im.copy()
    cream = sample_color(im, w // 2, int(h * 0.42))
    draw = ImageDraw.Draw(out)
    draw.rectangle((int(w * 0.20), int(h * 0.10), int(w * 0.80), int(h * 0.50)), fill=cream)
    return out


def build_engagement_05() -> Image.Image:
    """Mango Leaf Gold v3 — mango/marigold sides & toran, rose-diya temple footer."""
    ref = Image.open(SRC / "engagement_04_source.png")
    land = prepare_engagement_05_landscape(
        Image.open(SRC / "engagement_05_source_landscape_backup.png").convert("RGB")
    )
    return build_fresh_card(
        land,
        ref,
        top_h=184,
        bottom_h=216,
        side_w=124,
        side_end_ratio=0.52,
        bottom_mode="reference",
        top_crop_y=4,
        cream=(252, 246, 236),
    )


def build_naming_05() -> Image.Image:
    """Tulsi Paladai v3 — tulsi sage sides & toran, moon-lotus celestial footer."""
    ref = Image.open(SRC / "naming_04_source.png")
    land = prepare_naming_05_landscape(
        Image.open(SRC / "naming_05_source_landscape_backup.png").convert("RGB")
    )
    return build_fresh_card(
        land,
        ref,
        top_h=128,
        bottom_h=204,
        side_w=108,
        side_end_ratio=0.52,
        bottom_mode="reference",
        cream=(252, 250, 244),
    )


def build_naming_01() -> Image.Image:
    """Jasmine Cradle Pink — continuous cream panel, jasmine/rose/cradle décor."""
    ref = Image.open(SRC / "naming_02_source.png")
    land = Image.open(SRC / "naming_01_source_landscape_backup.png").convert("RGB")
    w, h = land.size
    draw = ImageDraw.Draw(land)
    cream = (252, 244, 234)
    draw.rectangle((int(w * 0.18), int(h * 0.08), int(w * 0.82), int(h * 0.48)), fill=cream)
    out = build_fresh_card(
        land,
        ref,
        top_h=168,
        bottom_h=240,
        side_w=148,
        side_end_ratio=0.48,
        bottom_mode="landscape",
        cream=cream,
    )
    return out


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
        print(f"new design → {path.name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
