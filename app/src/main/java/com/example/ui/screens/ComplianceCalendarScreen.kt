package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComplianceTaskEntity
import com.example.ui.components.FlowAuthorityType
import com.example.ui.components.OfficialFlowBadge
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@Composable
fun ComplianceCalendarScreen(
    tasks: List<ComplianceTaskEntity>,
    scheduledReminderTaskIds: Set<Long> = emptySet(),
    onMarkCompleted: (ComplianceTaskEntity) -> Unit,
    onToggleReminder: (ComplianceTaskEntity, Boolean) -> Unit = { _, _ -> },
    onScheduleAllReminders: () -> Unit = {},
    onTestNotification: (ComplianceTaskEntity) -> Unit = {},
    onAskAi: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("COMPLIANCE AUTOMATION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                Text("Official Indian Statutory Calendar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            OfficialFlowBadge(
                authorityType = FlowAuthorityType.OFFICIAL_GOVERNMENT,
                label = "CBDT • CBIC • MCA"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Local Notification Reminder Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(RoyalGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Statutory Deadline Alerts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${scheduledReminderTaskIds.size} active alert(s) • Local notifications",
                                fontSize = 10.sp,
                                color = RoyalGoldLight
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (scheduledReminderTaskIds.isNotEmpty()) "ENABLED" else "OPTIONAL",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (scheduledReminderTaskIds.isNotEmpty()) EmeraldGreen else Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Receive scheduled local device reminders with statutory penalty warnings before Advance Tax, GSTR-1, and TDS due dates.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onScheduleAllReminders,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Enable All Alerts",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val target = tasks.firstOrNull { it.status == "UPCOMING" } ?: tasks.firstOrNull()
                            if (target != null) {
                                onTestNotification(target)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Alert", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Categories
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("ALL", "INCOME_TAX", "GST", "ROC", "MSME").forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat.replace("_", " "), fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalGold,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredTasks = if (selectedCategory == "ALL") tasks else tasks.filter { it.category == selectedCategory }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredTasks) { task ->
                val isReminderSet = scheduledReminderTaskIds.contains(task.id)
                ComplianceTaskCard(
                    task = task,
                    isReminderSet = isReminderSet,
                    onToggleReminder = { onToggleReminder(task, !isReminderSet) },
                    onTestNotification = { onTestNotification(task) },
                    onMarkCompleted = { onMarkCompleted(task) },
                    onAskAi = onAskAi
                )
            }
        }
    }
}

@Composable
private fun ComplianceTaskCard(
    task: ComplianceTaskEntity,
    isReminderSet: Boolean = false,
    onToggleReminder: () -> Unit = {},
    onTestNotification: () -> Unit = {},
    onMarkCompleted: () -> Unit,
    onAskAi: (String) -> Unit
) {
    val isCompleted = task.status == "COMPLETED"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isCompleted) Color(0xFF0F1A24) else DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) Color(0xFF1E293B) else DarkCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isCompleted) Color(0xFF94A3B8) else Color.White)
                    Text(task.authority, fontSize = 10.sp, color = RoyalGoldLight)
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (isCompleted) Color(0xFF064E3B) else Color(0xFF1E293B),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isCompleted) "COMPLETED" else task.dueDate,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) EmeraldGreen else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Warning
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(task.penaltyWarning, fontSize = 11.sp, color = Color(0xFFF59E0B))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(10.dp))

            // Reminder & Notification Row
            if (!isCompleted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A101C), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onToggleReminder() }
                    ) {
                        Icon(
                            imageVector = if (isReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = "Toggle Reminder",
                            tint = if (isReminderSet) RoyalGold else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isReminderSet) "Statutory Alert Active" else "Set Device Reminder",
                            fontSize = 11.sp,
                            fontWeight = if (isReminderSet) FontWeight.Bold else FontWeight.Normal,
                            color = if (isReminderSet) RoyalGold else Color(0xFFCBD5E1)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .clickable { onTestNotification() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Test Alert",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalGoldLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isCompleted) {
                    Button(
                        onClick = onMarkCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Filed", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                OutlinedButton(
                    onClick = { onAskAi("How do I file ${task.title}? What is the official procedure?") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Official Portal Guide", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}
