package com.mindscale.games.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindscale.games.domain.engine.ExpressionEvaluator
import com.mindscale.games.domain.model.Token
import com.mindscale.games.presentation.theme.*

@Composable
fun ScratchPad(
    text: String,
    onTextChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridButtons = listOf(
        listOf("7", "8", "9", "+"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "×"),
        listOf(".", "0", "=", "⌫")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & Read-only Display Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentPurple.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "PAD",
                    color = AccentPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Text(
                text = text.ifEmpty { "0" },
                color = if (text.isEmpty()) TextSecondary.copy(alpha = 0.5f) else TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            TextButton(
                onClick = onClear,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "CLR",
                    color = AccentRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // 4x4 Grid Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gridButtons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { symbol ->
                        val isOperator = symbol in listOf("+", "−", "×", "=")
                        val isDelete = symbol == "⌫"
                        val btnColor = when {
                            symbol == "=" -> AccentMint
                            isOperator -> AccentPurple
                            isDelete -> AccentRed
                            else -> TextPrimary
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceCardBorder.copy(alpha = 0.4f))
                                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(10.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    when (symbol) {
                                        "⌫" -> {
                                            if (text.isNotEmpty()) {
                                                onTextChange(text.substring(0, text.length - 1))
                                            }
                                        }
                                        "=" -> {
                                            val evaluated = evaluateScratchPadText(text)
                                            if (evaluated != null) {
                                                onTextChange(evaluated)
                                            }
                                        }
                                        else -> {
                                            onTextChange(text + symbol)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = symbol,
                                color = btnColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun evaluateScratchPadText(rawText: String): String? {
    if (rawText.isBlank()) return null
    return try {
        val tokens = mutableListOf<Token>()
        var i = 0
        val s = rawText.replace("−", "-").replace("×", "*").replace("÷", "/")
        while (i < s.length) {
            val c = s[i]
            when {
                c.isDigit() || c == '.' -> {
                    val start = i
                    while (i < s.length && (s[i].isDigit() || s[i] == '.')) {
                        i++
                    }
                    val numVal = s.substring(start, i).toDouble()
                    tokens.add(Token.Number(numVal))
                    continue
                }
                c == '+' || c == '-' || c == '*' || c == '/' -> {
                    val opSymbol = when (c) {
                        '*' -> '×'
                        '/' -> '÷'
                        else -> c
                    }
                    tokens.add(Token.Operator(opSymbol))
                }
                c == '(' -> tokens.add(Token.OpenParen)
                c == ')' -> tokens.add(Token.CloseParen)
            }
            i++
        }
        val result = ExpressionEvaluator.evaluate(tokens)
        if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    } catch (_: Exception) {
        null
    }
}

@Preview
@Composable
fun ScratchPadPreview() {
    MindScaleTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ScratchPad(
                text = "12 + 5",
                onTextChange = {},
                onClear = {}
            )
        }
    }
}
