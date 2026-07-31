package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bag_types")
data class BagType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int? = null,
    val clientName: String,
    val typeName: String, // e.g., "أكياس خليل 40 مبسم شفاف"
    val mouthpiecesPerBag: Int = 40,
    val emptyBagTareGrams: Double = 5.0, // Grams per empty bag
    val avgMouthpieceWeightGrams: Double = 2.5, // Grams per single mouthpiece
    val notes: String? = null
)
