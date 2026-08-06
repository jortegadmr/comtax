package com.comtax.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tipografia con tamanos base elevados (sp, respeta fontScale del sistema hasta 200%).
val ComtaxTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 28.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 24.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 22.sp)
)

// Tratamiento unico para cifras monetarias: negrita + digitos tabulares (alineados
// como en un libro de caja). Se aplica sobre el tamano de base que corresponda en
// cada pantalla (headlineLarge para saldo, titleMedium para importe por movimiento...).
fun TextStyle.comoCifra(): TextStyle =
    copy(fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum")
