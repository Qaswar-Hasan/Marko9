package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.InjectionRecord
import com.example.data.local.entities.Machine
import com.example.data.local.entities.Shift
import com.example.data.local.entities.Worker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InjectionScreen(
    machines: List<Machine>,
    shifts: List<Shift>,
    workers: List<Worker>,
    records: List<InjectionRecord>,
    onSaveRecord: (
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
    ) -> Unit,
    onDeleteRecord: (InjectionRecord) -> Unit
) {
    val context = LocalContext.current

    // Filter injection machines
    val injectionMachines = remember(machines) {
        machines.filter { it.departmentName.contains("حقن") || it.name.contains("حقن") }.ifEmpty { machines }
    }

    var selectedMachine by remember(injectionMachines) { mutableStateOf(injectionMachines.firstOrNull()) }
    var machineExpanded by remember { mutableStateOf(false) }

    var selectedShift by remember(shifts) { mutableStateOf(shifts.firstOrNull()) }
    var shiftExpanded by remember { mutableStateOf(false) }

    var selectedWorker by remember(workers) { mutableStateOf(workers.firstOrNull()) }
    var workerExpanded by remember { mutableStateOf(false) }

    var rawMaterialText by remember { mutableStateOf("") }
    var finishedText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    // Live Calculations
    val rawKg by remember(rawMaterialText) { derivedStateOf { rawMaterialText.toDoubleOrNull() ?: 0.0 } }
    val finishedKg by remember(finishedText) { derivedStateOf { finishedText.toDoubleOrNull() ?: 0.0 } }
    val wasteKg by remember(rawKg, finishedKg) { derivedStateOf { (rawKg - finishedKg).coerceAtLeast(0.0) } }
    val yieldPct by remember(rawKg, finishedKg) { derivedStateOf { if (rawKg > 0) (finishedKg / rawKg) * 100.0 else 0.0 } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PrecisionManufacturing, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تسجيل إنتاج قسم الحقن (Module A)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Machine Dropdown
                    ExposedDropdownMenuBox(
                        expanded = machineExpanded,
                        onExpandedChange = { machineExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedMachine?.name ?: "اختر الآلة",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("آلة الحقن (Machine ID)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = machineExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = machineExpanded,
                            onDismissRequest = { machineExpanded = false }
                        ) {
                            injectionMachines.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.name) },
                                    onClick = {
                                        selectedMachine = m
                                        machineExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Shift & Operator Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = shiftExpanded,
                            onExpandedChange = { shiftExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedShift?.name ?: "الوردية",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("الوردية (Shift)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shiftExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = shiftExpanded,
                                onDismissRequest = { shiftExpanded = false }
                            ) {
                                shifts.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s.name) },
                                        onClick = {
                                            selectedShift = s
                                            shiftExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = workerExpanded,
                            onExpandedChange = { workerExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedWorker?.name ?: "المشغل",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("عامل التشغيل") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = workerExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = workerExpanded,
                                onDismissRequest = { workerExpanded = false }
                            ) {
                                workers.forEach { w ->
                                    DropdownMenuItem(
                                        text = { Text(w.name) },
                                        onClick = {
                                            selectedWorker = w
                                            workerExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Input Weights
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = rawMaterialText,
                            onValueChange = { rawMaterialText = it },
                            label = { Text("المواد الأولية (كغ)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = finishedText,
                            onValueChange = { finishedText = it },
                            label = { Text("خرج المباسم (كغ)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Live Auto-calculated Analytics Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("وزن الهدر الصافي", style = MaterialTheme.typography.labelSmall)
                                Text("%.2f كغ".format(wasteKg), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("نسبة الإنتاجية (Yield %)", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "%.1f%%".format(yieldPct),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (yieldPct >= 90) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("ملاحظات التشغيل (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (selectedMachine == null || selectedShift == null || selectedWorker == null) {
                                Toast.makeText(context, "يرجى اختيار الآلة والوردية والمشغل", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (rawKg <= 0 || finishedKg <= 0) {
                                Toast.makeText(context, "يرجى إدخال أوزان صحيحة أكبر من الصفر", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            onSaveRecord(
                                "",
                                selectedMachine!!.id,
                                selectedMachine!!.name,
                                selectedShift!!.id,
                                selectedShift!!.name,
                                selectedWorker!!.id,
                                selectedWorker!!.name,
                                rawKg,
                                finishedKg,
                                notesText
                            )

                            // Clear inputs
                            rawMaterialText = ""
                            finishedText = ""
                            notesText = ""
                            Toast.makeText(context, "تم تسجيل دفعة الحقن بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ دفعة الحقن", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Previous Logs Section
        item {
            Text(
                text = "سجلات الحقن السابقة (${records.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(records) { rec ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(rec.machineName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("(${rec.shiftName})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "مواد أولية: %.1f كغ | خرج: %.1f كغ | هدر: %.1f كغ".format(rec.rawMaterialWeightKg, rec.finishedMouthpiecesWeightKg, rec.wasteWeightKg),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "المشغل: ${rec.operatorName} | التاريخ: ${rec.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!rec.notes.isNullOrBlank()) {
                            Text("ملاحظة: ${rec.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (rec.yieldPercentage >= 90) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "%.1f%%".format(rec.yieldPercentage),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (rec.yieldPercentage >= 90) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        }
                        IconButton(onClick = { onDeleteRecord(rec) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
