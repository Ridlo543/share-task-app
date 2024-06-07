package com.l0122138.ridlo.sharetaskapp.ui.calendar

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan


class OutlineCircleSpan(
    private val borderColor: Int,
    private val borderWidth: Float,
    private val padding: Float
) : LineBackgroundSpan {

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val oldColor = paint.color
        val oldStyle = paint.style
        val oldStrokeWidth = paint.strokeWidth

        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth

        val centerX = (left + right) / 2f
        val centerY = (top + bottom) / 2f
        val radius = (right - left) / 2f - padding

        canvas.drawCircle(centerX, centerY, radius, paint)

        paint.color = oldColor
        paint.style = oldStyle
        paint.strokeWidth = oldStrokeWidth
    }
}