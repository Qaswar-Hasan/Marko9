package com.example.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FactoryAnalyticsState
import com.example.data.local.entities.AutoPackagingRecord
import com.example.data.local.entities.InjectionRecord
import com.example.data.local.entities.ManualPackagingRecord
import com.example.ui.components.StatCard

@Composable
fun DashboardScreen(
    analytics: FactoryAnalyticsState,
    injectionRecords: List<InjectionRecord>,
    autoPackagingRecords: List<AutoPackagingRecord>,
    manualPackagingRecords: List<ManualPackagingRecord>,
    onNavigateToModuleA: () -> Unit,
    onNavigateToModuleB: () -> Unit,
    onNavigateToModuleC: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToMasterData: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Hero Banner / Summary Cards Grid (Professional Polish Design)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("M", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "مصنع المباسم البلاستيكية",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "لوحة التحكم • التشغيل المباشر",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Total output container
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "إجمالي الخرج الصافي",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        "%.1f".format(analytics.totalInjectionFinishedKg + analytics.totalAutoPackKg),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("كجم", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }

                        // Waste Yield container
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "نسبة الكفاءة العامة",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        "%.1f".format(analytics.avgInjectionYieldPct),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Cards (Quick Module Entry)
        item {
            Text(
                text = "اقسام الإنتاج والتسجيل",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickModuleCard(
                    title = "قسم الحقن",
                    subtitle = "وزن المواد والخرج",
                    icon = Icons.Default.PrecisionManufacturing,
                    bgColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToModuleA
                )
                QuickModuleCard(
                    title = "التغليف الآلي",
                    subtitle = "الورديات والعمال",
                    icon = Icons.Default.Speed,
                    bgColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToModuleB
                )
                QuickModuleCard(
                    title = "تعبئة 40 مبسم",
                    subtitle = "تصنيف العملاء",
                    icon = Icons.Default.ShoppingBag,
                    bgColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToModuleC
                )
            }
        }

        // Stats Overview Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "المواد الأولية للحقن",
                    value = "%.1f كغ".format(analytics.totalInjectionRawKg),
                    subtitle = "الهدر: %.1f كغ".format(analytics.totalInjectionWasteKg),
                    icon = Icons.Default.Inventory,
                    iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTintColor = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    title = "إنتاج التغليف الآلي",
                    value = "%.1f كغ".format(analytics.totalAutoPackKg),
                    subtitle = "الورديات التراكمية",
                    icon = Icons.Default.Speed,
                    iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconTintColor = MaterialTheme.colorScheme.secondary
                )
                StatCard(
                    title = "التعبئة اليدوية (40 مبسم/كيس)",
                    value = "${analytics.totalManualBagsCount} كيس (${analytics.totalManualMouthpiecesCount} مبسم)",
                    subtitle = "صافي الوزن: %.1f كغ".format(analytics.totalManualPackKg),
                    icon = Icons.Default.ShoppingBag,
                    iconBgColor = Color(0xFFD1FAE5),
                    iconTintColor = Color(0xFF059669)
                )
            }
        }

        // Quick Admin & Reports buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedCard(
                    onClick = onNavigateToReports,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("التقارير والإحصائيات", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("تحليل العملاء والورديات", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                OutlinedCard(
                    onClick = onNavigateToMasterData,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("البيانات الأساسية", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("العملاء، الآلات، الورديات", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Recent Activity Feed
        item {
            Text(
                text = "آخر عمليات الإنتاج المسجلة",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (injectionRecords.isEmpty() && autoPackagingRecords.isEmpty() && manualPackagingRecords.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("لا توجد سجلات إنتاج مدخلة بعد. استخدم الأقسام أعلاه لإضافة أول سجل.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            // Show recent Manual Packaging (Module C) entries
            items(manualPackagingRecords.take(3)) { rec ->
                RecentLogCard(
                    badgeText = "تعبئة يدوية",
                    title = rec.clientName,
                    details = "${rec.calculatedTotalBags} كيس (${rec.calculatedTotalMouthpieces} مبسم) - صافي: %.2f كغ".format(rec.netMouthpiecesWeightKg),
                    subDetails = "${rec.shiftName} | العمال: ${rec.workerNames}",
                    badgeBgColor = Color(0xFFFEF3C7),
                    badgeTextColor = Color(0xFFD97706)
                )
            }

            // Show recent Injection (Module A) entries
            items(injectionRecords.take(3)) { rec ->
                RecentLogCard(
                    badgeText = "حقن",
                    title = rec.machineName,
                    details = "خرج: %.1f كغ (إنتاجية: %.1f%%)".format(rec.finishedMouthpiecesWeightKg, rec.yieldPercentage),
                    subDetails = "${rec.shiftName} | المشغل: ${rec.operatorName}",
                    badgeBgColor = Color(0xFFE0F2FE),
                    badgeTextColor = Color(0xFF0369A1)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun QuickModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    contentColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = contentColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp), color = contentColor)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = contentColor.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun RecentLogCard(
    badgeText: String,
    title: String,
    details: String,
    subDetails: String,
    badgeBgColor: Color,
    badgeTextColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = badgeBgColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = badgeTextColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(text = details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = subDetails, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
