#!/usr/bin/env python3
"""
Generate native 1080×1350 portrait sources for engagement_05 and naming_05.

Uses a proven portrait card (engagement_01 / naming_03) for full-bleed layout,
with décor extracted from the Mango Leaf / Tulsi Paladai landscape masters.

    python3 tools/generate_portrait_05_templates.py
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

TARGET_W = 1080
TARGET_H = 1350
SRC = Path(__file__).parent / "bg_sources"

TOP_H = 172
BOTTOM_H = 198
SIDE_W = 128
NAMING_05_TOP_H = 108


def sample_color(im: Image.Image, x: int | None = None, y: int | None = None) -> tuple[int, int, int]:
    w, h = im.size
    x = w // 2 if x is None else x
    y = int(h * 0.40) if y is None else y
    patch = im.crop((x - 20, y - 20, x + 20, y + 20))
    pixels = list(
        patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata()
    )
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def add_parchment_texture(base: Image.Image, strength: float = 0.03) -> Image.Image:
    noise = Image.effect_noise(base.size, 28).convert("L")
    noise = noise.filter(ImageFilter.GaussianBlur(radius=1.0))
    overlay = Image.merge("RGB", (noise, noise, noise))
    return Image.blend(base, overlay, strength)


def scale_to_width(im: Image.Image, width: int = TARGET_W) -> Image.Image:
    scale = width / im.width
    return im.resize((width, max(1, int(im.height * scale))), Image.Resampling.LANCZOS)


def compose_portrait(
    reference: Image.Image,
    landscape: Image.Image,
    *,
    cream_center: bool = True,
    bottom_from_reference: bool = False,
    top_h: int = TOP_H,
) -> Image.Image:
    """Full-bleed portrait: décor bands from landscape, layout from reference."""
    ref = reference.convert("RGB")
    if ref.size != (TARGET_W, TARGET_H):
        raise ValueError(f"reference must be {TARGET_W}×{TARGET_H}, got {ref.size}")

    scaled = scale_to_width(landscape)
    sw, sh = scaled.size
    side_src_end = int(sh * 0.54)

    cream = sample_color(ref, TARGET_W // 2, 520)
    canvas = add_parchment_texture(Image.new("RGB", (TARGET_W, TARGET_H), cream))

    mid_top = top_h
    mid_bottom = TARGET_H - BOTTOM_H
    mid_h = mid_bottom - mid_top

    canvas.paste(scaled.crop((0, 0, sw, top_h)), (0, 0))

    if side_src_end > top_h and mid_h > 0:
        left = scaled.crop((0, top_h, SIDE_W, side_src_end)).resize((SIDE_W, mid_h), Image.Resampling.LANCZOS)
        right = scaled.crop((sw - SIDE_W, top_h, sw, side_src_end)).resize((SIDE_W, mid_h), Image.Resampling.LANCZOS)
        canvas.paste(left, (0, mid_top))
        canvas.paste(right, (TARGET_W - SIDE_W, mid_top))

    panel_w = TARGET_W - 2 * SIDE_W
    if cream_center:
        center = add_parchment_texture(Image.new("RGB", (panel_w, mid_h), cream))
        canvas.paste(center, (SIDE_W, mid_top))
    else:
        center = ref.crop((SIDE_W, mid_top, TARGET_W - SIDE_W, mid_bottom))
        if center.size[1] != mid_h:
            center = center.resize((panel_w, mid_h), Image.Resampling.LANCZOS)
        canvas.paste(center, (SIDE_W, mid_top))

    if bottom_from_reference:
        canvas.paste(ref.crop((0, TARGET_H - BOTTOM_H, TARGET_W, TARGET_H)), (0, TARGET_H - BOTTOM_H))
    else:
        footer = scaled.crop((0, sh - int(sh * 0.28), sw, sh))
        footer = footer.resize((TARGET_W, BOTTOM_H), Image.Resampling.LANCZOS)
        canvas.paste(footer, (0, TARGET_H - BOTTOM_H))

    return canvas


def build_engagement_05() -> Image.Image:
    """Mango Leaf Gold — engagement_01 full-bleed layout + mango/marigold décor."""
    ref = Image.open(SRC / "engagement_01_source.png").convert("RGB")
    landscape = Image.open(SRC / "engagement_05_source_landscape_backup.png").convert("RGB")
    return compose_portrait(
        ref,
        landscape,
        cream_center=True,
        bottom_from_reference=True,
    )


def build_naming_05() -> Image.Image:
    """Tulsi Paladai Gold — naming_03 layout + tulsi/sage décor."""
    ref = Image.open(SRC / "naming_03_source.png").convert("RGB")
    landscape = Image.open(SRC / "naming_05_source_landscape_backup.png").convert("RGB")
    out = compose_portrait(
        ref,
        landscape,
        cream_center=True,
        bottom_from_reference=False,
        top_h=NAMING_05_TOP_H,
    )
    cream = sample_color(out, TARGET_W // 2, 500)
    mid_bottom = TARGET_H - BOTTOM_H
    draw = ImageDraw.Draw(out)
    draw.rectangle((SIDE_W - 2, NAMING_05_TOP_H - 8, TARGET_W - SIDE_W + 2, NAMING_05_TOP_H + 42), fill=cream)
    draw.rectangle((SIDE_W, mid_bottom - 10, TARGET_W - SIDE_W, mid_bottom + 8), fill=cream)
    return out


def main() -> int:
    jobs = [
        ("engagement_05", build_engagement_05),
        ("naming_05", build_naming_05),
    ]
    for stem, builder in jobs:
        backup = SRC / f"{stem}_source_landscape_backup.png"
        if not backup.exists():
            raise SystemExit(f"Missing landscape backup: {backup}")
        portrait = builder()
        dest = SRC / f"{stem}_source.png"
        portrait.save(dest, format="PNG", optimize=True)
        print(f"generated {dest.name} ({TARGET_W}×{TARGET_H})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
