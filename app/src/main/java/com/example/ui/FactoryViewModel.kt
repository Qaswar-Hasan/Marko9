package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AutoPackagingRecord
import com.example.data.local.entities.BagType
import com.example.data.local.entities.Client
import com.example.data.local.entities.Department
import com.example.data.local.entities.InjectionRecord
import com.example.data.local.entities.ManualPackagingRecord
import com.example.data.local.entities.Machine
import com.example.data.local.entities.Shift
import com.example.data.local.entities.Worker
import com.example.data.repository.FactoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Analytics & Summary Models
data class ClientReportSummary(
    val clientId: Int,
    val clientName: String,
    val totalBagsCount: Int,
    val totalMouthpiecesCount: Int,
    val totalPackedWeightKg: Double,
    val totalNetMouthpiecesWeightKg: Double,
    val totalLossKg: Double
)

data class ShiftPerformanceSummary(
    val shiftName: String,
    val injectionCount: Int,
    val injectionFinishedKg: Double,
    val autoPackagingKg: Double,
    val manualPackagingKg: Double,
    val manualBagsCount: Int
)

data class FactoryAnalyticsState(
    val totalInjectionRawKg: Double = 0.0,
    val totalInjectionFinishedKg: Double = 0.0,
    val totalInjectionWasteKg: Double = 0.0,
    val avgInjectionYieldPct: Double = 0.0,
    val totalAutoPackKg: Double = 0.0,
    val totalManualPackKg: Double = 0.0,
    val totalManualBagsCount: Int = 0,
    val totalManualMouthpiecesCount: Int = 0,
    val totalPackagingLossKg: Double = 0.0,
    val clientSummaries: List<ClientReportSummary> = emptyList(),
    val shiftSummaries: List<ShiftPerformanceSummary> = emptyList()
)

class FactoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FactoryRepository

    init {
        val dao = AppDatabase.getDatabase(application).factoryDao()
        repository = FactoryRepository(dao)
    }

    val clients: StateFlow<List<Client>> = repository.clients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bagTypes: StateFlow<List<BagType>> = repository.bagTypes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val departments: StateFlow<List<Department>> = repository.departments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val machines: StateFlow<List<Machine>> = repository.machines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shifts: StateFlow<List<Shift>> = repository.shifts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workers: StateFlow<List<Worker>> = repository.workers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val injectionRecords: StateFlow<List<InjectionRecord>> = repository.injectionRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val autoPackagingRecords: StateFlow<List<AutoPackagingRecord>> = repository.autoPackagingRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val manualPackagingRecords: StateFlow<List<ManualPackagingRecord>> = repository.manualPackagingRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combine records into Analytics State
    val analyticsState: StateFlow<FactoryAnalyticsState> = combine(
        injectionRecords,
        autoPackagingRecords,
        manualPackagingRecords,
        clients,
        shifts
    ) { injections, autoPacks, manualPacks, clientList, shiftList ->
        val totalRawKg = injections.sumOf { it.rawMaterialWeightKg }
        val totalFinishedKg = injections.sumOf { it.finishedMouthpiecesWeightKg }
        val totalWasteKg = injections.sumOf { it.wasteWeightKg }
        val avgYield = if (totalRawKg > 0) (totalFinishedKg / totalRawKg) * 100.0 else 0.0

        val totalAutoKg = autoPacks.sumOf { it.netShiftWeightKg }
        val totalManualKg = manualPacks.sumOf { it.netMouthpiecesWeightKg }
        val totalManualBags = manualPacks.sumOf { it.calculatedTotalBags }
        val totalManualMouthpieces = manualPacks.sumOf { it.calculatedTotalMouthpieces }
        val totalPkgLoss = manualPacks.sumOf { it.packagingLossKg }

        // Client Summaries
        val clientSummaries = clientList.map { client ->
            val clientRecords = manualPacks.filter { it.clientId == client.id || it.clientName == client.name }
            ClientReportSummary(
                clientId = client.id,
                clientName = client.name,
                totalBagsCount = clientRecords.sumOf { it.calculatedTotalBags },
                totalMouthpiecesCount = clientRecords.sumOf { it.calculatedTotalMouthpieces },
                totalPackedWeightKg = clientRecords.sumOf { it.totalPackedBagsWeightKg },
                totalNetMouthpiecesWeightKg = clientRecords.sumOf { it.netMouthpiecesWeightKg },
                totalLossKg = clientRecords.sumOf { it.packagingLossKg }
            )
        }

        // Shift Summaries
        val shiftSummaries = shiftList.map { shift ->
            val injShift = injections.filter { it.shiftName == shift.name }
            val autoShift = autoPacks.filter { it.shiftName == shift.name }
            val manShift = manualPacks.filter { it.shiftName == shift.name }

            ShiftPerformanceSummary(
                shiftName = shift.name,
                injectionCount = injShift.size,
                injectionFinishedKg = injShift.sumOf { it.finishedMouthpiecesWeightKg },
                autoPackagingKg = autoShift.sumOf { it.netShiftWeightKg },
                manualPackagingKg = manShift.sumOf { it.netMouthpiecesWeightKg },
                manualBagsCount = manShift.sumOf { it.calculatedTotalBags }
            )
        }

        FactoryAnalyticsState(
            totalInjectionRawKg = totalRawKg,
            totalInjectionFinishedKg = totalFinishedKg,
            totalInjectionWasteKg = totalWasteKg,
            avgInjectionYieldPct = avgYield,
            totalAutoPackKg = totalAutoKg,
            totalManualPackKg = totalManualKg,
            totalManualBagsCount = totalManualBags,
            totalManualMouthpiecesCount = totalManualMouthpieces,
            totalPackagingLossKg = totalPkgLoss,
            clientSummaries = clientSummaries,
            shiftSummaries = shiftSummaries
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FactoryAnalyticsState())

    // === Master Data Management Actions ===
    fun addClient(name: String, phone: String?, notes: String?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertClient(Client(name = name.trim(), phone = phone?.trim(), notes = notes?.trim()))
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch { repository.updateClient(client) }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch { repository.deleteClient(client) }
    }

    fun addBagType(clientId: Int?, clientName: String, typeName: String, mouthpiecesPerBag: Int, tareGrams: Double, avgWeightGrams: Double, notes: String?) {
        if (typeName.isBlank() || clientName.isBlank()) return
        viewModelScope.launch {
            repository.insertBagType(
                BagType(
                    clientId = clientId,
                    clientName = clientName.trim(),
                    typeName = typeName.trim(),
                    mouthpiecesPerBag = mouthpiecesPerBag,
                    emptyBagTareGrams = tareGrams,
                    avgMouthpieceWeightGrams = avgWeightGrams,
                    notes = notes?.trim()
                )
            )
        }
    }

    fun updateBagType(bagType: BagType) {
        viewModelScope.launch { repository.updateBagType(bagType) }
    }

    fun deleteBagType(bagType: BagType) {
        viewModelScope.launch { repository.deleteBagType(bagType) }
    }

    fun addDepartment(name: String, code: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertDepartment(Department(name = name.trim(), code = code.trim()))
        }
    }

    fun deleteDepartment(department: Department) {
        viewModelScope.launch { repository.deleteDepartment(department) }
    }

    fun addMachine(name: String, departmentId: Int, departmentName: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertMachine(Machine(name = name.trim(), departmentId = departmentId, departmentName = departmentName))
        }
    }

    fun deleteMachine(machine: Machine) {
        viewModelScope.launch { repository.deleteMachine(machine) }
    }

    fun addShift(name: String, startTime: String, endTime: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertShift(Shift(name = name.trim(), startTime = startTime.trim(), endTime = endTime.trim()))
        }
    }

    fun deleteShift(shift: Shift) {
        viewModelScope.launch { repository.deleteShift(shift) }
    }

    fun addWorker(name: String, role: String, phone: String?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertWorker(Worker(name = name.trim(), role = role.trim(), phone = phone?.trim()))
        }
    }

    fun deleteWorker(worker: Worker) {
        viewModelScope.launch { repository.deleteWorker(worker) }
    }

    // === Module A: Injection Log Action ===
    fun addInjectionRecord(
        date: String,
        machineId: Int,
        machineName: String,
        shiftId: Int,
        shiftName: String,
        operatorId: Int,
        operatorName: String,
        rawMaterialKg: Double,
        finishedKg: Double,
        notes: String?
    ) {
        viewModelScope.launch {
            val wasteKg = (rawMaterialKg - finishedKg).coerceAtLeast(0.0)
            val yieldPct = if (rawMaterialKg > 0) (finishedKg / rawMaterialKg) * 100.0 else 0.0
            val record = InjectionRecord(
                date = if (date.isBlank()) getTodayDateString() else date,
                machineId = machineId,
                machineName = machineName,
                shiftId = shiftId,
                shiftName = shiftName,
                operatorId = operatorId,
                operatorName = operatorName,
                rawMaterialWeightKg = rawMaterialKg,
                finishedMouthpiecesWeightKg = finishedKg,
                wasteWeightKg = wasteKg,
                yieldPercentage = yieldPct,
                notes = notes?.trim()
            )
            repository.insertInjectionRecord(record)
        }
    }

    fun deleteInjectionRecord(record: InjectionRecord) {
        viewModelScope.launch { repository.deleteInjectionRecord(record) }
    }

    // === Module B: Auto Packaging Log Action ===
    fun addAutoPackagingRecord(
        date: String,
        machineId: Int,
        machineName: String,
        shiftId: Int,
        shiftName: String,
        workerNames: String,
        startAccumulatedKg: Double,
        endAccumulatedKg: Double,
        notes: String?
    ) {
        viewModelScope.launch {
            val netKg = (endAccumulatedKg - startAccumulatedKg).coerceAtLeast(0.0)
            val record = AutoPackagingRecord(
                date = if (date.isBlank()) getTodayDateString() else date,
                machineId = machineId,
                machineName = machineName,
                shiftId = shiftId,
                shiftName = shiftName,
                workerNames = workerNames.trim(),
                startAccumulatedWeightKg = startAccumulatedKg,
                endAccumulatedWeightKg = endAccumulatedKg,
                netShiftWeightKg = netKg,
                notes = notes?.trim()
            )
            repository.insertAutoPackagingRecord(record)
        }
    }

    fun deleteAutoPackagingRecord(record: AutoPackagingRecord) {
        viewModelScope.launch { repository.deleteAutoPackagingRecord(record) }
    }

    // === Module C: Manual Packaging Log Action ===
    fun addManualPackagingRecord(
        date: String,
        clientId: Int,
        clientName: String,
        bagTypeId: Int?,
        bagTypeName: String,
        shiftId: Int,
        shiftName: String,
        workerNames: String,
        emptyBagsTareGrams: Double,
        unpackedMouthpiecesKg: Double,
        totalPackedBagsKg: Double,
        mouthpiecesPerBag: Int,
        calculatedTotalBags: Int,
        notes: String?
    ) {
        viewModelScope.launch {
            val totalTareGrams = calculatedTotalBags * emptyBagsTareGrams
            val totalTareKg = totalTareGrams / 1000.0
            val netMouthpiecesKg = (totalPackedBagsKg - totalTareKg).coerceAtLeast(0.0)
            val calcMouthpiecesCount = calculatedTotalBags * mouthpiecesPerBag
            val lossKg = (unpackedMouthpiecesKg - netMouthpiecesKg).coerceAtLeast(0.0)

            val record = ManualPackagingRecord(
                date = if (date.isBlank()) getTodayDateString() else date,
                clientId = clientId,
                clientName = clientName,
                bagTypeId = bagTypeId,
                bagTypeName = bagTypeName,
                shiftId = shiftId,
                shiftName = shiftName,
                workerNames = workerNames.trim(),
                emptyBagsTareGrams = emptyBagsTareGrams,
                totalEmptyBagsWeightGrams = totalTareGrams,
                unpackedMouthpiecesWeightKg = unpackedMouthpiecesKg,
                totalPackedBagsWeightKg = totalPackedBagsKg,
                mouthpiecesPerBag = mouthpiecesPerBag,
                calculatedTotalBags = calculatedTotalBags,
                calculatedTotalMouthpieces = calcMouthpiecesCount,
                netMouthpiecesWeightKg = netMouthpiecesKg,
                packagingLossKg = lossKg,
                notes = notes?.trim()
            )
            repository.insertManualPackagingRecord(record)
        }
    }

    fun deleteManualPackagingRecord(record: ManualPackagingRecord) {
        viewModelScope.launch { repository.deleteManualPackagingRecord(record) }
    }

    fun generateFormattedArabicReport(analytics: FactoryAnalyticsState): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        val builder = StringBuilder()
        builder.appendLine("🏭 *تقرير إنتاج مصنع المباسم البلاستيكية* 🏭")
        builder.appendLine("📅 التاريخ والوقت: $dateStr")
        builder.appendLine("----------------------------------------")
        builder.appendLine("🔹 *قسم الحقن (Injection):*")
        builder.appendLine("• إجمالي المواد الأولية: %.2f كغ".format(analytics.totalInjectionRawKg))
        builder.appendLine("• إجمالي الخرج الصافي: %.2f كغ".format(analytics.totalInjectionFinishedKg))
        builder.appendLine("• إجمالي الهدر: %.2f كغ".format(analytics.totalInjectionWasteKg))
        builder.appendLine("• متوسط نسبة الإنتاجية: %.1f%%".format(analytics.avgInjectionYieldPct))
        builder.appendLine()
        builder.appendLine("🔹 *قسم التغليف الآلي (Auto Packaging):*")
        builder.appendLine("• إجمالي الوزن المغلف: %.2f كغ".format(analytics.totalAutoPackKg))
        builder.appendLine()
        builder.appendLine("🔹 *قسم التعبئة اليدوية (Manual Packaging 40/bag):*")
        builder.appendLine("• إجمالي الأكياس المعبأة: ${analytics.totalManualBagsCount} كيس")
        builder.appendLine("• إجمالي المباسم المنتجة: ${analytics.totalManualMouthpiecesCount} مبسم")
        builder.appendLine("• صافي وزن المباسم: %.2f كغ".format(analytics.totalManualPackKg))
        builder.appendLine("• فاقد التعبئة اليدوية: %.2f كغ".format(analytics.totalPackagingLossKg))
        builder.appendLine()
        builder.appendLine("👥 *تفاصيل إنتاج العملاء / الأكياس:*")
        analytics.clientSummaries.forEach { c ->
            builder.appendLine("▪️ *${c.clientName}*:")
            builder.appendLine("   - عدد الأكياس: ${c.totalBagsCount} كيس")
            builder.appendLine("   - عدد المباسم: ${c.totalMouthpiecesCount} مبسم")
            builder.appendLine("   - الصافي: %.2f كغ".format(c.totalNetMouthpiecesWeightKg))
        }
        builder.appendLine()
        builder.appendLine("⏱️ *أداء الورديات (Shifts):*")
        analytics.shiftSummaries.forEach { s ->
            builder.appendLine("▪️ *${s.shiftName}*: حقن: %.1f كغ | تغليف آلي: %.1f كغ | تعبئة يدوية: %d كيس (%.1f كغ)".format(
                s.injectionFinishedKg, s.autoPackagingKg, s.manualBagsCount, s.manualPackagingKg
            ))
        }
        builder.appendLine("----------------------------------------")
        builder.appendLine("تم التوليد بواسطة تطبيق إدارة مصنع المباسم")
        return builder.toString()
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}
