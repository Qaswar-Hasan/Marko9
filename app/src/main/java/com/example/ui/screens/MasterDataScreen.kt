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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.entities.BagType
import com.example.data.local.entities.Client
import com.example.data.local.entities.Department
import com.example.data.local.entities.Machine
import com.example.data.local.entities.Shift
import com.example.data.local.entities.Worker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterDataScreen(
    clients: List<Client>,
    bagTypes: List<BagType>,
    departments: List<Department>,
    machines: List<Machine>,
    shifts: List<Shift>,
    workers: List<Worker>,
    onAddClient: (name: String, phone: String?, notes: String?) -> Unit,
    onDeleteClient: (Client) -> Unit,
    onAddBagType: (clientId: Int?, clientName: String, typeName: String, mouthpiecesPerBag: Int, tareGrams: Double, avgWeightGrams: Double, notes: String?) -> Unit,
    onDeleteBagType: (BagType) -> Unit,
    onAddDepartment: (name: String, code: String) -> Unit,
    onDeleteDepartment: (Department) -> Unit,
    onAddMachine: (name: String, departmentId: Int, departmentName: String) -> Unit,
    onDeleteMachine: (Machine) -> Unit,
    onAddShift: (name: String, startTime: String, endTime: String) -> Unit,
    onDeleteShift: (Shift) -> Unit,
    onAddWorker: (name: String, role: String, phone: String?) -> Unit,
    onDeleteWorker: (Worker) -> Unit
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("العملاء", "أنواع الأكياس", "القطاعات", "الآلات", "الورديات", "العمال")

    // Dialog state for adding
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "إدارة البيانات الأساسية (Master Data)",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة جديد")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        ScrollableTabRow(selectedTabIndex = selectedTabIndex, edgePadding = 0.dp) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Contents
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedTabIndex) {
                0 -> { // Clients
                    items(clients) { client ->
                        MasterItemCard(
                            title = client.name,
                            subtitle = "الهاتف: ${client.phone ?: "غير محدد"} | ${client.notes ?: ""}",
                            icon = Icons.Default.Group,
                            onDelete = { onDeleteClient(client) }
                        )
                    }
                }
                1 -> { // Bag Types
                    items(bagTypes) { bt ->
                        MasterItemCard(
                            title = bt.typeName,
                            subtitle = "العميل: ${bt.clientName} | ${bt.mouthpiecesPerBag} مبسم | تار الكيس: ${bt.emptyBagTareGrams} غ",
                            icon = Icons.Default.ShoppingBag,
                            onDelete = { onDeleteBagType(bt) }
                        )
                    }
                }
                2 -> { // Departments
                    items(departments) { dep ->
                        MasterItemCard(
                            title = dep.name,
                            subtitle = "الرمز: ${dep.code}",
                            icon = Icons.Default.Build,
                            onDelete = { onDeleteDepartment(dep) }
                        )
                    }
                }
                3 -> { // Machines
                    items(machines) { m ->
                        MasterItemCard(
                            title = m.name,
                            subtitle = "القسم: ${m.departmentName} | الحالة: ${m.status}",
                            icon = Icons.Default.PrecisionManufacturing,
                            onDelete = { onDeleteMachine(m) }
                        )
                    }
                }
                4 -> { // Shifts
                    items(shifts) { shift ->
                        MasterItemCard(
                            title = shift.name,
                            subtitle = "التوقيت: ${shift.startTime} - ${shift.endTime}",
                            icon = Icons.Default.Schedule,
                            onDelete = { onDeleteShift(shift) }
                        )
                    }
                }
                5 -> { // Workers
                    items(workers) { worker ->
                        MasterItemCard(
                            title = worker.name,
                            subtitle = "الدور: ${worker.role} | ${worker.phone ?: ""}",
                            icon = Icons.Default.Person,
                            onDelete = { onDeleteWorker(worker) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMasterDataDialog(
            tabIndex = selectedTabIndex,
            clients = clients,
            departments = departments,
            onDismiss = { showAddDialog = false },
            onAddClient = onAddClient,
            onAddBagType = onAddBagType,
            onAddDepartment = onAddDepartment,
            onAddMachine = onAddMachine,
            onAddShift = onAddShift,
            onAddWorker = onAddWorker
        )
    }
}

@Composable
fun MasterItemCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onDelete: () -> Unit
) {
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
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMasterDataDialog(
    tabIndex: Int,
    clients: List<Client>,
    departments: List<Department>,
    onDismiss: () -> Unit,
    onAddClient: (name: String, phone: String?, notes: String?) -> Unit,
    onAddBagType: (clientId: Int?, clientName: String, typeName: String, mouthpiecesPerBag: Int, tareGrams: Double, avgWeightGrams: Double, notes: String?) -> Unit,
    onAddDepartment: (name: String, code: String) -> Unit,
    onAddMachine: (name: String, departmentId: Int, departmentName: String) -> Unit,
    onAddShift: (name: String, startTime: String, endTime: String) -> Unit,
    onAddWorker: (name: String, role: String, phone: String?) -> Unit
) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }

    var selectedClient by remember(clients) { mutableStateOf(clients.firstOrNull()) }
    var clientExpanded by remember { mutableStateOf(false) }

    var selectedDepartment by remember(departments) { mutableStateOf(departments.firstOrNull()) }
    var depExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "إضافة عنصر جديد", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                when (tabIndex) {
                    0 -> { // Client
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("اسم العميل / أكياس (مثال: أكياس خليل)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = field2, onValueChange = { field2 = it }, label = { Text("رقم الهاتف") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = field3, onValueChange = { field3 = it }, label = { Text("ملاحظات") }, modifier = Modifier.fillMaxWidth())
                    }
                    1 -> { // Bag Type
                        ExposedDropdownMenuBox(expanded = clientExpanded, onExpandedChange = { clientExpanded = it }) {
                            OutlinedTextField(
                                value = selectedClient?.name ?: "اختر العميل",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("العميل المرتبط") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = clientExpanded, onDismissRequest = { clientExpanded = false }) {
                                clients.forEach { c ->
                                    DropdownMenuItem(text = { Text(c.name) }, onClick = { selectedClient = c; clientExpanded = false })
                                }
                            }
                        }
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("اسم نوع الكيس (مثال: أكياس خليل 40 مبسم)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = field2, onValueChange = { field2 = it }, label = { Text("وزن الكيس فارغ (غرام)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    }
                    2 -> { // Department
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("اسم القسم (مثال: قسم التعبئة اليدوية)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = field2, onValueChange = { field2 = it }, label = { Text("رمز القسم") }, modifier = Modifier.fillMaxWidth())
                    }
                    3 -> { // Machine
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("اسم أو رقم الماكينة (مثال: حقن 03)") }, modifier = Modifier.fillMaxWidth())
                        ExposedDropdownMenuBox(expanded = depExpanded, onExpandedChange = { depExpanded = it }) {
                            OutlinedTextField(
                                value = selectedDepartment?.name ?: "اختر القسم",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("القسم التابع له") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = depExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = depExpanded, onDismissRequest = { depExpanded = false }) {
                                departments.forEach { d ->
                                    DropdownMenuItem(text = { Text(d.name) }, onClick = { selectedDepartment = d; depExpanded = false })
                                }
                            }
                        }
                    }
                    4 -> { // Shift
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("اسم الوردية (مثال: وردية صباحية)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = field2, onValueChange = { field2 = it }, label = { Text("وقت البداية (مثال: 08:00)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = field3, onValueChange = { field3 = it }, label = { Text("وقت النهاية (مثال: 16:00)") }, modifier = Modifier.fillMaxWidth())
                    }
                    5 -> { // Worker
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("اسم العامل") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = field2, onValueChange = { field2 = it }, label = { Text("الدور / الوظيفة") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = field3, onValueChange = { field3 = it }, label = { Text("رقم الهاتف") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (tabIndex) {
                        0 -> onAddClient(field1, field2.ifBlank { null }, field3.ifBlank { null })
                        1 -> onAddBagType(selectedClient?.id, selectedClient?.name ?: "عميل عام", field1, 40, field2.toDoubleOrNull() ?: 4.5, 2.5, null)
                        2 -> onAddDepartment(field1, field2)
                        3 -> selectedDepartment?.let { onAddMachine(field1, it.id, it.name) }
                        4 -> onAddShift(field1, field2.ifBlank { "08:00" }, field3.ifBlank { "16:00" })
                        5 -> onAddWorker(field1, field2.ifBlank { "عامل تشغيل" }, field3.ifBlank { null })
                    }
                    onDismiss()
                }
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
