package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.FactoryDao
import com.example.data.local.entities.AutoPackagingRecord
import com.example.data.local.entities.BagType
import com.example.data.local.entities.Client
import com.example.data.local.entities.Department
import com.example.data.local.entities.InjectionRecord
import com.example.data.local.entities.ManualPackagingRecord
import com.example.data.local.entities.Machine
import com.example.data.local.entities.Shift
import com.example.data.local.entities.Worker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        Client::class,
        BagType::class,
        Department::class,
        Machine::class,
        Shift::class,
        Worker::class,
        InjectionRecord::class,
        AutoPackagingRecord::class,
        ManualPackagingRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun factoryDao(): FactoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mouthpiece_factory_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.factoryDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: FactoryDao) {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            // 1. Initial Clients
            val c1Id = dao.insertClient(Client(name = "أكياس خليل", phone = "0501234567", notes = "عميل رئيسي - طلبات تجميعية"))
            val c2Id = dao.insertClient(Client(name = "أكياس يحيى", phone = "0509876543", notes = "عميل مميز - أكياس طباعة خاصة"))
            val c3Id = dao.insertClient(Client(name = "مؤسسة الأمل للمستلزمات", phone = "0554433221", notes = "تعبئة سريعة"))

            // 2. Initial Bag Types
            dao.insertBagType(BagType(clientId = c1Id.toInt(), clientName = "أكياس خليل", typeName = "أكياس خليل (40 مبسم - شفاف)", mouthpiecesPerBag = 40, emptyBagTareGrams = 4.5, avgMouthpieceWeightGrams = 2.4))
            dao.insertBagType(BagType(clientId = c2Id.toInt(), clientName = "أكياس يحيى", typeName = "أكياس يحيى (40 مبسم - أزرق)", mouthpiecesPerBag = 40, emptyBagTareGrams = 5.0, avgMouthpieceWeightGrams = 2.5))
            dao.insertBagType(BagType(clientId = c3Id.toInt(), clientName = "مؤسسة الأمل", typeName = "أكياس الأمل (50 مبسم)", mouthpiecesPerBag = 50, emptyBagTareGrams = 6.0, avgMouthpieceWeightGrams = 2.5))

            // 3. Initial Departments
            val d1Id = dao.insertDepartment(Department(name = "قسم آلة الحقن", code = "INJECTION", isSystem = true))
            val d2Id = dao.insertDepartment(Department(name = "قسم آلة التغليف الآلي", code = "AUTO_PACK", isSystem = true))
            val d3Id = dao.insertDepartment(Department(name = "قسم التعبئة اليدوية", code = "MANUAL_PACK", isSystem = true))

            // 4. Initial Machines
            dao.insertMachine(Machine(name = "ماكينة حقن 01", departmentId = d1Id.toInt(), departmentName = "قسم آلة الحقن"))
            dao.insertMachine(Machine(name = "ماكينة حقن 02", departmentId = d1Id.toInt(), departmentName = "قسم آلة الحقن"))
            dao.insertMachine(Machine(name = "تغليف آلي A", departmentId = d2Id.toInt(), departmentName = "قسم آلة التغليف الآلي"))
            dao.insertMachine(Machine(name = "تغليف آلي B", departmentId = d2Id.toInt(), departmentName = "قسم آلة التغليف الآلي"))

            // 5. Initial Shifts
            dao.insertShift(Shift(name = "وردية صباحية", startTime = "08:00", endTime = "16:00"))
            dao.insertShift(Shift(name = "وردية مسائية", startTime = "16:00", endTime = "00:00"))
            dao.insertShift(Shift(name = "وردية ليلية", startTime = "00:00", endTime = "08:00"))

            // 6. Initial Workers
            dao.insertWorker(Worker(name = "أحمد المحمد", role = "مشرف حقن"))
            dao.insertWorker(Worker(name = "سامر علي", role = "عامل تغليف آلي"))
            dao.insertWorker(Worker(name = "محمود حسن", role = "عامل تعبئة يدوية"))
            dao.insertWorker(Worker(name = "خالد النجار", role = "عامل تشغيل"))

            // 7. Initial Seed Sample Records for immediate rich visualization
            // Module A Sample
            dao.insertInjectionRecord(
                InjectionRecord(
                    date = todayStr,
                    machineId = 1,
                    machineName = "ماكينة حقن 01",
                    shiftId = 1,
                    shiftName = "وردية صباحية",
                    operatorId = 1,
                    operatorName = "أحمد المحمد",
                    rawMaterialWeightKg = 150.0,
                    finishedMouthpiecesWeightKg = 142.5,
                    wasteWeightKg = 7.5,
                    yieldPercentage = 95.0,
                    notes = "تشغيل ممتاز بحبيبات البولي بروبيلين"
                )
            )

            // Module B Sample
            dao.insertAutoPackagingRecord(
                AutoPackagingRecord(
                    date = todayStr,
                    machineId = 3,
                    machineName = "تغليف آلي A",
                    shiftId = 1,
                    shiftName = "وردية صباحية",
                    workerNames = "سامر علي، خالد النجار",
                    startAccumulatedWeightKg = 1200.0,
                    endAccumulatedWeightKg = 1285.0,
                    netShiftWeightKg = 85.0,
                    notes = "تسجيل إنتاج نهاية الوردية الصباحية"
                )
            )

            // Module C Sample ("أكياس خليل" and "أكياس يحيى")
            dao.insertManualPackagingRecord(
                ManualPackagingRecord(
                    date = todayStr,
                    clientId = c1Id.toInt(),
                    clientName = "أكياس خليل",
                    bagTypeId = 1,
                    bagTypeName = "أكياس خليل (40 مبسم - شفاف)",
                    shiftId = 1,
                    shiftName = "وردية صباحية",
                    workerNames = "محمود حسن",
                    emptyBagsTareGrams = 4.5,
                    totalEmptyBagsWeightGrams = 225.0, // 50 bags * 4.5g
                    unpackedMouthpiecesWeightKg = 5.2,
                    totalPackedBagsWeightKg = 5.225, // 5kg mouthpieces + 0.225kg empty bags
                    mouthpiecesPerBag = 40,
                    calculatedTotalBags = 50, // 50 bags
                    calculatedTotalMouthpieces = 2000, // 2000 mouthpieces
                    netMouthpiecesWeightKg = 5.0,
                    packagingLossKg = 0.2,
                    notes = "دفعة تعبئة يدوي 40 مبسم للكيس لصالح أكياس خليل"
                )
            )

            dao.insertManualPackagingRecord(
                ManualPackagingRecord(
                    date = todayStr,
                    clientId = c2Id.toInt(),
                    clientName = "أكياس يحيى",
                    bagTypeId = 2,
                    bagTypeName = "أكياس يحيى (40 مبسم - أزرق)",
                    shiftId = 2,
                    shiftName = "وردية مسائية",
                    workerNames = "محمود حسن، سامر علي",
                    emptyBagsTareGrams = 5.0,
                    totalEmptyBagsWeightGrams = 375.0, // 75 bags * 5.0g
                    unpackedMouthpiecesWeightKg = 7.8,
                    totalPackedBagsWeightKg = 7.875,
                    mouthpiecesPerBag = 40,
                    calculatedTotalBags = 75,
                    calculatedTotalMouthpieces = 3000,
                    netMouthpiecesWeightKg = 7.5,
                    packagingLossKg = 0.3,
                    notes = "دفعة تعبئة أكياس يحيى 40 مبسم"
                )
            )
        }
    }
}
