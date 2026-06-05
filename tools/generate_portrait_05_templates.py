#!/usr/bin/env python3
"""Regenerate engagement_05 and naming_05 — see generate_new_photo_cards.py."""

from __future__ import annotations

import generate_new_photo_cards as gen

TARGET_W = gen.TARGET_W
TARGET_H = gen.TARGET_H
SRC = gen.SRC


def main() -> int:
    for name, builder in (
        ("engagement_05", gen.build_engagement_05),
        ("naming_05", gen.build_naming_05),
    ):
        img = builder()
        dest = SRC / f"{name}_source.png"
        img.save(dest, format="PNG", optimize=True)
        print(f"generated {dest.name} ({TARGET_W}×{TARGET_H})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
