#!/usr/bin/env python3
"""Render Android vector drawables to PNG previews for documentation."""

from __future__ import annotations

import re
import xml.etree.ElementTree as ET
from pathlib import Path

import cairosvg

ANDROID_NS = "http://schemas.android.com/apk/res/android"
DRAWABLE_DIR = Path(__file__).resolve().parents[1] / "app/src/main/res/drawable"
OUT_DIR = Path("/opt/cursor/artifacts/template-previews")


def android_color(value: str | None) -> str:
    if not value:
        return "none"
    if value.startswith("@android:color/transparent"):
        return "none"
    if value.startswith("#"):
        hex_color = value[1:]
        if len(hex_color) == 8:
            alpha = int(hex_color[0:2], 16) / 255
            rgb = hex_color[2:]
            r, g, b = int(rgb[0:2], 16), int(rgb[2:4], 16), int(rgb[4:6], 16)
            return f"rgba({r},{g},{b},{alpha:.3f})"
        if len(hex_color) == 6:
            return f"#{hex_color}"
    return "#cccccc"


def vector_to_svg(xml_path: Path) -> str:
    root = ET.parse(xml_path).getroot()
    width = root.get(f"{{{ANDROID_NS}}}width", "108dp").replace("dp", "")
    height = root.get(f"{{{ANDROID_NS}}}height", "135dp").replace("dp", "")
    vw = root.get(f"{{{ANDROID_NS}}}viewportWidth", width)
    vh = root.get(f"{{{ANDROID_NS}}}viewportHeight", height)

    shapes: list[str] = []
    for element in root.iter():
        tag = element.tag.split("}")[-1]
        if tag != "path":
            continue
        path_data = element.get(f"{{{ANDROID_NS}}}pathData")
        if not path_data:
            continue
        fill = android_color(element.get(f"{{{ANDROID_NS}}}fillColor"))
        stroke = android_color(element.get(f"{{{ANDROID_NS}}}strokeColor"))
        stroke_width = element.get(f"{{{ANDROID_NS}}}strokeWidth", "0")
        fill_rule = element.get(f"{{{ANDROID_NS}}}fillType", "nonZero")
        rule = "evenodd" if fill_rule == "evenOdd" else "nonzero"
        attrs = [f'd="{path_data}"', f'fill="{fill}"', f'fill-rule="{rule}"']
        if stroke != "none" and float(stroke_width or 0) > 0:
            attrs.append(f'stroke="{stroke}"')
            attrs.append(f'stroke-width="{stroke_width}"')
        shapes.append(f"  <path {' '.join(attrs)} />")

    return f"""<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="216" height="270" viewBox="0 0 {vw} {vh}">
{chr(10).join(shapes)}
</svg>
"""


def render_all() -> list[Path]:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    outputs: list[Path] = []
    for xml_path in sorted(DRAWABLE_DIR.glob("template_*.xml")):
        svg = vector_to_svg(xml_path)
        png_path = OUT_DIR / f"{xml_path.stem}.png"
        cairosvg.svg2png(bytestring=svg.encode("utf-8"), write_to=str(png_path), scale=3)
        outputs.append(png_path)
    return outputs


if __name__ == "__main__":
    paths = render_all()
    print(f"Rendered {len(paths)} previews in {OUT_DIR}")
