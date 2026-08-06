package com.comtax.app.ui.common

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Firma visual de "sobre": esquina superior derecha cortada en diagonal (solapa de
 * un sobre real), resto de esquinas redondeadas. Pasar como `shape` a Card/FilterChip/
 * Surface para cualquier elemento que represente un sobre de presupuesto.
 */
class FormaSobre(
    private val corte: Dp = 10.dp,
    private val radio: Dp = 8.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cortePx = with(density) { corte.toPx() }.coerceAtMost(size.width / 2f)
        val radioPx = with(density) { radio.toPx() }.coerceAtMost(minOf(size.width, size.height) / 2f)
        val path = Path().apply {
            moveTo(radioPx, 0f)
            lineTo(size.width - cortePx, 0f)
            lineTo(size.width, cortePx)
            lineTo(size.width, size.height - radioPx)
            quadraticTo(size.width, size.height, size.width - radioPx, size.height)
            lineTo(radioPx, size.height)
            quadraticTo(0f, size.height, 0f, size.height - radioPx)
            lineTo(0f, radioPx)
            quadraticTo(0f, 0f, radioPx, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}
