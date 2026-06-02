#!/usr/bin/env python3
"""Generate vector drawable templates for engagement, naming ceremony, and baby shower."""

from __future__ import annotations

from pathlib import Path

OUT = Path(__file__).resolve().parents[1] / "app/src/main/res/drawable"


def frame(border: str, gold: str) -> str:
    return (
        f'<path android:fillColor="{border}" android:pathData="M0,0h108v16h-108zM0,119h108v16h-108z" /> '
        f'<path android:fillColor="{gold}" android:pathData="M5,5h98v125h-98zM10,10v115h88v-115z" android:fillType="evenOdd" /> '
        f'<path android:fillColor="#FFFFFF" android:pathData="M22,28h64v82h-64z" /> '
    )


def circle(cx: float, cy: float, r: float, color: str) -> str:
    return f'<path android:fillColor="{color}" android:pathData="M{cx},{cy}m-{r},0a{r},{r} 0,1 0,{2*r},0a{r},{r} 0,1 0,-{2*r},0" /> '


def oval(cx: float, cy: float, rx: float, ry: float, color: str) -> str:
    return (
        f'<path android:fillColor="{color}" android:pathData="M{cx},{cy}m-{rx},0a{rx},{ry} 0,1 0,{2*rx},0a{rx},{ry} 0,1 0,-{2*rx},0" /> '
    )


def heart(cx: float, cy: float, scale: float, color: str) -> str:
    s = scale
    return (
        f'<path android:fillColor="{color}" android:pathData="M{cx},{cy + 2 * s}'
        f"c{-3 * s},{-4 * s} {-8 * s},{-4 * s} {-8 * s},{2 * s}"
        f"c0,{5 * s} {8 * s},{9 * s} {8 * s},{9 * s}"
        f"s{8 * s},{-4 * s} {8 * s},{-9 * s}"
        f"c0,{-6 * s} {-5 * s},{-6 * s} {-8 * s},{-2 * s} z\" /> "
    )


def arch_top(color: str) -> str:
    return f'<path android:fillColor="{color}" android:pathData="M14,18 Q54,4 94,18 L94,26 Q54,14 14,26 z" /> '


def dots_row(y: float, color: str, count: int = 9) -> str:
    parts = []
    for i in range(count):
        x = 12 + i * 10.5
        parts.append(circle(x, y, 2.2, color))
    return "".join(parts)


def cloud(x: float, y: float, color: str) -> str:
    return (
        oval(x, y, 8, 5, color)
        + oval(x + 9, y + 1, 7, 4.5, color)
        + oval(x + 16, y, 6, 4, color)
    )


def star(cx: float, cy: float, color: str) -> str:
    return (
        f'<path android:fillColor="{color}" android:pathData="M{cx},{cy-4} L{cx+1.2},{cy-1} L{cx+4},{cy-1} '
        f'L{cx+1.8},{cy+0.8} L{cx+2.8},{cy+4} L{cx},{cy+2.2} L{cx-2.8},{cy+4} L{cx-1.8},{cy+0.8} L{cx-4},{cy-1} L{cx-1.2},{cy-1} z" /> '
    )


def elephant_hint(color: str) -> str:
    return (
        oval(54, 20, 12, 9, color)
        + circle(44, 22, 5, color)
        + circle(64, 22, 5, color)
        + f'<path android:fillColor="{color}" android:pathData="M48,28 Q54,34 60,28 L58,32 Q54,36 50,32 z" /> '
    )


def ring(cx: float, cy: float, color: str) -> str:
    return (
        f'<path android:fillColor="{color}" android:strokeColor="{color}" android:strokeWidth="1.5" '
        f'android:pathData="M{cx},{cy}m-6,0a6,6 0,1 0,12 0a6,6 0,1 0,-12 0" android:fillType="evenOdd" /> '
        f'<path android:fillColor="#FFFFFF" android:pathData="M{cx},{cy}m-3.5,0a3.5,3.5 0,1 0,7 0a3.5,3.5 0,1 0,-7 0" /> '
    )


TEMPLATES: list[dict] = [
    # Engagement — romantic florals & gold (inspired by sample row)
    {
        "id": "engagement_01",
        "bg": "#FFF0F3",
        "border": "#AD1457",
        "gold": "#F8BBD0",
        "accent": "#C2185B",
        "decor": lambda: (
            arch_top("#F48FB1")
            + dots_row(12, "#EC407A", 8)
            + heart(18, 108, 1.1, "#F06292")
            + heart(90, 108, 1.1, "#F06292")
            + circle(54, 22, 10, "#FCE4EC")
            + ring(54, 22, "#D81B60")
        ),
        "title": "Blush Rose Gold",
        "desc": "Soft pink frame with rose-gold ring motif for engagement invites.",
    },
    {
        "id": "engagement_02",
        "bg": "#F1F8E9",
        "border": "#2E7D32",
        "gold": "#C5E1A5",
        "accent": "#388E3C",
        "decor": lambda: (
            dots_row(11, "#66BB6A")
            + f'<path android:fillColor="#81C784" android:pathData="M12,20 L20,14 L28,20 L24,18 L24,24 L16,24 L16,18 z" /> '
            + f'<path android:fillColor="#81C784" android:pathData="M80,20 L88,14 L96,20 L92,18 L92,24 L84,24 L84,18 z" /> '
            + circle(20, 105, 4, "#A5D6A7")
            + circle(88, 105, 4, "#A5D6A7")
            + oval(54, 20, 14, 8, "#DCEDC8")
        ),
        "title": "Sage Garden",
        "desc": "Fresh green botanical frame for an elegant garden engagement.",
    },
    {
        "id": "engagement_03",
        "bg": "#E0F2F1",
        "border": "#004D40",
        "gold": "#FFB74D",
        "accent": "#00695C",
        "decor": lambda: (
            f'<path android:fillColor="#004D40" android:pathData="M0,0h108v22h-108z" /> '
            + dots_row(14, "#FFB300", 7)
            + circle(22, 18, 3, "#FFD54F")
            + circle(86, 18, 3, "#FFD54F")
            + f'<path android:fillColor="#FFB300" android:pathData="M30,16 L34,24 L38,16 zM70,16 L74,24 L78,16 z" /> '
            + heart(54, 110, 1.0, "#FF8F00")
        ),
        "title": "Lantern Glow",
        "desc": "Teal and gold festive frame inspired by evening engagement decor.",
    },
    {
        "id": "engagement_04",
        "bg": "#F3E5F5",
        "border": "#6A1B9A",
        "gold": "#CE93D8",
        "accent": "#8E24AA",
        "decor": lambda: (
            circle(16, 20, 5, "#BA68C8")
            + circle(92, 20, 5, "#BA68C8")
            + circle(24, 26, 3, "#E1BEE7")
            + circle(84, 26, 3, "#E1BEE7")
            + oval(54, 18, 16, 9, "#E1BEE7")
            + dots_row(108, "#AB47BC", 6)
        ),
        "title": "Lavender Bloom",
        "desc": "Lilac floral accents for a dreamy engagement announcement.",
    },
    {
        "id": "engagement_05",
        "bg": "#FFFDE7",
        "border": "#F9A825",
        "gold": "#FFF59D",
        "accent": "#F57F17",
        "decor": lambda: (
            arch_top("#FDD835")
            + dots_row(12, "#FBC02D", 9)
            + f'<path android:fillColor="#FBC02D" android:pathData="M18,104c4,-3 8,-3 12,0M78,104c4,-3 8,-3 12,0" /> '
            + circle(54, 20, 9, "#FFF9C4")
            + ring(54, 20, "#F9A825")
        ),
        "title": "Golden Toran",
        "desc": "Marigold-gold traditional frame for engagement celebrations.",
    },
    # Naming ceremony
    {
        "id": "naming_01",
        "bg": "#FFF8E1",
        "border": "#795548",
        "gold": "#FFCC80",
        "accent": "#6D4C41",
        "decor": lambda: (
            f'<path android:fillColor="#8D6E63" android:pathData="M40,18 Q54,10 68,18 L66,24 Q54,18 42,24 z" /> '
            + oval(54, 22, 10, 6, "#FFE0B2")
            + circle(30, 108, 3, "#A1887F")
            + circle(78, 108, 3, "#A1887F")
            + f'<path android:fillColor="#FFCC80" android:pathData="M48,106 L54,100 L60,106 z" /> '
        ),
        "title": "Cradle Blessing",
        "desc": "Warm traditional frame for naming ceremony invitations.",
    },
    {
        "id": "naming_02",
        "bg": "#E3F2FD",
        "border": "#1565C0",
        "gold": "#90CAF9",
        "accent": "#1976D2",
        "decor": lambda: (
            cloud(14, 14, "#BBDEFB")
            + cloud(68, 16, "#BBDEFB")
            + star(22, 24, "#FFD54F")
            + star(86, 24, "#FFD54F")
            + oval(54, 20, 8, 6, "#FFFFFF")
        ),
        "title": "Sky Blessing",
        "desc": "Soft blue clouds for a gentle naming day invite.",
    },
    {
        "id": "naming_03",
        "bg": "#FFF9C4",
        "border": "#F57F17",
        "gold": "#FFE082",
        "accent": "#EF6C00",
        "decor": lambda: (
            dots_row(12, "#FFB300", 8)
            + f'<path android:fillColor="#FFB300" android:pathData="M50,16 L54,22 L58,16 L54,12 z" /> '
            + circle(20, 108, 4, "#FFA000")
            + circle(88, 108, 4, "#FFA000")
            + oval(54, 22, 9, 7, "#FFECB3")
        ),
        "title": "Marigold Lamp",
        "desc": "Auspicious yellow marigold tones for traditional naming day.",
    },
    {
        "id": "naming_04",
        "bg": "#FCE4EC",
        "border": "#EC407A",
        "gold": "#F8BBD0",
        "accent": "#D81B60",
        "decor": lambda: (
            star(18, 16, "#F48FB1")
            + star(90, 16, "#F48FB1")
            + star(54, 12, "#F06292")
            + cloud(38, 18, "#F8BBD0")
            + oval(54, 22, 9, 6, "#FFFFFF")
        ),
        "title": "Dream Moon",
        "desc": "Pink starry frame for a sweet naming ceremony.",
    },
    {
        "id": "naming_05",
        "bg": "#E8F5E9",
        "border": "#388E3C",
        "gold": "#A5D6A7",
        "accent": "#2E7D32",
        "decor": lambda: (
            f'<path android:fillColor="#66BB6A" android:pathData="M54,14 m-16,0 a16,12 0,1 0,32,0 a16,12 0,1 0,-32,0" android:fillType="evenOdd" /> '
            + f'<path android:fillColor="#E8F5E9" android:pathData="M54,14 m-12,0 a12,9 0,1 0,24,0 a12,9 0,1 0,-24,0" /> '
            + circle(54, 108, 5, "#81C784")
        ),
        "title": "Fresh Wreath",
        "desc": "Minimal green wreath frame for a clean naming invite.",
    },
    # Baby shower
    {
        "id": "babyshower_01",
        "bg": "#FCE4EC",
        "border": "#EC407A",
        "gold": "#F48FB1",
        "accent": "#E91E63",
        "decor": lambda: (
            circle(20, 18, 4, "#F48FB1")
            + circle(88, 18, 4, "#F48FB1")
            + circle(16, 24, 2.5, "#F8BBD0")
            + circle(92, 24, 2.5, "#F8BBD0")
            + oval(54, 20, 12, 8, "#F8BBD0")
            + heart(54, 108, 1.2, "#EC407A")
        ),
        "title": "Pink Balloons",
        "desc": "Playful pink frame for a cheerful baby shower.",
    },
    {
        "id": "babyshower_02",
        "bg": "#E3F2FD",
        "border": "#1E88E5",
        "gold": "#90CAF9",
        "accent": "#1976D2",
        "decor": lambda: elephant_hint("#64B5F6")
            + cloud(12, 108, "#BBDEFB")
            + cloud(70, 108, "#BBDEFB"),
        "title": "Blue Elephant",
        "desc": "Soft blue frame with elephant motif for baby shower.",
    },
    {
        "id": "babyshower_03",
        "bg": "#FFF3E0",
        "border": "#8D6E63",
        "gold": "#FFCC80",
        "accent": "#6D4C41",
        "decor": lambda: (
            f'<path android:fillColor="#FFCC80" android:pathData="M40,108h6v-8h4v8h6v-8h4v8h8v6h-32z" /> '
            + circle(22, 20, 4, "#FFE0B2")
            + circle(86, 20, 4, "#FFE0B2")
            + oval(54, 22, 10, 7, "#FFFFFF")
        ),
        "title": "Teddy Blocks",
        "desc": "Neutral cream frame with playful baby shower accents.",
    },
    {
        "id": "babyshower_04",
        "bg": "#EDE7F6",
        "border": "#7E57C2",
        "gold": "#B39DDB",
        "accent": "#9C27B0",
        "decor": lambda: (
            oval(54, 18, 8, 10, "#D1C4E9")
            + circle(18, 20, 3, "#CE93D8")
            + circle(90, 20, 3, "#CE93D8")
            + star(54, 12, "#BA68C8")
            + heart(30, 108, 0.9, "#AB47BC")
            + heart(78, 108, 0.9, "#AB47BC")
        ),
        "title": "Lavender Bunny",
        "desc": "Lilac whimsical frame for a cozy baby shower.",
    },
    {
        "id": "babyshower_05",
        "bg": "#EFEBE9",
        "border": "#A1887F",
        "gold": "#D7CCC8",
        "accent": "#8D6E63",
        "decor": lambda: (
            f'<path android:fillColor="#BCAAA4" android:pathData="M20,20 Q30,8 40,20 M68,20 Q78,8 88,20" /> '
            + f'<path android:fillColor="#FFCC80" android:pathData="M48,14 Q54,6 60,14 Q54,10 48,14" /> '
            + oval(54, 22, 11, 6, "#D7CCC8")
            + dots_row(110, "#A1887F", 5)
        ),
        "title": "Boho Neutral",
        "desc": "Earthy boho frame for a modern baby shower invite.",
    },
]


def build_xml(t: dict) -> str:
    decor = t["decor"]()
    paths = (
        f'<path android:fillColor="{t["bg"]}" android:pathData="M0,0h108v135h-108z" /> '
        + frame(t["border"], t["gold"])
        + decor
    )
    return f'''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="135dp"
    android:viewportWidth="108"
    android:viewportHeight="135">
    {paths.strip()}
</vector>
'''


def kotlin_int(color_hex: str) -> str:
    c = color_hex.lstrip("#")
    return f"0xFF{c.upper()}.toInt()"


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    for t in TEMPLATES:
        path = OUT / f"template_{t['id']}.xml"
        path.write_text(build_xml(t), encoding="utf-8")
        print(f"Wrote {path.name}")


if __name__ == "__main__":
    main()
