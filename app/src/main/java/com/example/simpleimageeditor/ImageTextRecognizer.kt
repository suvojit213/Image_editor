package com.example.simpleimageeditor

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.IOException

object ImageTextRecognizer {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val imageTextMap = mutableMapOf<Uri, String>()

    suspend fun recognizeTextFromImage(context: Context, imageUri: Uri): String? {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = textRecognizer.process(image).await()
            val recognizedText = result.text
            imageTextMap[imageUri] = recognizedText // Store the recognized text
            recognizedText
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getRecognizedText(imageUri: Uri): String? {
        return imageTextMap[imageUri]
    }

    fun getAllRecognizedTexts(): Map<Uri, String> {
        return imageTextMap
    }

    fun clearRecognizedTexts() {
        imageTextMap.clear()
    }
}