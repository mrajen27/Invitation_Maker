#!/usr/bin/env python3
"""
Import illustrated landscape masters into portrait photo-card sources.

    python3 tools/import_05_landscape_masters.py
    python3 tools/import_05_landscape_masters.py --stem naming_05 --art naming_05_landscape_master.png
    python3 tools/convert_photo_backgrounds.py --prefix naming
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from PIL import Image, ImageDraw

TOOLS = Path(__file__).parent
sys.path.insert(0, str(TOOLS))

from build_portrait_sources import landscape_to_portrait, sample_cream

LANDSCAPE = (1536, 1024)
SRC = TOOLS / "bg_sources"
ART = Path("/opt/cursor/artifacts/assets")

DEFAULT_MASTERS: tuple[tuple[str, str], ...] = (
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


def resolve_art_path(art_name: str, art_dir: Path) -> Path:
    for candidate in (art_dir / art_name, SRC / art_name):
        if candidate.exists():
            return candidate
    raise FileNotFoundError(f"Master art missing: {art_name} (checked {art_dir} and {SRC})")


def import_one(art_name: str, stem: str, *, art_dir: Path = ART) -> None:
    src_path = resolve_art_path(art_name, art_dir)

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
    parser = argparse.ArgumentParser()
    parser.add_argument("--stem", help="Import one template stem, e.g. naming_05")
    parser.add_argument("--art", help="Master PNG filename or path")
    parser.add_argument("--art-dir", type=Path, default=ART)
    args = parser.parse_args()

    SRC.mkdir(parents=True, exist_ok=True)
    if args.stem:
        art = args.art or f"{args.stem}_landscape_master.png"
        import_one(art, args.stem, art_dir=args.art_dir)
        return 0

    for art, stem in DEFAULT_MASTERS:
        import_one(art, stem, art_dir=args.art_dir)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
