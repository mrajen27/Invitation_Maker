package com.vaangainvite.core.image

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Paints full-bleed invitation backgrounds inspired by premium printable card designs.
 */
object InvitationBackgroundPainter {
    fun draw(canvas: Canvas, templateId: String, width: Int, height: Int) {
        when (templateId) {
            "birthday_01" -> drawBirthdayPinkGold(canvas, width, height)
            "birthday_02" -> drawBirthdayNavyGold(canvas, width, height)
            "birthday_03" -> drawBirthdayPlayful(canvas, width, height)
            "birthday_04" -> drawBirthdayFloral(canvas, width, height)
            "birthday_05" -> drawBirthdayPurpleParty(canvas, width, height)
            in weddingIds -> drawWeddingTheme(canvas, width, height, templateId)
            in housewarmingIds -> drawHousewarmingTheme(canvas, width, height, templateId)
            in pubertyIds -> drawPubertyTheme(canvas, width, height, templateId)
            else -> drawDefaultWarm(canvas, width, height)
        }
    }

    private val weddingIds = setOf(
        "wedding_01", "wedding_02", "wedding_03", "wedding_04", "wedding_05"
    )
    private val housewarmingIds = setOf(
        "housewarming_01", "housewarming_02", "housewarming_03",
        "housewarming_04", "housewarming_05"
    )
    private val pubertyIds = setOf(
        "puberty_01", "puberty_02", "puberty_03", "puberty_04", "puberty_05"
    )

    private fun drawBirthdayPinkGold(canvas: Canvas, width: Int, height: Int) {
        fillGradient(canvas, width, height, Color.parseColor("#FFE8F0"), Color.parseColor("#FFF8FB"))
        drawConfetti(canvas, width, height, seed = 11, alpha = 90)
        drawBalloonCluster(canvas, 90f, 220f, 70f, Color.parseColor("#F48FB1"), Color.parseColor("#F06292"))
        drawBalloonCluster(canvas, width - 90f, 240f, 65f, Color.parseColor("#FFD54F"), Color.parseColor("#FFB300"))
        drawBalloonCluster(canvas, width - 120f, height - 180f, 55f, Color.parseColor("#F48FB1"), Color.parseColor("#EC407A"))
        drawGoldHexFrame(canvas, width, height, inset = 72f)
    }

    private fun drawBirthdayNavyGold(canvas: Canvas, width: Int, height: Int) {
        fillGradient(canvas, width, height, Color.parseColor("#0D1B3E"), Color.parseColor("#1A2F5C"))
        drawBunting(canvas, width, 120f, Color.parseColor("#F7C948"), Color.parseColor("#1A2F5C"))
        drawStringLights(canvas, width, 95f)
        drawBalloonCluster(canvas, 100f, 260f, 60f, Color.parseColor("#F7C948"), Color.parseColor("#FFB300"))
        drawBalloonCluster(canvas, width - 110f, 280f, 58f, Color.parseColor("#5C6BC0"), Color.parseColor("#3949AB"))
        drawConfetti(canvas, width, height, seed = 22, alpha = 70, light = true)
    }

    private fun drawBirthdayPlayful(canvas: Canvas, width: Int, height: Int) {
        fillGradient(canvas, width, height, Color.parseColor("#FFF8E7"), Color.parseColor("#FFFDF5"))
        drawBunting(canvas, width, 100f, Color.parseColor("#42A5F5"), Color.parseColor("#FF7043"))
        drawConfetti(canvas, width, height, seed = 33, alpha = 110)
        drawBalloonCluster(canvas, 85f, 200f, 50f, Color.parseColor("#42A5F5"), Color.parseColor("#1E88E5"))
        drawBalloonCluster(canvas, width - 95f, 210f, 48f, Color.parseColor("#66BB6A"), Color.parseColor("#43A047"))
        drawCartoonCake(canvas, width / 2f, height - 130f)
        drawGiftBox(canvas, width - 130f, height - 120f, Color.parseColor("#42A5F5"))
    }

    private fun drawBirthdayFloral(canvas: Canvas, width: Int, height: Int) {
        fillGradient(canvas, width, height, Color.parseColor("#E8F2E5"), Color.parseColor("#F7FBF4"))
        drawWatercolorRose(canvas, width - 140f, 150f, 1.1f)
        drawWatercolorRose(canvas, 130f, height - 160f, 0.95f)
        drawGoldHexFrame(canvas, width, height, inset = 80f, alpha = 140)
        drawLeafSprig(canvas, 60f, 180f)
        drawLeafSprig(canvas, width - 60f, height - 200f, flip = true)
    }

    private fun drawBirthdayPurpleParty(canvas: Canvas, width: Int, height: Int) {
        fillGradient(canvas, width, height, Color.parseColor("#2D1046"), Color.parseColor("#4A148C"))
        drawBokeh(canvas, width, height, seed = 44)
        drawStringLights(canvas, width, 88f, warm = true)
        drawBalloonCluster(canvas, width - 100f, 250f, 62f, Color.parseColor("#EC407A"), Color.parseColor("#AD1457"))
        drawBalloonCluster(canvas, width - 130f, height - 150f, 50f, Color.parseColor("#AB47BC"), Color.parseColor("#7B1FA2"))
        drawGiftBox(canvas, width - 120f, height - 110f, Color.parseColor("#AB47BC"))
        drawConfetti(canvas, width, height, seed = 55, alpha = 60, light = true)
    }

    private fun drawWeddingTheme(canvas: Canvas, width: Int, height: Int, templateId: String) {
        val deep = when (templateId) {
            "wedding_02" -> Color.parseColor("#4A0E1F")
            "wedding_03" -> Color.parseColor("#3E0F24")
            else -> Color.parseColor("#5D1532")
        }
        fillGradient(canvas, width, height, deep, Color.parseColor("#8B1E3F"))
        drawGoldHexFrame(canvas, width, height, inset = 68f)
        drawKolamRing(canvas, width / 2f, 200f, 55f)
        drawBalloonCluster(canvas, 95f, 240f, 45f, Color.parseColor("#F7C948"), Color.parseColor("#FFB300"))
        drawBalloonCluster(canvas, width - 95f, 240f, 45f, Color.parseColor("#F7C948"), Color.parseColor("#FFB300"))
        if (templateId == "wedding_05") {
            drawPeacockFeather(canvas, 80f, 180f)
            drawPeacockFeather(canvas, width - 80f, 180f, flip = true)
        }
    }

    private fun drawHousewarmingTheme(canvas: Canvas, width: Int, height: Int, templateId: String) {
        fillGradient(canvas, width, height, Color.parseColor("#1B5E20"), Color.parseColor("#E8F5E9"))
        drawMangoLeaves(canvas, width, 100f)
        when (templateId) {
            "housewarming_02" -> drawLampRow(canvas, width, height - 200f)
            "housewarming_03" -> drawTempleFrame(canvas, width, height)
            "housewarming_04" -> drawBananaLeafPanel(canvas, width, height)
            else -> drawHomeIcon(canvas, width / 2f, height - 180f)
        }
        drawGoldHexFrame(canvas, width, height, inset = 74f, alpha = 120)
    }

    private fun drawPubertyTheme(canvas: Canvas, width: Int, height: Int, templateId: String) {
        fillGradient(canvas, width, height, Color.parseColor("#4A148C"), Color.parseColor("#FCE4EC"))
        drawGarland(canvas, width, 110f)
        when (templateId) {
            "puberty_01", "puberty_05" -> {
                drawPeacockFeather(canvas, 75f, 170f)
                drawPeacockFeather(canvas, width - 75f, 170f, flip = true)
            }
            "puberty_03" -> drawKolamRing(canvas, width / 2f, 190f, 48f)
            else -> drawWatercolorRose(canvas, width - 120f, 160f, 0.85f)
        }
        drawGoldHexFrame(canvas, width, height, inset = 76f, alpha = 110)
    }

    private fun drawDefaultWarm(canvas: Canvas, width: Int, height: Int) {
        fillGradient(canvas, width, height, Color.parseColor("#FFF4E6"), Color.parseColor("#FFFFFF"))
        drawGoldHexFrame(canvas, width, height, inset = 80f)
    }

    private fun fillGradient(canvas: Canvas, width: Int, height: Int, top: Int, bottom: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                top, bottom,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun drawBalloonCluster(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        fill: Int,
        shadow: Int
    ) {
        val offsets = listOf(
            0f to 0f,
            -radius * 0.55f to radius * 0.35f,
            radius * 0.5f to radius * 0.4f,
            -radius * 0.2f to -radius * 0.55f
        )
        offsets.forEach { (dx, dy) ->
            drawBalloon(canvas, cx + dx, cy + dy, radius * 0.72f, fill, shadow)
        }
    }

    private fun drawBalloon(canvas: Canvas, cx: Float, cy: Float, radius: Float, fill: Int, shadow: Int) {
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = shadow }
        canvas.drawCircle(cx + 6f, cy + 8f, radius, shadowPaint)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fill }
        canvas.drawCircle(cx, cy, radius, paint)
        val shine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(120, 255, 255, 255)
        }
        canvas.drawCircle(cx - radius * 0.25f, cy - radius * 0.2f, radius * 0.22f, shine)
        val stringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(160, 80, 80, 80)
            strokeWidth = 2.5f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(cx, cy + radius, cx + 8f, cy + radius + 40f, stringPaint)
    }

    private fun drawConfetti(
        canvas: Canvas,
        width: Int,
        height: Int,
        seed: Int,
        alpha: Int,
        light: Boolean = false
    ) {
        val random = Random(seed)
        val colors = if (light) {
            intArrayOf(
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#F7C948"),
                Color.parseColor("#F48FB1"),
                Color.parseColor("#81D4FA")
            )
        } else {
            intArrayOf(
                Color.parseColor("#F06292"),
                Color.parseColor("#FFB300"),
                Color.parseColor("#42A5F5"),
                Color.parseColor("#66BB6A"),
                Color.parseColor("#AB47BC")
            )
        }
        repeat(55) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = colors[random.nextInt(colors.size)]
                this.alpha = alpha
            }
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = 4f + random.nextFloat() * 8f
            if (random.nextBoolean()) {
                canvas.drawCircle(x, y, size, paint)
            } else {
                canvas.drawRect(x, y, x + size * 2, y + size * 0.6f, paint)
            }
        }
    }

    private fun drawGoldHexFrame(canvas: Canvas, width: Int, height: Int, inset: Float, alpha: Int = 200) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(alpha, 247, 201, 72)
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        val rect = RectF(inset, inset + 40f, width - inset, height - inset - 40f)
        val path = Path()
        val cx = rect.centerX()
        val cy = rect.centerY()
        val rx = rect.width() / 2f
        val ry = rect.height() / 2f
        for (i in 0 until 6) {
            val angle = Math.toRadians((60 * i - 30).toDouble())
            val x = cx + rx * cos(angle).toFloat()
            val y = cy + ry * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawBunting(canvas: Canvas, width: Int, top: Float, colorA: Int, colorB: Int) {
        val flagWidth = width / 10f
        var x = 20f
        var toggle = true
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rope = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 120, 90, 60)
            strokeWidth = 3f
        }
        canvas.drawLine(0f, top, width.toFloat(), top + 18f, rope)
        while (x < width - 20f) {
            paint.color = if (toggle) colorA else colorB
            val path = Path().apply {
                moveTo(x, top + 18f)
                lineTo(x + flagWidth / 2f, top + 58f)
                lineTo(x + flagWidth, top + 18f)
                close()
            }
            canvas.drawPath(path, paint)
            x += flagWidth
            toggle = !toggle
        }
    }

    private fun drawStringLights(canvas: Canvas, width: Int, top: Float, warm: Boolean = false) {
        val bulbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val wire = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(140, 60, 60, 60)
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        var x = 30f
        canvas.drawLine(0f, top, width.toFloat(), top + 10f, wire)
        while (x < width - 30f) {
            bulbPaint.color = if (warm) {
                Color.parseColor(if ((x.toInt() / 40) % 2 == 0) "#FFEB3B" else "#FF80AB")
            } else {
                Color.parseColor(if ((x.toInt() / 40) % 2 == 0) "#FFF59D" else "#FFFFFF")
            }
            canvas.drawCircle(x, top + 14f, 8f, bulbPaint)
            x += 42f
        }
    }

    private fun drawBokeh(canvas: Canvas, width: Int, height: Int, seed: Int) {
        val random = Random(seed)
        repeat(18) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    random.nextFloat() * width,
                    random.nextFloat() * height,
                    30f + random.nextFloat() * 80f,
                    Color.argb(50, 255, 255, 255),
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(
                random.nextFloat() * width,
                random.nextFloat() * height,
                40f + random.nextFloat() * 60f,
                paint
            )
        }
    }

    private fun drawWatercolorRose(canvas: Canvas, cx: Float, cy: Float, scale: Float) {
        val petal = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F48FB1") }
        val core = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F06292") }
        repeat(6) { i ->
            val angle = Math.toRadians((i * 60).toDouble())
            val px = cx + cos(angle).toFloat() * 28f * scale
            val py = cy + sin(angle).toFloat() * 28f * scale
            canvas.drawCircle(px, py, 22f * scale, petal)
        }
        canvas.drawCircle(cx, cy, 16f * scale, core)
    }

    private fun drawLeafSprig(canvas: Canvas, x: Float, y: Float, flip: Boolean = false) {
        val leaf = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#66BB6A") }
        val dx = if (flip) -1f else 1f
        canvas.drawOval(RectF(x, y, x + 40f * dx, y + 18f), leaf)
        canvas.drawOval(RectF(x + 12f * dx, y + 14f, x + 52f * dx, y + 32f), leaf)
    }

    private fun drawCartoonCake(canvas: Canvas, cx: Float, cy: Float) {
        val base = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFCC80") }
        val frost = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F48FB1") }
        canvas.drawRoundRect(RectF(cx - 55f, cy - 30f, cx + 55f, cy + 10f), 12f, 12f, base)
        canvas.drawRoundRect(RectF(cx - 40f, cy - 55f, cx + 40f, cy - 25f), 10f, 10f, frost)
        val flame = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFB300") }
        canvas.drawCircle(cx, cy - 62f, 7f, flame)
    }

    private fun drawGiftBox(canvas: Canvas, x: Float, y: Float, fillColor: Int) {
        val box = Paint(Paint.ANTI_ALIAS_FLAG).apply { setColor(fillColor) }
        val ribbon = Paint(Paint.ANTI_ALIAS_FLAG).apply { setColor(Color.parseColor("#F7C948")) }
        canvas.drawRect(x - 30f, y - 30f, x + 30f, y + 20f, box)
        canvas.drawRect(x - 6f, y - 30f, x + 6f, y + 20f, ribbon)
        canvas.drawRect(x - 30f, y - 8f, x + 30f, y + 8f, ribbon)
    }

    private fun drawKolamRing(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(160, 247, 201, 72)
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawCircle(cx, cy, radius, paint)
        canvas.drawCircle(cx, cy, radius * 0.65f, paint)
        repeat(8) { i ->
            val angle = Math.toRadians((i * 45).toDouble())
            val x = cx + cos(angle).toFloat() * radius
            val y = cy + sin(angle).toFloat() * radius
            canvas.drawCircle(x, y, 6f, paint)
        }
    }

    private fun drawPeacockFeather(canvas: Canvas, x: Float, y: Float, flip: Boolean = false) {
        val shaft = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00897B")
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }
        val eye = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5C6BC0") }
        val dx = if (flip) -1f else 1f
        canvas.drawLine(x, y, x + 50f * dx, y + 90f, shaft)
        canvas.drawCircle(x + 55f * dx, y + 95f, 22f, eye)
        canvas.drawCircle(x + 55f * dx, y + 95f, 10f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F7C948")
        })
    }

    private fun drawMangoLeaves(canvas: Canvas, width: Int, top: Float) {
        val leaf = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#2E7D32") }
        var x = 24f
        while (x < width - 24f) {
            val path = Path().apply {
                moveTo(x, top)
                lineTo(x + 18f, top + 28f)
                lineTo(x + 36f, top)
                close()
            }
            canvas.drawPath(path, leaf)
            x += 42f
        }
    }

    private fun drawLampRow(canvas: Canvas, width: Int, y: Float) {
        val positions = listOf(0.25f, 0.4f, 0.5f, 0.6f, 0.75f)
        positions.forEach { fraction ->
            drawDiya(canvas, width * fraction, y)
        }
    }

    private fun drawDiya(canvas: Canvas, x: Float, y: Float) {
        val lamp = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF9800") }
        val flame = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFEB3B") }
        canvas.drawOval(RectF(x - 18f, y, x + 18f, y + 14f), lamp)
        canvas.drawCircle(x, y - 8f, 8f, flame)
    }

    private fun drawTempleFrame(canvas: Canvas, width: Int, height: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F7C948") }
        val cx = width / 2f
        val path = Path().apply {
            moveTo(cx, 120f)
            lineTo(cx - 120f, 200f)
            lineTo(cx + 120f, 200f)
            close()
        }
        canvas.drawPath(path, paint)
    }

    private fun drawBananaLeafPanel(canvas: Canvas, width: Int, height: Int) {
        val leaf = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#2E7D32") }
        canvas.drawOval(RectF(80f, height - 280f, width - 80f, height - 120f), leaf)
    }

    private fun drawHomeIcon(canvas: Canvas, cx: Float, cy: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F7C948") }
        val path = Path().apply {
            moveTo(cx, cy - 40f)
            lineTo(cx - 50f, cy)
            lineTo(cx - 35f, cy)
            lineTo(cx - 35f, cy + 45f)
            lineTo(cx + 35f, cy + 45f)
            lineTo(cx + 35f, cy)
            lineTo(cx + 50f, cy)
            close()
        }
        canvas.drawPath(path, paint)
    }

    private fun drawGarland(canvas: Canvas, width: Int, top: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F48FB1") }
        var x = 30f
        while (x < width - 30f) {
            canvas.drawCircle(x, top + 10f + (sin(x / 30f) * 8f), 9f, paint)
            x += 24f
        }
    }
}
