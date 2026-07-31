package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import com.example.data.local.entities.AutoPackagingRecord
import com.example.data.local.entities.Machine
import com.example.data.local.entities.Shift
import com.example.data.local.entities.Worker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoPackagingScreen(
    machines: List<Machine>,
    shifts: List<Shift>,
    workers: List<Worker>,
    records: List<AutoPackagingRecord>,
    onSaveRecord: (
        date: String,
        machineId: Int,
        machineName: String,
        shiftId: Int,
        shiftName: String,
        workerNames: String,
        startAccumulatedKg: Double,
        endAccumulatedKg: Double,
        notes: String?
    ) -> Unit,
    onDeleteRecord: (AutoPackagingRecord) -> Unit
) {
    val context = LocalContext.current

    // Filter packaging machines
    val packMachines = remember(machines) {
        machines.filter { it.departmentName.contains("تغليف") || it.name.contains("تغليف") }.ifEmpty { machines }
    }

    var selectedMachine by remember(packMachines) { mutableStateOf(packMachines.firstOrNull()) }
    var machineExpanded by remember { mutableStateOf(false) }

    var selectedShift by remember(shifts) { mutableStateOf(shifts.firstOrNull()) }
    var shiftExpanded by remember { mutableStateOf(false) }

    // Multi-worker selection (2+ workers per shift)
    val selectedWorkerNames = remember { mutableStateListOf<String>() }

    var startWeightText by remember { mutableStateOf("") }
    var endWeightText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    val startKg by remember(startWeightText) { derivedStateOf { startWeightText.toDoubleOrNull() ?: 0.0 } }
    val endKg by remember(endWeightText) { derivedStateOf { endWeightText.toDoubleOrNull() ?: 0.0 } }
    val netShiftKg by remember(startKg, endKg) { derivedStateOf { (endKg - startKg).coerceAtLeast(0.0) } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Input Form Card
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
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تسجيل آلة التغليف الآلي (Module B)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = machineExpanded,
                            onExpandedChange = { machineExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedMachine?.name ?: "اختر الآلة",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("آلة التغليف الآلي") },
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
                                packMachines.forEach { m ->
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

                        ExposedDropdownMenuBox(
                            expanded = shiftExpanded,
                            onExpandedChange = { shiftExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedShift?.name ?: "الوردية",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("الوردية (صباحية/مسائية)") },
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
                    }

                    // Multi-worker selection section
                    Text(
                        text = "تعيين عمال الوردية (اختر 2+ عمال):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        workers.forEach { worker ->
                            val isSelected = selectedWorkerNames.contains(worker.name)
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    if (isSelected) selectedWorkerNames.remove(worker.name) else selectedWorkerNames.add(worker.name)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.height(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = worker.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Dual-Shift Accumulated Weights
                    Text(
                        text = "تسجيل الوزن التراكمي لآلة التغليف (كغ):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startWeightText,
                            onValueChange = { startWeightText = it },
                            label = { Text("الوزن بداية الوردية (كغ)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endWeightText,
                            onValueChange = { endWeightText = it },
                            label = { Text("الوزن نهاية الوردية (كغ)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Calculated Net Production Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("صافي وزن إنتاج الوردية", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "%.2f كغ".format(netShiftKg),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("ملاحظات التغليف الآلي") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (selectedMachine == null || selectedShift == null) {
                                Toast.makeText(context, "يرجى اختيار الآلة والوردية", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (selectedWorkerNames.isEmpty()) {
                                Toast.makeText(context, "يرجى اختيار عمال الوردية", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (endKg <= startKg) {
                                Toast.makeText(context, "يجب أن يكون الوزن بنهاية الوردية أكبر من الوزن بالبداية", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            onSaveRecord(
                                "",
                                selectedMachine!!.id,
                                selectedMachine!!.name,
                                selectedShift!!.id,
                                selectedShift!!.name,
                                selectedWorkerNames.joinToString("، "),
                                startKg,
                                endKg,
                                notesText
                            )

                            // Reset form
                            startWeightText = ""
                            endWeightText = ""
                            notesText = ""
                            Toast.makeText(context, "تم حفظ سجل التغليف الآلي بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ سجل التغليف الآلي", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // History List
        item {
            Text(
                text = "سجلات التغليف الآلي السابقة (${records.size})",
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
                            Text("(${rec.shiftName})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "صافي الإنتاج: %.2f كغ (بداية: %.1f كغ -> نهاية: %.1f كغ)".format(rec.netShiftWeightKg, rec.startAccumulatedWeightKg, rec.endAccumulatedWeightKg),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            "العمال: ${rec.workerNames} | التاريخ: ${rec.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { onDeleteRecord(rec) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
