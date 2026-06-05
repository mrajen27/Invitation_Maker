#!/usr/bin/env python3
"""Regenerate illustrated photo-card templates and convert to WebP."""

from __future__ import annotations

import argparse
import subprocess
import sys
from pathlib import Path

TOOLS = Path(__file__).parent
ROOT = TOOLS.parent


def run(cmd: list[str]) -> None:
    print("+", " ".join(cmd))
    subprocess.run(cmd, check=True, cwd=ROOT)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--babyshower", action="store_true")
    parser.add_argument("--cards", action="store_true", help="engagement_05, naming_01, naming_05")
    parser.add_argument("--all", action="store_true")
    args = parser.parse_args()

    if args.all or (not args.babyshower and not args.cards):
        args.babyshower = args.cards = True

    if args.cards:
        run([sys.executable, str(TOOLS / "generate_new_photo_cards.py")])
        run([sys.executable, str(TOOLS / "convert_photo_backgrounds.py"), "--prefix", "engagement"])
        run([sys.executable, str(TOOLS / "convert_photo_backgrounds.py"), "--prefix", "naming"])
    if args.babyshower:
        run([sys.executable, str(TOOLS / "generate_babyshower_templates.py")])
        run([sys.executable, str(TOOLS / "convert_photo_backgrounds.py"), "--prefix", "babyshower"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
