#!/usr/bin/env python3
"""
Build baby-shower photo-card source PNGs from illustrated engagement art.

Uses the same designed WebP pipeline as engagement/naming (not flat vector art).
Each template starts from a different engagement source with a distinct pastel grade.

    python3 tools/generate_babyshower_sources.py
    python3 tools/convert_photo_backgrounds.py --prefix babyshower
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageEnhance

SRC_DIR = Path(__file__).parent / "bg_sources"
OUT_SIZE = (1536, 1024)

# (output stem, engagement base, RGB multipliers, pastel overlay RGBA)
THEMES: list[tuple[str, str, tuple[float, float, float], tuple[int, int, int, int]]] = [
    ("babyshower_01", "engagement_04_source.png", (1.08, 0.92, 0.96), (255, 210, 225, 38)),
    ("babyshower_02", "engagement_03_source.png", (0.88, 0.96, 1.12), (200, 225, 255, 36)),
    ("babyshower_03", "engagement_01_source.png", (0.94, 1.06, 0.98), (230, 255, 235, 32)),
    ("babyshower_04", "engagement_02_source.png", (1.02, 0.94, 1.10), (230, 215, 255, 40)),
    ("babyshower_05", "engagement_05_source.png", (1.10, 1.02, 0.92), (255, 235, 215, 34)),
]


def grade_channel(value: float, mul: float) -> int:
    return max(0, min(255, int(value * mul)))


def pastel_grade(im: Image.Image, rgb_mul: tuple[float, float, float], overlay: tuple[int, int, int, int]) -> Image.Image:
    base = im.convert("RGB")
    pixels = base.load()
    rw, gw, bw = rgb_mul
    for y in range(base.height):
        for x in range(base.width):
            r, g, b = pixels[x, y]
            pixels[x, y] = (
                grade_channel(r, rw),
                grade_channel(g, gw),
                grade_channel(b, bw),
            )
    base = ImageEnhance.Color(base).enhance(1.06)
    base = ImageEnhance.Brightness(base).enhance(1.04)
    base = ImageEnhance.Contrast(base).enhance(0.94)
    tint = Image.new("RGBA", base.size, overlay)
    return Image.alpha_composite(base.convert("RGBA"), tint).convert("RGB")


def build_one(stem: str, base_name: str, rgb_mul: tuple[float, float, float], overlay: tuple[int, int, int, int]) -> None:
    src = SRC_DIR / base_name
    if not src.exists():
        raise FileNotFoundError(f"Missing base art: {src}")
    im = Image.open(src).convert("RGB")
    if im.size != OUT_SIZE:
        im = im.resize(OUT_SIZE, Image.Resampling.LANCZOS)
    im = pastel_grade(im, rgb_mul, overlay)
    dest = SRC_DIR / f"{stem}_source.png"
    im.save(dest, format="PNG", optimize=True)
    print(f"Wrote {dest.name} (from {base_name})")


def main() -> int:
    SRC_DIR.mkdir(parents=True, exist_ok=True)
    for stem, base, rgb_mul, overlay in THEMES:
        build_one(stem, base, rgb_mul, overlay)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
