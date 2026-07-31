package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departments")
data class Department(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // e.g., "قسم آلة الحقن", "قسم آلة التغليف الآلي", "قسم التعبئة اليدوية"
    val code: String = "",
    val isSystem: Boolean = false
)

@Entity(tableName = "machines")
data class Machine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // e.g., "حقن 01", "حقن 02", "تغليف 01"
    val departmentId: Int,
    val departmentName: String,
    val status: String = "نشط" // نشط, صيانة
)

@Entity(tableName = "shifts")
data class Shift(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // e.g., "وردية صباحية", "وردية مسائية", "وردية ليلية"
    val startTime: String = "08:00",
    val endTime: String = "16:00"
)

@Entity(tableName = "workers")
data class Worker(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String = "عامل تشغيل", // حقن, تغليف آلي, تعبئة يدوية, مشرف
    val phone: String? = null
)
