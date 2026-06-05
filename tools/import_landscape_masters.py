#!/usr/bin/env python3
"""
Import illustrated landscape masters → 1080×1350 portrait sources.

Frameless build: décor on top/sides/bottom only; seamless cream center
(no inner rectangle, no divider, no stretched frame from master art).

    python3 tools/import_landscape_masters.py --stem naming_01
    python3 tools/import_landscape_masters.py --all-special
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from PIL import Image

TOOLS = Path(__file__).parent
sys.path.insert(0, str(TOOLS))

from build_portrait_sources import frameless_portrait_from_landscape

LANDSCAPE = (1536, 1024)
SRC = TOOLS / "bg_sources"
ART = Path("/opt/cursor/artifacts/assets")

SPECIAL_STEMS = (
    "engagement_05",
    "naming_01",
    "naming_05",
    "babyshower_01",
    "babyshower_02",
    "babyshower_03",
    "babyshower_04",
    "babyshower_05",
)


def resolve_master(stem: str, art: str | None, art_dir: Path) -> Path:
    if art:
        for candidate in (Path(art), art_dir / art, SRC / art, SRC / f"{stem}_landscape_master.png"):
            if candidate.exists():
                return candidate
        raise FileNotFoundError(f"Art not found: {art}")
    for candidate in (art_dir / f"{stem}_landscape_master.png", SRC / f"{stem}_landscape_master.png"):
        if candidate.exists():
            return candidate
    raise FileNotFoundError(f"No master for {stem}")


def import_one(stem: str, *, art: str | None = None, art_dir: Path = ART) -> None:
    master_path = resolve_master(stem, art, art_dir)
    landscape = Image.open(master_path).convert("RGB").resize(LANDSCAPE, Image.Resampling.LANCZOS)

    backup = SRC / f"{stem}_source_landscape_backup.png"
    landscape.save(backup, format="PNG", optimize=True)

    master_copy = SRC / f"{stem}_landscape_master.png"
    landscape.save(master_copy, format="PNG", optimize=True)

    portrait = frameless_portrait_from_landscape(landscape, stem=stem)
    dest = SRC / f"{stem}_source.png"
    portrait.save(dest, format="PNG", optimize=True)
    print(f"{stem}: {master_path.name} → {dest.name} ({portrait.size[0]}×{portrait.size[1]})")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--stem", action="append", help="Template stem, e.g. babyshower_03")
    parser.add_argument("--art", help="Master PNG filename or path")
    parser.add_argument("--art-dir", type=Path, default=ART)
    parser.add_argument("--all-special", action="store_true", help="Import all regenerated templates")
    args = parser.parse_args()

    SRC.mkdir(parents=True, exist_ok=True)
    stems = list(SPECIAL_STEMS) if args.all_special else (args.stem or [])
    if not stems:
        parser.error("Pass --stem or --all-special")

    for stem in stems:
        art = args.art if len(stems) == 1 else None
        import_one(stem, art=art, art_dir=args.art_dir)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
