package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Module A: Injection Molding Record (قسم آلة الحقن)
 */
@Entity(tableName = "injection_records")
data class InjectionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val machineId: Int,
    val machineName: String,
    val shiftId: Int,
    val shiftName: String,
    val operatorId: Int,
    val operatorName: String,
    val rawMaterialWeightKg: Double, // وزن المواد الأولية (كغ)
    val finishedMouthpiecesWeightKg: Double, // وزن الخرج بعد الانتهاء (كغ)
    val wasteWeightKg: Double, // وزن الهدر (كغ) = rawMaterial - finished
    val yieldPercentage: Double, // نسبة الإنتاجية = (finished / rawMaterial) * 100
    val notes: String? = null
)

/**
 * Module B: Machine Packaging Record (قسم آلة التغليف الآلي)
 * Dual-shift recording support for accumulated weights per machine/shift
 */
@Entity(tableName = "auto_packaging_records")
data class AutoPackagingRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val machineId: Int,
    val machineName: String,
    val shiftId: Int,
    val shiftName: String, // "وردية صباحية" or "وردية مسائية" or "وردية ليلية"
    val workerNames: String, // Assigned multiple workers (e.g. "أحمد المحمد، سامر علي")
    val startAccumulatedWeightKg: Double, // الوزن التراكمي بداية الوردية
    val endAccumulatedWeightKg: Double, // الوزن التراكمي نهاية الوردية
    val netShiftWeightKg: Double, // صافي وزن الإنتاج في الوردية = end - start
    val notes: String? = null
)

/**
 * Module C: Manual Packaging Record (40 Mouthpieces per bag) (قسم التعبئة اليدوية - 40 مبسم)
 */
@Entity(tableName = "manual_packaging_records")
data class ManualPackagingRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val clientId: Int,
    val clientName: String, // e.g., "أكياس خليل", "أكياس يحيى"
    val bagTypeId: Int? = null,
    val bagTypeName: String = "",
    val shiftId: Int,
    val shiftName: String,
    val workerNames: String,
    val emptyBagsTareGrams: Double = 5.0, // Tare per bag in grams
    val totalEmptyBagsWeightGrams: Double = 0.0, // Total tare weight of empty bags
    val unpackedMouthpiecesWeightKg: Double, // وزن المباسم غير المعبأة قبل التعبئة (كغ)
    val totalPackedBagsWeightKg: Double, // الوزن الكلي للأكياس المعبأة (كغ)
    val mouthpiecesPerBag: Int = 40, // 40 مبسم per bag default
    val calculatedTotalBags: Int, // عدد الأكياس المعبأة
    val calculatedTotalMouthpieces: Int, // عدد المباسم الكلي = calculatedTotalBags * mouthpiecesPerBag
    val netMouthpiecesWeightKg: Double, // صافي وزن المباسم المنتجة للعميل (كغ)
    val packagingLossKg: Double = 0.0, // الفاقد أو الهدر = unpackedMouthpiecesWeightKg - netMouthpiecesWeightKg
    val notes: String? = null
)
