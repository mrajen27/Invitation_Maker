#!/usr/bin/env python3
"""Regenerate naming_01 without inner frame — see generate_new_photo_cards.py."""

from __future__ import annotations

import generate_new_photo_cards as gen

if __name__ == "__main__":
    out = gen.build_naming_01()
    dest = gen.SRC / "naming_01_source.png"
    out.save(dest, format="PNG", optimize=True)
    print(f"regenerated {dest.name} — no top line box")
    raise SystemExit(0)
