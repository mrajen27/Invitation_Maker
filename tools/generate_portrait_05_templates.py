#!/usr/bin/env python3
"""
Generate native 1080×1350 portrait sources for engagement_05 and naming_05.

These two landscape masters embed a bottom kalash/footer inside the cream panel,
which breaks the generic portrait compositor. This script rebuilds them with:
  • Continuous cream center (photo + text)
  • Top toran and side borders from the designed art
  • Bottom décor confined to a single footer band

    python3 tools/generate_portrait_05_templates.py
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

TARGET_W = 1080
TARGET_H = 1350
SRC = Path(__file__).parent / "bg_sources"


def sample_color(im: Image.Image, x: int | None = None, y: int | None = None) -> tuple[int, int, int]:
    w, h = im.size
    x = w // 2 if x is None else x
    y = int(h * 0.38) if y is None else y
    patch = im.crop((x - 20, y - 20, x + 20, y + 20))
    pixels = list(
        patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata()
    )
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def add_parchment_texture(base: Image.Image, strength: float = 0.032) -> Image.Image:
    noise = Image.effect_noise(base.size, 28).convert("L")
    noise = noise.filter(ImageFilter.GaussianBlur(radius=1.0))
    overlay = Image.merge("RGB", (noise, noise, noise))
    return Image.blend(base, overlay, strength)


def soft_edge_wash(panel: Image.Image, edge_color: tuple[int, int, int], strength: float = 0.07) -> Image.Image:
    w, h = panel.size
    wash = Image.new("RGB", (w, h), edge_color)
    mask = Image.new("L", (w, h))
    draw = ImageDraw.Draw(mask)
    for x in range(w):
        t = min(x / max(1, w * 0.16), (w - 1 - x) / max(1, w * 0.16), 1.0)
        shade = int(255 * (1 - strength * (1 - t)))
        draw.line([(x, 0), (x, h)], fill=shade)
    return Image.composite(wash, panel, mask)


def scale_to_width(im: Image.Image, width: int = TARGET_W) -> Image.Image:
    scale = width / im.width
    return im.resize((width, max(1, int(im.height * scale))), Image.Resampling.LANCZOS)


def build_engagement_05(landscape: Image.Image) -> Image.Image:
    """Mango Leaf Gold — marigold toran, mango sides, kalash footer only."""
    scaled = scale_to_width(landscape)
    sw, sh = scaled.size

    top_h = 172
    bottom_h = 162
    side_w = 128
    side_src_end = int(sh * 0.56)

    cream = sample_color(scaled, sw // 2, int(sh * 0.34))
    canvas = add_parchment_texture(Image.new("RGB", (TARGET_W, TARGET_H), cream))

    mid_top = top_h
    mid_bottom = TARGET_H - bottom_h
    mid_h = mid_bottom - mid_top

    canvas.paste(scaled.crop((0, 0, sw, top_h)), (0, 0))

    if side_src_end > top_h and mid_h > 0:
        left = scaled.crop((0, top_h, side_w, side_src_end)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        right = scaled.crop((sw - side_w, top_h, sw, side_src_end)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        canvas.paste(left, (0, mid_top))
        canvas.paste(right, (TARGET_W - side_w, mid_top))

    panel_w = sw - 2 * side_w
    center = add_parchment_texture(Image.new("RGB", (panel_w, mid_h), cream))
    canvas.paste(center, (side_w, mid_top))

    # Footer: corner mango fronds + centered kalash on the same cream field.
    footer = Image.new("RGB", (TARGET_W, bottom_h), cream)
    for x0, crop_x in ((0, 0), (TARGET_W - side_w, sw - side_w)):
        corner = scaled.crop((crop_x, int(sh * 0.46), crop_x + side_w, sh))
        corner = corner.resize((side_w, bottom_h), Image.Resampling.LANCZOS)
        footer.paste(corner, (x0, 0))

    kalash = scaled.crop((int(sw * 0.31), int(sh * 0.755), int(sw * 0.69), int(sh * 0.97)))
    target_kh = int(bottom_h * 0.78)
    target_kw = max(1, int(kalash.width * target_kh / kalash.height))
    kalash = kalash.resize((target_kw, target_kh), Image.Resampling.LANCZOS)
    footer.paste(kalash, ((TARGET_W - target_kw) // 2, bottom_h - target_kh - 8))

    canvas.paste(footer, (0, TARGET_H - bottom_h))
    return canvas


def build_naming_05(landscape: Image.Image) -> Image.Image:
    """Tulsi Paladai Gold — tulsi sides, toran top, ritual footer, no inner divider."""
    scaled = scale_to_width(landscape)
    sw, sh = scaled.size

    top_h = 128
    bottom_h = 178
    side_w = 118
    side_src_end = int(sh * 0.54)

    cream = sample_color(scaled, sw // 2, int(sh * 0.40))
    sage = sample_color(scaled, 24, sh - 24)

    canvas = Image.new("RGB", (TARGET_W, TARGET_H), sage)
    canvas = add_parchment_texture(canvas, strength=0.02)

    mid_top = top_h
    mid_bottom = TARGET_H - bottom_h
    mid_h = mid_bottom - mid_top

    # Toran only — stop before the inner gold frame begins.
    canvas.paste(scaled.crop((0, 0, sw, top_h)), (0, 0))

    if side_src_end > top_h and mid_h > 0:
        left = scaled.crop((0, top_h + 8, side_w, side_src_end)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        right = scaled.crop((sw - side_w, top_h + 8, sw, side_src_end)).resize((side_w, mid_h), Image.Resampling.LANCZOS)
        canvas.paste(left, (0, mid_top))
        canvas.paste(right, (TARGET_W - side_w, mid_top))

    panel_w = sw - 2 * side_w
    center = add_parchment_texture(Image.new("RGB", (panel_w, mid_h), cream))
    canvas.paste(center, (side_w, mid_top))

    footer = scaled.crop((0, int(sh * 0.70), sw, sh))
    footer = footer.resize((TARGET_W, bottom_h), Image.Resampling.LANCZOS)
    canvas.paste(footer, (0, TARGET_H - bottom_h))

    # Remove any residual inner gold frame lines from the imported top band.
    draw = ImageDraw.Draw(canvas)
    frame_left = side_w + 6
    frame_right = TARGET_W - side_w - 6
    draw.rectangle((frame_left, top_h - 4, frame_right, top_h + 28), fill=cream)
    return canvas


def main() -> int:
    jobs = [
        ("engagement_05", build_engagement_05),
        ("naming_05", build_naming_05),
    ]
    for stem, builder in jobs:
        backup = SRC / f"{stem}_source_landscape_backup.png"
        src = SRC / f"{stem}_source.png"
        if not backup.exists():
            raise SystemExit(f"Missing landscape backup: {backup}")
        portrait = builder(Image.open(backup).convert("RGB"))
        if portrait.size != (TARGET_W, TARGET_H):
            raise SystemExit(f"{stem} wrong size: {portrait.size}")
        portrait.save(src, format="PNG", optimize=True)
        print(f"generated {src.name} ({TARGET_W}×{TARGET_H})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
