package com.example.fooddiary.ui.screens.barcode

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun BarcodeScannerOverlay(
    modifier: Modifier = Modifier,
    isScanning: Boolean = false
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Полупрозрачный фон
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Затененные области по бокам
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                size = size
            )

            // Прозрачное окно для сканирования
            val scannerWidth = size.width * 0.8f
            val scannerHeight = scannerWidth * 0.5f
            val scannerLeft = (size.width - scannerWidth) / 2
            val scannerTop = (size.height - scannerHeight) / 2
            val scannerRight = scannerLeft + scannerWidth
            val scannerBottom = scannerTop + scannerHeight

            drawRect(
                color = Color.Transparent,
                topLeft = Offset(scannerLeft, scannerTop),
                size = androidx.compose.ui.geometry.Size(scannerWidth, scannerHeight)
            )

            // Рамка сканера
            val cornerLength = 40.dp.toPx()
            val strokeWidth = 4.dp.toPx()

            // Углы рамки
            // Левый верхний
            drawLine(
                color = Color.Green,
                start = Offset(scannerLeft, scannerTop),
                end = Offset(scannerLeft + cornerLength, scannerTop),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.Green,
                start = Offset(scannerLeft, scannerTop),
                end = Offset(scannerLeft, scannerTop + cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Правый верхний
            drawLine(
                color = Color.Green,
                start = Offset(scannerRight, scannerTop),
                end = Offset(scannerRight - cornerLength, scannerTop),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.Green,
                start = Offset(scannerRight, scannerTop),
                end = Offset(scannerRight, scannerTop + cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Левый нижний
            drawLine(
                color = Color.Green,
                start = Offset(scannerLeft, scannerBottom),
                end = Offset(scannerLeft + cornerLength, scannerBottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.Green,
                start = Offset(scannerLeft, scannerBottom),
                end = Offset(scannerLeft, scannerBottom - cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Правый нижний
            drawLine(
                color = Color.Green,
                start = Offset(scannerRight, scannerBottom),
                end = Offset(scannerRight - cornerLength, scannerBottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.Green,
                start = Offset(scannerRight, scannerBottom),
                end = Offset(scannerRight, scannerBottom - cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Линия сканирования
            if (isScanning) {
                val scanLineY = scannerTop + (scannerHeight * 0.5f)
                drawLine(
                    color = Color.Green.copy(alpha = 0.8f),
                    start = Offset(scannerLeft + 10.dp.toPx(), scanLineY),
                    end = Offset(scannerRight - 10.dp.toPx(), scanLineY),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Text(
            text = "Наведите камеру на штрихкод продукта",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )

        if (isScanning) {
            CircularProgressIndicator(
                color = Color.Green,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 100.dp)
            )
        }
    }
}