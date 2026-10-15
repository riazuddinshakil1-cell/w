package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CashFlowCard(
    income: Double,
    expense: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    val total = (income + expense).coerceAtLeast(1.0)
    val incomeRatio = (income / total).toFloat().coerceIn(0f, 1f)
    val netCashFlow = income - expense

    val animatedIncomeRatio by animateFloatAsState(
        targetValue = incomeRatio,
        animationSpec = tween(durationMillis = 800),
        label = "cash_flow_ratio"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cash Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = if (netCashFlow >= 0) "+${formatCurrency(netCashFlow)} $currency" else "-${formatCurrency(-netCashFlow)} $currency",
                color = if (netCashFlow >= 0) AccentGreen else AccentRed,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress bar container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(DarkSurfaceElevated)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (animatedIncomeRatio > 0.01f) {
                    Box(
                        modifier = Modifier
                            .weight(animatedIncomeRatio.coerceAtLeast(0.01f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 7.dp, bottomStart = 7.dp, topEnd = if (animatedIncomeRatio >= 0.99f) 7.dp else 0.dp, bottomEnd = if (animatedIncomeRatio >= 0.99f) 7.dp else 0.dp))
                            .background(AccentGreen)
                    )
                }
                val expenseWeight = (1f - animatedIncomeRatio).coerceAtLeast(0.01f)
                if (expenseWeight > 0.01f) {
                    Box(
                        modifier = Modifier
                            .weight(expenseWeight)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topEnd = 7.dp, bottomEnd = 7.dp, topStart = if (animatedIncomeRatio <= 0.01f) 7.dp else 0.dp, bottomStart = if (animatedIncomeRatio <= 0.01f) 7.dp else 0.dp))
                            .background(AccentRed)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Income",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = "${formatCurrency(income)} $currency",
                    color = AccentGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Expenses",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = "${formatCurrency(expense)} $currency",
                    color = AccentRed,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(amount)
}
