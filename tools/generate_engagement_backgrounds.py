#!/usr/bin/env python3
"""
DEPRECATED — procedural placeholders only.

Production engagement cards use **designed PNGs** in tools/bg_sources/
(engagement_01_source.png … engagement_05_source.png), converted to WebP via:

    python3 tools/convert_photo_backgrounds.py

Do not run this script for release builds unless regenerating scratch art.
"""

from __future__ import annotations

import sys

if __name__ == "__main__":
    print(
        "This procedural generator is deprecated.\n"
        "Add designed sources to tools/bg_sources/ and run convert_photo_backgrounds.py",
        file=sys.stderr,
    )
    raise SystemExit(1)
