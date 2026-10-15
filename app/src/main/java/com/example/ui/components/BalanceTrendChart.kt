package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TrendPoint
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BalanceTrendChart(
    points: List<TrendPoint>,
    currency: String,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    var selectedIndex by remember { mutableStateOf(points.size - 1) }
    val selectedPoint = points.getOrNull(selectedIndex) ?: points.last()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Balance Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${selectedPoint.label}: ${formatNum(selectedPoint.balance)}$currency",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentCyan
                )
            }

            // Trend Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentGreen.copy(alpha = 0.15f))
                    .border(0.5.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "+4.8%",
                    color = AccentGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart Canvas
        val minVal = points.minOf { it.balance } * 0.95
        val maxVal = points.maxOf { it.balance } * 1.05
        val range = if (maxVal - minVal > 0) (maxVal - minVal).toFloat() else 1f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                            val clickedIdx = (offset.x / stepX).toInt().coerceIn(0, points.size - 1)
                            selectedIndex = clickedIdx
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val stepX = width / (points.size - 1).coerceAtLeast(1)

                // Grid background horizontal lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = Color(0xFF2C2C2E).copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                // Compute point coordinates
                val coordinates = points.mapIndexed { index, point ->
                    val x = index * stepX
                    val normalizedY = ((point.balance - minVal) / range).toFloat()
                    val y = height - (normalizedY * (height - 30f)) - 15f
                    Offset(x, y)
                }

                // Path for line and fill
                val path = Path()
                val fillPath = Path()

                coordinates.forEachIndexed { index, offset ->
                    if (index == 0) {
                        path.moveTo(offset.x, offset.y)
                        fillPath.moveTo(offset.x, height)
                        fillPath.lineTo(offset.x, offset.y)
                    } else {
                        // Smooth cubic bezier
                        val prev = coordinates[index - 1]
                        val controlX1 = prev.x + (offset.x - prev.x) / 2
                        val controlY1 = prev.y
                        val controlX2 = prev.x + (offset.x - prev.x) / 2
                        val controlY2 = offset.y
                        path.cubicTo(controlX1, controlY1, controlX2, controlY2, offset.x, offset.y)
                        fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, offset.x, offset.y)
                    }
                }

                fillPath.lineTo(coordinates.last().x, height)
                fillPath.close()

                // Draw gradient under the curve
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.35f),
                            AccentCyan.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )

                // Draw line stroke
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        listOf(AccentBlue, AccentCyan)
                    ),
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw dots and vertical highlight line for selected index
                coordinates.forEachIndexed { index, offset ->
                    if (index == selectedIndex) {
                        // Vertical indicator line
                        drawLine(
                            color = AccentCyan.copy(alpha = 0.6f),
                            start = Offset(offset.x, 0f),
                            end = Offset(offset.x, height),
                            strokeWidth = 1.5.dp.toPx()
                        )
                        // Glowing outer circle
                        drawCircle(
                            color = AccentCyan.copy(alpha = 0.3f),
                            radius = 8.dp.toPx(),
                            center = offset
                        )
                        // Solid inner dot
                        drawCircle(
                            color = AccentCyan,
                            radius = 4.5.dp.toPx(),
                            center = offset
                        )
                    } else {
                        drawCircle(
                            color = Color(0xFF1C1C1E),
                            radius = 4.dp.toPx(),
                            center = offset
                        )
                        drawCircle(
                            color = AccentBlue,
                            radius = 2.5.dp.toPx(),
                            center = offset
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Date labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEachIndexed { idx, point ->
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (idx == selectedIndex) AccentCyan else TextSecondary,
                    fontWeight = if (idx == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun formatNum(value: Double): String {
    if (value == 0.0) return "00"
    return NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }.format(value)
}
