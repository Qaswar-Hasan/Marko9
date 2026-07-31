package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FactoryViewModel
import com.example.ui.components.ArabicRtlWrapper
import com.example.ui.screens.AutoPackagingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InjectionScreen
import com.example.ui.screens.ManualPackagingScreen
import com.example.ui.screens.MasterDataScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.MouthpieceFactoryTheme

data class NavTabItem(
    val title: String,
    val icon: ImageVector,
    val routeIndex: Int
)

class MainActivity : ComponentActivity() {

    private val viewModel: FactoryViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MouthpieceFactoryTheme {
                ArabicRtlWrapper {
                    var currentTabIndex by remember { mutableIntStateOf(0) }

                    val clients by viewModel.clients.collectAsStateWithLifecycle()
                    val bagTypes by viewModel.bagTypes.collectAsStateWithLifecycle()
                    val departments by viewModel.departments.collectAsStateWithLifecycle()
                    val machines by viewModel.machines.collectAsStateWithLifecycle()
                    val shifts by viewModel.shifts.collectAsStateWithLifecycle()
                    val workers by viewModel.workers.collectAsStateWithLifecycle()

                    val injectionRecords by viewModel.injectionRecords.collectAsStateWithLifecycle()
                    val autoPackagingRecords by viewModel.autoPackagingRecords.collectAsStateWithLifecycle()
                    val manualPackagingRecords by viewModel.manualPackagingRecords.collectAsStateWithLifecycle()

                    val analyticsState by viewModel.analyticsState.collectAsStateWithLifecycle()

                    val navTabs = listOf(
                        NavTabItem("الرئيسية", Icons.Default.Home, 0),
                        NavTabItem("الحقن", Icons.Default.PrecisionManufacturing, 1),
                        NavTabItem("تغليف آلي", Icons.Default.Speed, 2),
                        NavTabItem("تعبئة 40", Icons.Default.ShoppingBag, 3),
                        NavTabItem("التقارير", Icons.Default.Assessment, 4),
                        NavTabItem("البيانات", Icons.Default.Build, 5)
                    )

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = when (currentTabIndex) {
                                            0 -> "نظام إدارة مصنع المباسم"
                                            1 -> "قسم آلة الحقن (Module A)"
                                            2 -> "قسم التغليف الآلي (Module B)"
                                            3 -> "التعبئة اليدوية - 40 مبسم (Module C)"
                                            4 -> "التقارير والتحليلات"
                                            5 -> "إدارة البيانات الأساسية"
                                            else -> "مصنع المباسم"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                navTabs.forEach { tab ->
                                    NavigationBarItem(
                                        selected = currentTabIndex == tab.routeIndex,
                                        onClick = { currentTabIndex = tab.routeIndex },
                                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                                        label = { Text(tab.title, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTabIndex) {
                                0 -> DashboardScreen(
                                    analytics = analyticsState,
                                    injectionRecords = injectionRecords,
                                    autoPackagingRecords = autoPackagingRecords,
                                    manualPackagingRecords = manualPackagingRecords,
                                    onNavigateToModuleA = { currentTabIndex = 1 },
                                    onNavigateToModuleB = { currentTabIndex = 2 },
                                    onNavigateToModuleC = { currentTabIndex = 3 },
                                    onNavigateToReports = { currentTabIndex = 4 },
                                    onNavigateToMasterData = { currentTabIndex = 5 }
                                )

                                1 -> InjectionScreen(
                                    machines = machines,
                                    shifts = shifts,
                                    workers = workers,
                                    records = injectionRecords,
                                    onSaveRecord = viewModel::addInjectionRecord,
                                    onDeleteRecord = viewModel::deleteInjectionRecord
                                )

                                2 -> AutoPackagingScreen(
                                    machines = machines,
                                    shifts = shifts,
                                    workers = workers,
                                    records = autoPackagingRecords,
                                    onSaveRecord = viewModel::addAutoPackagingRecord,
                                    onDeleteRecord = viewModel::deleteAutoPackagingRecord
                                )

                                3 -> ManualPackagingScreen(
                                    clients = clients,
                                    bagTypes = bagTypes,
                                    shifts = shifts,
                                    workers = workers,
                                    records = manualPackagingRecords,
                                    onSaveRecord = viewModel::addManualPackagingRecord,
                                    onDeleteRecord = viewModel::deleteManualPackagingRecord
                                )

                                4 -> ReportsScreen(
                                    analytics = analyticsState,
                                    onGenerateArabicReportText = {
                                        viewModel.generateFormattedArabicReport(analyticsState)
                                    }
                                )

                                5 -> MasterDataScreen(
                                    clients = clients,
                                    bagTypes = bagTypes,
                                    departments = departments,
                                    machines = machines,
                                    shifts = shifts,
                                    workers = workers,
                                    onAddClient = viewModel::addClient,
                                    onDeleteClient = viewModel::deleteClient,
                                    onAddBagType = viewModel::addBagType,
                                    onDeleteBagType = viewModel::deleteBagType,
                                    onAddDepartment = viewModel::addDepartment,
                                    onDeleteDepartment = viewModel::deleteDepartment,
                                    onAddMachine = viewModel::addMachine,
                                    onDeleteMachine = viewModel::deleteMachine,
                                    onAddShift = viewModel::addShift,
                                    onDeleteShift = viewModel::deleteShift,
                                    onAddWorker = viewModel::addWorker,
                                    onDeleteWorker = viewModel::deleteWorker
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
