package com.example.tiaalert.main.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlarmsModel(
    @SerialName("id")
    val id: Int = 0, // Generates a unique ID if not provided
    @SerialName("hour")
    val hour: Int = 0,
    @SerialName("minute")
    val minute: Int = 0,
    @SerialName("interval_seconds") // Changed to snake_case for consistency
    val intervalHours: Int = 3600, // Default to 1 hour if not provided
    @SerialName("name")
    val name: String = "Alarm"
)
