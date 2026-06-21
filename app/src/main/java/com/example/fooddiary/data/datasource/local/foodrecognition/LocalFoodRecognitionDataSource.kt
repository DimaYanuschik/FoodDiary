package com.example.fooddiary.data.datasource.local.foodrecognition

//import android.content.Context
//import android.graphics.Bitmap
//import org.tensorflow.lite.Interpreter
//import org.tensorflow.lite.support.common.FileUtil
//import org.tensorflow.lite.support.metadata.MetadataExtractor
//import org.tensorflow.lite.DataType
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
//import java.io.BufferedReader
//import java.io.InputStreamReader
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import javax.inject.Inject
//import javax.inject.Singleton
//import java.nio.charset.Charset

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.metadata.MetadataExtractor
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class LocalFoodRecognitionDataSource @Inject constructor(
//    private val context: Context
//) {
//    private var interpreter: Interpreter? = null
//    private var labels: List<String> = emptyList()
//
//    init {
//        try {
//            val modelBuffer = FileUtil.loadMappedFile(context, "food_classifier.tflite")
//            interpreter = Interpreter(modelBuffer)
//
//            // Пытаемся извлечь метки из метаданных
//            labels = try {
//                val metadataExtractor = MetadataExtractor(modelBuffer)
//                val stream = metadataExtractor.getAssociatedFile("labels.txt")
//                if (stream != null) {
//                    stream.bufferedReader().use { reader ->
//                        reader.readLines().map { it.trim() }.filter { it.isNotEmpty() }
//                    }
//                } else {
//                    loadLabelsFromAssets()
//                }
//            } catch (e: Exception) {
//                loadLabelsFromAssets()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    // Запасной метод: читает labels.txt из assets
//    private fun loadLabelsFromAssets(): List<String> {
//        val labels = mutableListOf<String>()
//        try {
//            val reader = BufferedReader(InputStreamReader(context.assets.open("labels.txt")))
//            reader.forEachLine { line ->
//                val trimmed = line.trim()
//                if (trimmed.isNotEmpty()) {
//                    labels.add(trimmed)
//                }
//            }
//            reader.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return labels
//    }
//
////    fun recognize(bitmap: Bitmap): Pair<String, Float> {
////        val inputSize = 224
////        val processedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
////
////        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
////        byteBuffer.order(ByteOrder.nativeOrder())
////        val intValues = IntArray(inputSize * inputSize)
////        processedBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)
////
////        for (pixel in intValues) {
////            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
////            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
////            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)
////        }
////
////        val output = Array(1) { FloatArray(labels.size) }
////        interpreter?.run(byteBuffer, output)
////        val probabilities = output[0]
////
////        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
////        val confidence = probabilities[maxIndex]
////        val label = labels.getOrElse(maxIndex) { "Неизвестное блюдо" }
////
////        return Pair(label, confidence)
////    }
//
//    fun recognize(bitmap: Bitmap): Pair<String, Float> {
//        val inputSize = 192
//        val processedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
//
//        val inputTensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, inputSize, inputSize, 3), DataType.UINT8)
//        val intValues = IntArray(inputSize * inputSize)
//        processedBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)
//        val inputArray = IntArray(inputSize * inputSize * 3)
//        var i = 0
//        for (pixel in intValues) {
//            inputArray[i++] = (pixel shr 16) and 0xFF
//            inputArray[i++] = (pixel shr 8) and 0xFF
//            inputArray[i++] = pixel and 0xFF
//        }
//        inputTensorBuffer.loadArray(inputArray)
//
//        // Выход: 2024 класса, тип UINT8 (значения 0–255)
//        val outputTensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.UINT8)
//        interpreter?.run(inputTensorBuffer.buffer, outputTensorBuffer.buffer)
//
//        val outputArray = outputTensorBuffer.intArray  // целые числа от 0 до 255
//        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: 0
//        val confidence = outputArray[maxIndex] / 255.0f
//        val label = labels.getOrElse(maxIndex) { "Неизвестное блюдо" }
//
//        return Pair(label, confidence)
//    }
//}


@Singleton
class LocalFoodRecognitionDataSource @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "LocalFoodDS"
    }

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    init {
        try {
            val modelBuffer = FileUtil.loadMappedFile(context, "food_classifier.tflite")
            interpreter = Interpreter(modelBuffer)

            // Пытаемся извлечь метки из метаданных, если есть
            labels = try {
                val metadataExtractor = MetadataExtractor(modelBuffer)
                val stream = metadataExtractor.getAssociatedFile("labels.txt")
                if (stream != null) {
                    stream.bufferedReader().use { reader ->
                        parseLabelStream(reader)
                    }
                } else {
                    loadLabelsFromAssets()
                }
            } catch (e: Exception) {
                loadLabelsFromAssets()
            }

            if (labels.isEmpty()) {
                Log.e(TAG, "⚠️ Метки не загружены!")
            } else {
                Log.d(TAG, "✅ Загружено ${labels.size} меток")
                Log.d(TAG, "labels[0] = ${labels[0]}\n" +
                        "labels[2023] = ${labels[2023]}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка инициализации модели", e)
        }
    }

    // Запасной метод: читает labels.txt из assets
    private fun loadLabelsFromAssets(): List<String> {
        return try {
            val reader = BufferedReader(InputStreamReader(context.assets.open("labels.txt")))
            parseLabelStream(reader)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки labels.txt из assets", e)
            emptyList()
        }
    }

    // Парсит CSV-поток, извлекая название блюда (второй столбец)
    private fun parseLabelStream(reader: BufferedReader): List<String> {
        val result = mutableListOf<String>()
        reader.use { r ->
            r.forEachLine { line ->
                val parts = line.split(',', limit = 2)
                if (parts.size >= 2) {
                    val name = parts[1].trim()
                    if (name.isNotEmpty()) {
                        result.add(name)
                    }
                }
            }
        }
        // Отрезаем заголовок, если он есть
        return if (result.firstOrNull() == "name") {
            Log.d(TAG, "Обнаружен заголовок 'name', удалён")
            result.drop(1)
        } else {
            result
        }
    }

//    fun recognize(bitmap: Bitmap): Pair<String, Float> {
//        val inputSize = 192
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
//
//        val byteBuffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3)
//        byteBuffer.order(ByteOrder.nativeOrder())
//
//        val intValues = IntArray(inputSize * inputSize)
//        scaledBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)
//
//        for (pixel in intValues) {
//            byteBuffer.put(((pixel shr 16) and 0xFF).toByte())  // R
//            byteBuffer.put(((pixel shr 8) and 0xFF).toByte())   // G
//            byteBuffer.put((pixel and 0xFF).toByte())           // B
//        }
//
//        // Выход: массив байт размером [1, labels.size]
//        val output = Array(1) { ByteArray(labels.size) }
//        interpreter?.run(byteBuffer, output)
//
//        val probabilities = output[0]
//        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it].toInt() and 0xFF } ?: 0
//        val confidence = (probabilities[maxIndex].toInt() and 0xFF) / 255.0f
//        val label = labels.getOrElse(maxIndex) { "Неизвестное блюдо" }
//
//        return Pair(label, confidence)
//    }

    fun recognize(bitmap: Bitmap): Pair<String, Float> {
        val inputSize = 192
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val byteBuffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        scaledBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in intValues) {
            byteBuffer.put(((pixel shr 16) and 0xFF).toByte())  // R
            byteBuffer.put(((pixel shr 8) and 0xFF).toByte())   // G
            byteBuffer.put((pixel and 0xFF).toByte())           // B
        }

        // Выход: массив байт размером [1, labels.size]
        val output = Array(1) { ByteArray(labels.size) }
        interpreter?.run(byteBuffer, output)

        val probabilities = output[0]

        // Топ-5 предсказаний с логированием
        val top5 = probabilities
            .mapIndexed { index, byte -> Pair(index, (byte.toInt() and 0xFF) / 255.0f) }
            .sortedByDescending { it.second }
            .take(5)

        Log.d(TAG, "Топ-5 предсказаний:")
        top5.forEach { (index, conf) ->
            val label = labels.getOrElse(index) { "???" }
            Log.d(TAG, "  $label: ${String.format("%.2f", conf)}")
        }

        val maxIndex = top5.first().first
        val confidence = top5.first().second
        val label = labels.getOrElse(maxIndex) { "Неизвестное блюдо" }

        return Pair(label, confidence)
    }
}