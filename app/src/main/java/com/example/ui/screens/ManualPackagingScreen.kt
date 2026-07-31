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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.BagType
import com.example.data.local.entities.Client
import com.example.data.local.entities.ManualPackagingRecord
import com.example.data.local.entities.Shift
import com.example.data.local.entities.Worker
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualPackagingScreen(
    clients: List<Client>,
    bagTypes: List<BagType>,
    shifts: List<Shift>,
    workers: List<Worker>,
    records: List<ManualPackagingRecord>,
    onSaveRecord: (
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
    ) -> Unit,
    onDeleteRecord: (ManualPackagingRecord) -> Unit
) {
    val context = LocalContext.current

    var selectedClient by remember(clients) { mutableStateOf(clients.firstOrNull()) }
    var clientExpanded by remember { mutableStateOf(false) }

    val clientBagTypes = remember(selectedClient, bagTypes) {
        if (selectedClient == null) bagTypes else bagTypes.filter { it.clientId == selectedClient?.id || it.clientName == selectedClient?.name }
    }
    var selectedBagType by remember(clientBagTypes) { mutableStateOf(clientBagTypes.firstOrNull()) }
    var bagTypeExpanded by remember { mutableStateOf(false) }

    var selectedShift by remember(shifts) { mutableStateOf(shifts.firstOrNull()) }
    var shiftExpanded by remember { mutableStateOf(false) }

    var selectedWorker by remember(workers) { mutableStateOf(workers.firstOrNull()) }
    var workerExpanded by remember { mutableStateOf(false) }

    var tarePerBagText by remember { mutableStateOf("4.5") }
    var mouthpiecesPerBagText by remember { mutableStateOf("40") }
    var unpackedWeightText by remember { mutableStateOf("") }
    var totalPackedWeightText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    // Computations
    val tareGrams by remember(tarePerBagText) { derivedStateOf { tarePerBagText.toDoubleOrNull() ?: 4.5 } }
    val mouthpiecesPerBag by remember(mouthpiecesPerBagText) { derivedStateOf { mouthpiecesPerBagText.toIntOrNull() ?: 40 } }
    val unpackedKg by remember(unpackedWeightText) { derivedStateOf { unpackedWeightText.toDoubleOrNull() ?: 0.0 } }
    val totalPackedKg by remember(totalPackedWeightText) { derivedStateOf { totalPackedWeightText.toDoubleOrNull() ?: 0.0 } }

    // Estimate bag count & net weight
    val avgMouthpieceWeightGrams = selectedBagType?.avgMouthpieceWeightGrams ?: 2.5
    val bagUnitTotalGrams = (mouthpiecesPerBag * avgMouthpieceWeightGrams) + tareGrams
    val calculatedBagsCount by remember(totalPackedKg, bagUnitTotalGrams) {
        derivedStateOf {
            if (bagUnitTotalGrams > 0 && totalPackedKg > 0) {
                ((totalPackedKg * 1000.0) / bagUnitTotalGrams).roundToInt()
            } else 0
        }
    }
    val calculatedTotalMouthpieces by remember(calculatedBagsCount, mouthpiecesPerBag) {
        derivedStateOf { calculatedBagsCount * mouthpiecesPerBag }
    }
    val totalEmptyBagsTareKg by remember(calculatedBagsCount, tareGrams) {
        derivedStateOf { (calculatedBagsCount * tareGrams) / 1000.0 }
    }
    val netMouthpiecesKg by remember(totalPackedKg, totalEmptyBagsTareKg) {
        derivedStateOf { (totalPackedKg - totalEmptyBagsTareKg).coerceAtLeast(0.0) }
    }
    val packagingLossKg by remember(unpackedKg, netMouthpiecesKg) {
        derivedStateOf { (unpackedKg - netMouthpiecesKg).coerceAtLeast(0.0) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Input Form
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
                        Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFFE36414))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "قسم التعبئة اليدوية - 40 مبسم (Module C)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE36414)
                        )
                    }

                    // Client Selection (أكياس خليل, أكياس يحيى, etc)
                    ExposedDropdownMenuBox(
                        expanded = clientExpanded,
                        onExpandedChange = { clientExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedClient?.name ?: "اختر العميل / أكياس التعبئة",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("العميل / تصنيف الكيس (Client)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = clientExpanded,
                            onDismissRequest = { clientExpanded = false }
                        ) {
                            clients.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.name) },
                                    onClick = {
                                        selectedClient = c
                                        clientExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Bag Type & Shift
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = bagTypeExpanded,
                            onExpandedChange = { bagTypeExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedBagType?.typeName ?: "مواصفات الكيس",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("نوع الكيس") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bagTypeExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = bagTypeExpanded,
                                onDismissRequest = { bagTypeExpanded = false }
                            ) {
                                clientBagTypes.forEach { bt ->
                                    DropdownMenuItem(
                                        text = { Text(bt.typeName) },
                                        onClick = {
                                            selectedBagType = bt
                                            tarePerBagText = bt.emptyBagTareGrams.toString()
                                            mouthpiecesPerBagText = bt.mouthpiecesPerBag.toString()
                                            bagTypeExpanded = false
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
                                label = { Text("الوردية") },
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

                    // Worker selection & Tare weights
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = workerExpanded,
                            onExpandedChange = { workerExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedWorker?.name ?: "عامل التعبئة",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("عامل التعبئة") },
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

                        OutlinedTextField(
                            value = tarePerBagText,
                            onValueChange = { tarePerBagText = it },
                            label = { Text("وزن الكيس فارغ (غرام)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Weights entry: Unpacked mouthpieces vs Total Packed Bags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = unpackedWeightText,
                            onValueChange = { unpackedWeightText = it },
                            label = { Text("وزن المباسم قبل التعبئة (كغ)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = totalPackedWeightText,
                            onValueChange = { totalPackedWeightText = it },
                            label = { Text("الوزن الكلي للأكياس المعبأة (كغ)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Real-Time Calculation Preview Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "نتائج الحساب الآلي للدفعة (${selectedClient?.name ?: "العميل"})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("عدد الأكياس المعبأة:", style = MaterialTheme.typography.labelSmall)
                                    Text("$calculatedBagsCount كيس", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("إجمالي عدد المباسم (40/كيس):", style = MaterialTheme.typography.labelSmall)
                                    Text("$calculatedTotalMouthpieces مبسم", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("صافي وزن المباسم:", style = MaterialTheme.typography.labelSmall)
                                    Text("%.2f كغ".format(netMouthpiecesKg), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF059669)))
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("ملاحظات التعبئة اليدوية") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (selectedClient == null || selectedShift == null || selectedWorker == null) {
                                Toast.makeText(context, "يرجى اختيار العميل والوردية والعامل", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (totalPackedKg <= 0 || calculatedBagsCount <= 0) {
                                Toast.makeText(context, "يرجى إدخال وزن الأكياس المعبأة بشكل صحيح", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            onSaveRecord(
                                "",
                                selectedClient!!.id,
                                selectedClient!!.name,
                                selectedBagType?.id,
                                selectedBagType?.typeName ?: "كيس قياسي (40 مبسم)",
                                selectedShift!!.id,
                                selectedShift!!.name,
                                selectedWorker!!.name,
                                tareGrams,
                                unpackedKg,
                                totalPackedKg,
                                mouthpiecesPerBag,
                                calculatedBagsCount,
                                notesText
                            )

                            // Clear inputs
                            unpackedWeightText = ""
                            totalPackedWeightText = ""
                            notesText = ""
                            Toast.makeText(context, "تم حفظ دفعة التعبئة اليدوية للعميل بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE36414))
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ دفعة التعبئة اليدوية", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // History list with client badges
        item {
            Text(
                text = "سجلات التعبئة اليدوية وحسابات العملاء (${records.size})",
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
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = rec.clientName,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFD97706)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(rec.bagTypeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "الإنتاج: ${rec.calculatedTotalBags} كيس (${rec.calculatedTotalMouthpieces} مبسم)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            "الوزن الكلي: %.2f كغ | الصافي: %.2f كغ | الفاقد: %.2f كغ".format(rec.totalPackedBagsWeightKg, rec.netMouthpiecesWeightKg, rec.packagingLossKg),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "الوردية: ${rec.shiftName} | العامل: ${rec.workerNames} | ${rec.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
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
