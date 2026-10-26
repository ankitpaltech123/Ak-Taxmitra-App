package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RoyalGold

data class ConceptExplanation(
    val title: String,
    val whatIsIt: String,
    val whyItMatters: String,
    val doINeedIt: String,
    val costEstimate: String,
    val whoHandlesIt: String,
    val whatHappensNext: String,
    val canIDoItMyself: String,
    val whenToAskCa: String
)

@Composable
fun UniversalExplanationDialog(
    explanation: ConceptExplanation,
    onDismiss: () -> Unit,
    onProceedAction: (() -> Unit)? = null,
    actionButtonText: String = "Proceed with Action"
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = explanation.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExplanationSection(title = "WHAT IS IT?", body = explanation.whatIsIt)
                ExplanationSection(title = "WHY DOES IT MATTER?", body = explanation.whyItMatters)
                ExplanationSection(title = "DO I NEED IT?", body = explanation.doINeedIt)
                ExplanationSection(title = "WHAT WILL IT COST?", body = explanation.costEstimate, isHighlighted = true)
                ExplanationSection(title = "WHO HANDLES IT?", body = explanation.whoHandlesIt)
                ExplanationSection(title = "CAN I DO IT MYSELF?", body = explanation.canIDoItMyself)
                ExplanationSection(title = "WHEN SHOULD I ASK A CA?", body = explanation.whenToAskCa)
                ExplanationSection(title = "WHAT HAPPENS NEXT?", body = explanation.whatHappensNext)

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Understood", color = Color.White)
                    }

                    if (onProceedAction != null) {
                        Button(
                            onClick = {
                                onDismiss()
                                onProceedAction()
                            },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(actionButtonText, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplanationSection(
    title: String,
    body: String,
    isHighlighted: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                if (isHighlighted) Color(0xFF1F293D) else Color(0xFF131D31),
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp,
            color = if (isHighlighted) RoyalGold else Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            fontSize = 13.sp,
            color = Color.White,
            lineHeight = 18.sp
        )
    }
}
