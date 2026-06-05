#!/usr/bin/env python3
"""Deprecated wrapper — use tools/import_landscape_masters.py."""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))

from import_landscape_masters import main

if __name__ == "__main__":
    raise SystemExit(main())
