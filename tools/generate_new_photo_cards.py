#!/usr/bin/env python3
"""
Rebuild portrait sources for engagement_05, naming_01, naming_05 from illustrated masters.

    python3 tools/regenerate_photo_cards.py
"""

from __future__ import annotations

import sys
from pathlib import Path

TOOLS = Path(__file__).parent
sys.path.insert(0, str(TOOLS))

from import_landscape_masters import import_one

TARGET_W = 1080
TARGET_H = 1350
SRC = TOOLS / "bg_sources"

CARD_STEMS = ("engagement_05", "naming_01", "naming_05")


def main() -> int:
    for stem in CARD_STEMS:
        import_one(stem)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
