package com.example.fooddiary.utils

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

object CameraUtils {

    fun ImageProxy.toBitmap(): Bitmap {
        val yuvBytes = this.toYuvByteArray()
        val yuvImage = YuvImage(
            yuvBytes,
            ImageFormat.NV21,
            this.width,
            this.height,
            null
        )

        val outputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(0, 0, this.width, this.height),
            80,
            outputStream
        )

        val jpegBytes = outputStream.toByteArray()
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)

        // Поворачиваем bitmap если нужно
        val rotation = this.imageInfo.rotationDegrees
        return if (rotation != 0) {
            rotateBitmap(bitmap, rotation.toFloat())
        } else {
            bitmap
        }
    }

    private fun ImageProxy.toYuvByteArray(): ByteArray {
        val planes = this.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // Y
        yBuffer.get(nv21, 0, ySize)

        // U and V
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        return nv21
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source,
            0,
            0,
            source.width,
            source.height,
            matrix,
            true
        )
    }
}