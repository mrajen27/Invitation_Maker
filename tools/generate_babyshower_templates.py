#!/usr/bin/env python3
"""
Build baby-shower portrait sources from illustrated landscape masters.

Masters: tools/bg_sources/babyshower_XX_landscape_master.png
Import:  python3 tools/import_landscape_masters.py --stem babyshower_01
Convert: python3 tools/convert_photo_backgrounds.py --prefix babyshower

Regenerate all five:
    python3 tools/regenerate_photo_cards.py --babyshower
"""

from __future__ import annotations

import sys
from pathlib import Path

TOOLS = Path(__file__).parent
sys.path.insert(0, str(TOOLS))

from import_landscape_masters import SPECIAL_STEMS, import_one

BABY_STEMS = tuple(s for s in SPECIAL_STEMS if s.startswith("babyshower_"))


def main() -> int:
    for stem in BABY_STEMS:
        import_one(stem)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
