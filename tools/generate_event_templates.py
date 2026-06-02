#!/usr/bin/env python3
"""Generate engagement / naming / baby-shower templates using housewarming layout families."""

from __future__ import annotations

from pathlib import Path

DRAWABLE = Path(__file__).resolve().parents[1] / "app/src/main/res/drawable"

# Housewarming layout sources (same geometry, category-specific colours).
LAYOUT_SOURCES = {
    "classic_arch": "template_housewarming_01.xml",
    "gopuram": "template_housewarming_02.xml",
    "pillars": "template_housewarming_03.xml",
    "corner_panel": "template_housewarming_04.xml",
    "mandala": "template_housewarming_05.xml",
    "split_band": "template_housewarming_06.xml",
    "oval_frame": "template_housewarming_07.xml",
    "temple_home": "template_housewarming_08.xml",
    "leaf_garland": "template_housewarming_09.xml",
}

TEMPLATES: list[dict] = [
    # Engagement — housewarming-style frames, romantic / festive palettes
    {
        "id": "engagement_01",
        "layout": "classic_arch",
        "colors": {
            "#EFFFF3": "#FFF0F3",
            "#2E7D32": "#AD1457",
            "#FFD54F": "#FFCC80",
            "#F57C00": "#EC407A",
            "#795548": "#C2185B",
        },
        "title": "Blush Rose Gold",
        "desc": "Traditional arch frame in blush pink and gold, like grihapravesam cards.",
        "accent": "#C2185B",
    },
    {
        "id": "engagement_02",
        "layout": "gopuram",
        "colors": {
            "#F8FFF0": "#F1F8E9",
            "#33691E": "#2E7D32",
            "#FBC02D": "#A5D6A7",
            "#FFF8EA": "#FFFFFF",
        },
        "title": "Sage Garden",
        "desc": "Temple-arch housewarming layout in fresh green and cream.",
        "accent": "#388E3C",
    },
    {
        "id": "engagement_03",
        "layout": "pillars",
        "colors": {
            "#EDF7ED": "#E0F2F1",
            "#0B6B43": "#004D40",
            "#FFCA28": "#FFB74D",
            "#6D4C41": "#00695C",
        },
        "title": "Lantern Glow",
        "desc": "Pillar-bordered card in teal and marigold gold.",
        "accent": "#00695C",
    },
    {
        "id": "engagement_04",
        "layout": "corner_panel",
        "colors": {
            "#F1FFF0": "#F3E5F5",
            "#1F6F35": "#6A1B9A",
            "#F7C948": "#CE93D8",
            "#8B1E3F": "#8E24AA",
        },
        "title": "Lavender Bloom",
        "desc": "Corner-gem panel layout in lilac and gold.",
        "accent": "#8E24AA",
    },
    {
        "id": "engagement_05",
        "layout": "mandala",
        "colors": {
            "#EFFFF3": "#FFFDE7",
            "#2E7D32": "#F9A825",
            "#FFD54F": "#FFF59D",
        },
        "title": "Golden Toran",
        "desc": "Mandala frame in marigold yellow and cream.",
        "accent": "#F57F17",
    },
    # Naming ceremony
    {
        "id": "naming_01",
        "layout": "classic_arch",
        "colors": {
            "#EFFFF3": "#FFF8E1",
            "#2E7D32": "#795548",
            "#FFD54F": "#FFCC80",
            "#F57C00": "#FF9800",
            "#795548": "#6D4C41",
        },
        "title": "Cradle Blessing",
        "desc": "Warm traditional arch frame for naming day.",
        "accent": "#6D4C41",
    },
    {
        "id": "naming_02",
        "layout": "gopuram",
        "colors": {
            "#F8FFF0": "#E3F2FD",
            "#33691E": "#1565C0",
            "#FBC02D": "#90CAF9",
            "#FFF8EA": "#FFFFFF",
        },
        "title": "Sky Blessing",
        "desc": "Temple-arch layout in soft blue and white.",
        "accent": "#1976D2",
    },
    {
        "id": "naming_03",
        "layout": "split_band",
        "colors": {
            "#F8FFF0": "#FFF9C4",
            "#33691E": "#F57F17",
            "#FBC02D": "#FFE082",
            "#FFF3D0": "#FFFDE7",
            "#EF6C00": "#FFB300",
            "#8B1E3F": "#EF6C00",
        },
        "title": "Marigold Lamp",
        "desc": "Split-band auspicious layout in marigold yellow.",
        "accent": "#EF6C00",
    },
    {
        "id": "naming_04",
        "layout": "oval_frame",
        "colors": {
            "#EDF7ED": "#FCE4EC",
            "#0B6B43": "#EC407A",
            "#FFCA28": "#F8BBD0",
            "#6D4C41": "#D81B60",
        },
        "title": "Dream Moon",
        "desc": "Oval photo frame layout in gentle pink.",
        "accent": "#D81B60",
    },
    {
        "id": "naming_05",
        "layout": "leaf_garland",
        "colors": {
            "#EFFFF3": "#E8F5E9",
            "#2E7D32": "#388E3C",
            "#FFD54F": "#A5D6A7",
            "#F57C00": "#66BB6A",
            "#795548": "#2E7D32",
        },
        "title": "Fresh Wreath",
        "desc": "Garland leaf frame in fresh green and cream.",
        "accent": "#2E7D32",
    },
    # Baby shower
    {
        "id": "babyshower_01",
        "layout": "classic_arch",
        "colors": {
            "#EFFFF3": "#FCE4EC",
            "#2E7D32": "#EC407A",
            "#FFD54F": "#F48FB1",
            "#F57C00": "#F06292",
            "#795548": "#E91E63",
        },
        "title": "Pink Balloons",
        "desc": "Classic arch frame in cheerful pink and gold.",
        "accent": "#E91E63",
    },
    {
        "id": "babyshower_02",
        "layout": "gopuram",
        "colors": {
            "#F8FFF0": "#E3F2FD",
            "#33691E": "#1E88E5",
            "#FBC02D": "#90CAF9",
            "#FFF8EA": "#FFFFFF",
        },
        "title": "Blue Elephant",
        "desc": "Temple-arch layout in baby blue and cream.",
        "accent": "#1976D2",
    },
    {
        "id": "babyshower_03",
        "layout": "temple_home",
        "colors": {
            "#F1FFF0": "#FFF3E0",
            "#1F6F35": "#8D6E63",
            "#F7C948": "#FFCC80",
            "#FF9800": "#FFB74D",
        },
        "title": "Teddy Blocks",
        "desc": "Gopuram home motif in warm neutral cream.",
        "accent": "#6D4C41",
    },
    {
        "id": "babyshower_04",
        "layout": "corner_panel",
        "colors": {
            "#F1FFF0": "#EDE7F6",
            "#1F6F35": "#7E57C2",
            "#F7C948": "#B39DDB",
            "#8B1E3F": "#9C27B0",
        },
        "title": "Lavender Bunny",
        "desc": "Corner-gem panel in soft lavender.",
        "accent": "#9C27B0",
    },
    {
        "id": "babyshower_05",
        "layout": "mandala",
        "colors": {
            "#EFFFF3": "#EFEBE9",
            "#2E7D32": "#A1887F",
            "#FFD54F": "#D7CCC8",
        },
        "title": "Boho Neutral",
        "desc": "Mandala frame in earthy neutral tones.",
        "accent": "#8D6E63",
    },
]


def recolor_svg(svg: str, color_map: dict[str, str]) -> str:
    for old, new in sorted(color_map.items(), key=lambda item: -len(item[0])):
        svg = svg.replace(old, new)
    return svg


def build_template(template: dict) -> str:
    source_name = LAYOUT_SOURCES[template["layout"]]
    source_path = DRAWABLE / source_name
    if not source_path.is_file():
        raise FileNotFoundError(f"Missing layout source {source_path}")
    svg = source_path.read_text(encoding="utf-8")
    return recolor_svg(svg, template["colors"])


def kotlin_int(color_hex: str) -> str:
    return f"0xFF{color_hex.lstrip('#').upper()}.toInt()"


def main() -> None:
    for template in TEMPLATES:
        out_path = DRAWABLE / f"template_{template['id']}.xml"
        out_path.write_text(build_template(template), encoding="utf-8")
        print(f"Wrote {out_path.name} ({template['layout']})")


if __name__ == "__main__":
    main()
