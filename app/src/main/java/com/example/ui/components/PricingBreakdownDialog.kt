package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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

data class PriceBreakdown(
    val serviceName: String,
    val modeName: String, // DIY, AI Assisted, CA Review, CA Handled
    val govtFee: Double,
    val caFee: Double,
    val platformFee: Double,
    val thirdPartyFee: Double = 0.0,
    val taxes: Double,
    val totalAmount: Double,
    val isZeroGovtFeeNotice: Boolean = false
)

@Composable
fun PricingBreakdownDialog(
    breakdown: PriceBreakdown,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = RoyalGold
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "Transparent Fee Breakdown",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "${breakdown.serviceName} (${breakdown.modeName})",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF131D31), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriceLineItem(label = "Official Government Fee", amount = breakdown.govtFee)
                    if (breakdown.isZeroGovtFeeNotice) {
                        Text(
                            text = "✓ Official Udyam registration is 100% free by Govt of India.",
                            fontSize = 11.sp,
                            color = Color(0xFF34D399)
                        )
                    }
                    PriceLineItem(label = "Chartered Accountant Professional Fee", amount = breakdown.caFee)
                    PriceLineItem(label = "Taxmitra AI Platform Fee", amount = breakdown.platformFee)
                    if (breakdown.thirdPartyFee > 0) {
                        PriceLineItem(label = "Third-Party Registry / Notary Fee", amount = breakdown.thirdPartyFee)
                    }
                    PriceLineItem(label = "Applicable GST (18%)", amount = breakdown.taxes)

                    HorizontalDivider(color = Color(0xFF24334C), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TOTAL ESTIMATE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = RoyalGold
                        )
                        Text(
                            text = "₹${String.format("%,.2f", breakdown.totalAmount)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = RoyalGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "No hidden fees. Government fees and professional fees are always itemized separately before payment.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 15.sp
                )

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
                        Text("Cancel", color = Color.White)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm & Continue", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceLineItem(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Text(
            text = if (amount == 0.0) "₹0 (Free)" else "₹${String.format("%,.2f", amount)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
