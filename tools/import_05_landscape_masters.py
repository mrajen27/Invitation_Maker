#!/usr/bin/env python3
"""
Import illustrated landscape masters into portrait photo-card sources.

    python3 tools/import_05_landscape_masters.py
    python3 tools/convert_photo_backgrounds.py --prefix engagement
    python3 tools/convert_photo_backgrounds.py --prefix naming
"""

from __future__ import annotations

import sys
from pathlib import Path

from PIL import Image, ImageDraw

TOOLS = Path(__file__).parent
sys.path.insert(0, str(TOOLS))

from build_portrait_sources import landscape_to_portrait, sample_cream

LANDSCAPE = (1536, 1024)
SRC = TOOLS / "bg_sources"
ART = Path("/opt/cursor/artifacts/assets")

MASTERS: tuple[tuple[str, str], ...] = (
    ("engagement_05_landscape_master.png", "engagement_05"),
    ("naming_05_landscape_master.png", "naming_05"),
)


def flatten_center_panel(im: Image.Image) -> Image.Image:
    """One continuous cream field for photo + text (keeps border art intact)."""
    out = im.copy()
    w, h = out.size
    cream = sample_cream(out)
    ImageDraw.Draw(out).rectangle((int(w * 0.10), int(h * 0.20), int(w * 0.90), int(h * 0.76)), fill=cream)
    return out


def import_one(art_name: str, stem: str) -> None:
    src_path = ART / art_name
    if not src_path.exists():
        raise FileNotFoundError(f"Master art missing: {src_path}")

    landscape = Image.open(src_path).convert("RGB").resize(LANDSCAPE, Image.Resampling.LANCZOS)
    backup = SRC / f"{stem}_source_landscape_backup.png"
    landscape.save(backup, format="PNG", optimize=True)
    print(f"landscape backup → {backup.name}")

    master_copy = SRC / f"{stem}_landscape_master.png"
    landscape.save(master_copy, format="PNG", optimize=True)
    print(f"master copy → {master_copy.name}")

    prepared = flatten_center_panel(landscape)
    portrait = landscape_to_portrait(prepared)
    dest = SRC / f"{stem}_source.png"
    portrait.save(dest, format="PNG", optimize=True)
    print(f"portrait source → {dest.name} ({portrait.size[0]}×{portrait.size[1]})")


def main() -> int:
    SRC.mkdir(parents=True, exist_ok=True)
    for art, stem in MASTERS:
        import_one(art, stem)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
