package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntityContext
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.AppColors
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MidnightNavy

@Composable
fun GlobalContextBar(
    currentEntity: UserEntityContext,
    allEntities: List<UserEntityContext>,
    onSelectEntity: (UserEntityContext) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEntityMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.topBarBackground)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AppColors.chipBackground)
                .border(1.dp, AppColors.chipBorder, RoundedCornerShape(10.dp))
                .clickable { showEntityMenu = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(AppColors.goldAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (currentEntity.entityType == "Individual") Icons.Default.Person else Icons.Default.Business,
                        contentDescription = null,
                        tint = AppColors.goldAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentEntity.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (AppColors.isDark) Color(0xFF223455) else Color(0xFFFEF3C7),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = currentEntity.entityType,
                                fontSize = 9.sp,
                                color = AppColors.goldAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Text(
                        text = "ID: ${currentEntity.panOrGst} • ${currentEntity.taxYear}",
                        fontSize = 10.sp,
                        color = AppColors.textSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Switch",
                    fontSize = 10.sp,
                    color = AppColors.goldAccent,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Switch Entity",
                    tint = AppColors.goldAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = showEntityMenu,
            onDismissRequest = { showEntityMenu = false },
            modifier = Modifier
                .background(AppColors.surface)
                .border(1.dp, AppColors.cardBorder, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = "Select Operating Entity",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textSecondary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )

            allEntities.forEach { entity ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = entity.name,
                                    color = AppColors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${entity.entityType})",
                                    color = AppColors.goldAccent,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = "PAN/GST: ${entity.panOrGst}",
                                color = AppColors.textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (entity.entityType == "Individual") Icons.Default.Person else Icons.Default.Business,
                            contentDescription = null,
                            tint = if (entity.id == currentEntity.id) AppColors.goldAccent else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = if (entity.id == currentEntity.id) {
                        { Icon(Icons.Default.Check, contentDescription = "Active", tint = AppColors.goldAccent, modifier = Modifier.size(16.dp)) }
                    } else null,
                    onClick = {
                        onSelectEntity(entity)
                        showEntityMenu = false
                    }
                )
            }
        }
    }
}
