#!/usr/bin/env python3
"""One-off art fixes on designed photo-card source PNGs before WebP conversion.

engagement_05 and naming_05 use tools/generate_portrait_05_templates.py instead.
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw

SRC = Path(__file__).parent / "bg_sources"


def sample_cream(im: Image.Image, x: int | None = None, y: int | None = None) -> tuple[int, int, int]:
    w, h = im.size
    x = w // 2 if x is None else x
    y = int(h * 0.48) if y is None else y
    patch = im.crop((x - 16, y - 16, x + 16, y + 16))
    pixels = list(
        patch.get_flattened_data() if hasattr(patch, "get_flattened_data") else patch.getdata()
    )
    pixels.sort(key=lambda c: c[0] + c[1] + c[2])
    return pixels[len(pixels) // 2]


def shrink_engagement_05_bottom(im: Image.Image, scale: float = 0.68) -> Image.Image:
    """Scale down center kalash + diyas so they do not crowd the text band."""
    w, h = im.size
    left, top, right, bottom = int(w * 0.20), int(h * 0.66), int(w * 0.80), h
    region = im.crop((left, top, right, bottom))
    rw, rh = region.size
    new_w = max(1, int(rw * scale))
    new_h = max(1, int(rh * scale))
    scaled = region.resize((new_w, new_h), Image.Resampling.LANCZOS)

    out = im.copy()
    cream = sample_cream(im, w // 2, int(h * 0.42))
    draw = ImageDraw.Draw(out)
    draw.rectangle((left, top, right, bottom), fill=cream)

    paste_x = left + (rw - new_w) // 2
    paste_y = bottom - new_h
    out.paste(scaled, (paste_x, paste_y))
    return out


def patch_naming_05(im: Image.Image) -> Image.Image:
    """Remove inner dotted divider; shift baby footprints down away from copy."""
    w, h = im.size
    out = im.copy()
    cream = sample_cream(im, w // 2, int(h * 0.50))
    draw = ImageDraw.Draw(out)

    # Dotted gold rule below the inner frame (inside cream text panel).
    draw.rectangle((int(w * 0.26), 552, int(w * 0.74), 598), fill=cream)

    # Cream/green separator: remove paired gold rules + patterned strip (keep soft color fade).
    band_top, band_bottom = 706, 742
    green = tuple(im.getpixel((w // 2, band_bottom + 36))[:3])
    for y in range(band_top, band_bottom):
        t = (y - band_top) / max(1, band_bottom - band_top - 1)
        fill = tuple(int(cream[i] * (1 - t) + green[i] * t) for i in range(3))
        draw.line([(int(w * 0.24), y), (int(w * 0.76), y)], fill=fill, width=1)

    # Baby footprints + side flourishes — move down into the green footer.
    feet_box = (int(w * 0.40), 700, int(w * 0.60), 776)
    shift_y = 62
    feet = im.crop(feet_box)
    draw.rectangle(feet_box, fill=cream)
    out.paste(feet, (feet_box[0], feet_box[1] + shift_y))
    return out


def main() -> int:
    e_src = SRC / "engagement_05_source.png"
    n_src = SRC / "naming_05_source.png"

    eng = Image.open(e_src).convert("RGB")
    shrink_engagement_05_bottom(eng).save(e_src)
    print(f"Patched {e_src.name}")

    nam = Image.open(n_src).convert("RGB")
    patch_naming_05(nam).save(n_src)
    print(f"Patched {n_src.name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
