package com.example.fooddiary.data.datasource.remote.foodrecognition

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.fooddiary.BuildConfig
import com.example.fooddiary.domain.model.foodrecognition.FoodRecognitionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqFoodRecognitionDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private const val TAG = "GroqFoodDS"
        private const val GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"
//        private const val MODEL = "llava-v1.5-7b-4096-preview"
        private const val MODEL = "meta-llama/llama-4-scout-17b-16e-instruct"
        private const val MAX_IMAGE_DIMENSION = 1024
    }

    suspend fun recognizeFood(bitmap: Bitmap): Result<FoodRecognitionResult> =
        withContext(Dispatchers.IO) {
            try {
                val scaledBitmap = scaleBitmap(bitmap, MAX_IMAGE_DIMENSION)
                val base64Image = bitmapToBase64(scaledBitmap)

                val prompt = """
                    What food is shown in this image? Answer ONLY with a valid JSON object in this exact format, no extra text:
                    {
                      "name": "food name in Russian",
                      "calories": kcal per serving (number),
                      "protein": g per serving (number),
                      "fat": g per serving (number),
                      "carbs": g per serving (number),
                      "confidence": 0.0 to 1.0 (number),
                      "description": "brief description in Russian"
                    }
                    If you cannot identify the food, set name to "Неизвестное блюдо" and confidence to 0.
                """.trimIndent()

                val requestBodyJson = JSONObject().apply {
                    put("model", MODEL)
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("type", "text")
                                    put("text", prompt)
                                })
                                put(JSONObject().apply {
                                    put("type", "image_url")
                                    put("image_url", JSONObject().apply {
                                        put("url", "data:image/jpeg;base64,$base64Image")
                                    })
                                })
                            })
                        })
                    })
                }

                val bodyString = requestBodyJson.toString()
                val bodyBytes = bodyString.toByteArray()
                Log.d(TAG, "Размер тела запроса: ${bodyBytes.size / 1024} кБ")
                Log.d(TAG, "Первые 200 символов тела: ${bodyString.take(200)}")

                val requestBody = bodyString.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(GROQ_API_URL)
                    .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                    .post(requestBody)
                    .build()

                Log.d(TAG, "Отправка запроса к Groq...")
                val response = okHttpClient.newCall(request).execute()
                val responseText = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    Log.e(TAG, "HTTP ошибка: ${response.code} $responseText")
                    return@withContext Result.failure(Exception("Ошибка сервера: ${response.code}"))
                }

                Log.d(TAG, "Ответ Groq: $responseText")
                parseGroqResponse(responseText)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка распознавания: ${e.message}", e)
                Result.failure(e)
            }
        }

    // refineRecognition – точно так же добавляем масштабирование и логи
    suspend fun refineRecognition(bitmap: Bitmap, query: String): Result<FoodRecognitionResult> =
        withContext(Dispatchers.IO) {
            try {
                val scaledBitmap = scaleBitmap(bitmap, MAX_IMAGE_DIMENSION)
                val base64Image = bitmapToBase64(scaledBitmap)

                val prompt = """
                Here is a food photo. The user asks: "$query"
                Answer ONLY with a valid JSON object in this exact format, no extra text:
                {
                  "name": "food name in Russian",
                  "calories": kcal per serving (number),
                  "protein": g per serving (number),
                  "fat": g per serving (number),
                  "carbs": g per serving (number),
                  "confidence": 0.0 to 1.0 (number),
                  "description": "brief description in Russian"
                }
                Update the nutritional info based on the user's clarification.
                If the user specifies a different portion size, adjust accordingly.
                """.trimIndent()

                val requestBodyJson = JSONObject().apply {
                    put("model", MODEL)
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("type", "text")
                                    put("text", prompt)
                                })
                                put(JSONObject().apply {
                                    put("type", "image_url")
                                    put("image_url", JSONObject().apply {
                                        put("url", "data:image/jpeg;base64,$base64Image")
                                    })
                                })
                            })
                        })
                    })
                }

                val bodyString = requestBodyJson.toString()
                Log.d(TAG, "Размер тела запроса (refine): ${bodyString.toByteArray().size / 1024} кБ")

                val requestBody = bodyString.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(GROQ_API_URL)
                    .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseText = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    Log.e(TAG, "HTTP ошибка refine: ${response.code} $responseText")
                    return@withContext Result.failure(Exception("Ошибка сервера: ${response.code}"))
                }

                parseGroqResponse(responseText)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка уточнения: ${e.message}", e)
                Result.failure(e)
            }
        }

    private fun scaleBitmap(original: Bitmap, maxSize: Int): Bitmap {
        val width = original.width
        val height = original.height
        if (width <= maxSize && height <= maxSize) return original

        val ratio = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        val bytes = outputStream.toByteArray()
        Log.d(TAG, "Размер изображения после сжатия: ${bytes.size / 1024} кБ")
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun parseGroqResponse(jsonResponse: String): Result<FoodRecognitionResult> {
        return try {
            val root = JSONObject(jsonResponse)
            val choices = root.getJSONArray("choices")
            val messageContent = choices
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            val cleanedJson = messageContent
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val foodObj = JSONObject(cleanedJson)
            val result = FoodRecognitionResult(
                name = foodObj.optString("name", "Неизвестное блюдо"),
                calories = foodObj.optDouble("calories", 0.0),
                protein = foodObj.optDouble("protein", 0.0),
                fat = foodObj.optDouble("fat", 0.0),
                carbs = foodObj.optDouble("carbs", 0.0),
                confidence = foodObj.optDouble("confidence", 0.0),
                description = foodObj.optString("description", "")
            )
            Log.d(TAG, "Распознано: $result")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка парсинга ответа Groq: ${e.message}")
            Result.failure(Exception("Не удалось распознать блюдо"))
        }
    }
}