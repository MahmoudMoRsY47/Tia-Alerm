package com.example.tiaalert.main.utils

import android.view.View
import androidx.core.view.isVisible
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

val json = Json { ignoreUnknownKeys = true }

inline fun <reified T> T.toJson(): String =
    json.encodeToString(this)

inline fun <reified T : Any> String.fromJson(): T =
    json.decodeFromString(this)

inline fun <reified T : Any> String.fromJsonArray(): List<T> =
    json.decodeFromString(serializer<List<T>>(), this)


fun View.toVisible() {
    if (!isVisible) {
        visibility = View.VISIBLE
        scaleX = 0f
        scaleY = 0f
        animate().scaleX(1f).scaleY(1f).setDuration(800).start()
    }
}

fun View.toGone() {
    if (isVisible) {
        scaleX = 1f
        scaleY = 1f
        animate().scaleX(0f).scaleY(0f).setDuration(800)
            .withEndAction { visibility = View.GONE }.start()
    }

}

fun View.toInVisible() {
    if (isVisible) {
        scaleX = 1f
        scaleY = 1f
        animate().scaleX(0f).scaleY(0f).setDuration(800)
            .withEndAction { visibility = View.INVISIBLE }.start()
    }
}

fun View.setVisibility(b: Boolean?) {
    isVisible = b ?: false
}