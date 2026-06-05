#!/usr/bin/env python3
"""
Deprecated — use generate_babyshower_templates.py for portrait baby-shower art.

    python3 tools/generate_babyshower_templates.py
    python3 tools/convert_photo_backgrounds.py --prefix babyshower
"""

from __future__ import annotations

import generate_babyshower_templates as gen

if __name__ == "__main__":
    raise SystemExit(gen.main())
