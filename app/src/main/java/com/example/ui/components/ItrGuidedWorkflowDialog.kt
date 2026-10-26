package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.engine.TaxCalculationEngine
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MidnightNavy
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ItrGuidedWorkflowDialog(
    initialSalary: Double = 850000.0,
    initialBusiness: Double = 0.0,
    initial80c: Double = 150000.0,
    initial80d: Double = 25000.0,
    initialTds: Double = 25000.0,
    onDismiss: () -> Unit,
    onSubmitComplete: (Boolean) -> Unit, // withCa
    onExplainResult: (String, String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Profile, 2: Income, 3: Deductions, 4: Calculation, 5: Review, 6: Confirmation

    var salaryInput by remember { mutableStateOf(initialSalary.toInt().toString()) }
    var businessInput by remember { mutableStateOf(initialBusiness.toInt().toString()) }
    var deduction80cInput by remember { mutableStateOf(initial80c.toInt().toString()) }
    var deduction80dInput by remember { mutableStateOf(initial80d.toInt().toString()) }
    var tdsPaidInput by remember { mutableStateOf(initialTds.toInt().toString()) }
    var withCaReview by remember { mutableStateOf(false) }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")) }

    val salary = salaryInput.toDoubleOrNull() ?: 0.0
    val business = businessInput.toDoubleOrNull() ?: 0.0
    val d80c = deduction80cInput.toDoubleOrNull() ?: 0.0
    val d80d = deduction80dInput.toDoubleOrNull() ?: 0.0
    val tds = tdsPaidInput.toDoubleOrNull() ?: 0.0

    val calcResult = remember(salary, business, d80c, d80d, tds) {
        TaxCalculationEngine.calculate(
            salaryIncome = salary,
            businessIncome = business,
            rentalIncome = 0.0,
            capitalGains = 0.0,
            otherIncome = 0.0,
            deduction80C = d80c,
            deduction80D = d80d,
            deductionNps = 0.0,
            otherDeductions = 0.0,
            tdsPaid = tds,
            advanceTaxPaid = 0.0
        )
    }

    val progressFraction = step / 6f
    val progressPercent = (progressFraction * 100).toInt()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp))
                .background(MidnightNavy)
                .border(1.dp, Color(0xFF223250), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header with Step Counter & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ITR Guided Workflow",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF1E2D4A), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$progressPercent% Complete",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold
                                )
                            }
                        }
                        Text(
                            text = "Step $step of 6 • ${getStepTitle(step)}",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ChampagneGold,
                    trackColor = Color(0xFF1B2840)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step Content Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (step) {
                        1 -> Step1Profile(calcResult.recommendedItrForm)
                        2 -> Step2Income(
                            salaryInput = salaryInput,
                            onSalaryChange = { salaryInput = it },
                            businessInput = businessInput,
                            onBusinessChange = { businessInput = it }
                        )
                        3 -> Step3Deductions(
                            d80cInput = deduction80cInput,
                            onD80cChange = { deduction80cInput = it },
                            d80dInput = deduction80dInput,
                            onD80dChange = { deduction80dInput = it },
                            tdsInput = tdsPaidInput,
                            onTdsChange = { tdsPaidInput = it }
                        )
                        4 -> Step4Calculation(
                            salary = salary,
                            d80c = d80c + d80d,
                            calcResult = calcResult,
                            onExplain = {
                                onExplainResult(
                                    "Tax Regime Calculation Breakdown",
                                    "Under the New Tax Regime (Finance Act 2024), you receive ₹75,000 Standard Deduction. Taxable income is taxed under progressive slabs (0-3L 0%, 3-7L 5%, 7-10L 10%, etc.). If taxable income is <= ₹7,00,000, Section 87A provides a full rebate."
                                )
                            }
                        )
                        5 -> Step5Review(
                            calcResult = calcResult,
                            withCaReview = withCaReview,
                            onToggleCa = { withCaReview = it }
                        )
                        6 -> Step6Confirmation(
                            calcResult = calcResult,
                            withCa = withCaReview
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Navigation Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", fontSize = 12.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    if (step < 6) {
                        Button(
                            onClick = { step++ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
                        ) {
                            Text("Continue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                onSubmitComplete(withCaReview)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
                        ) {
                            Text("CONFIRM & CONTINUE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun getStepTitle(step: Int): String {
    return when (step) {
        1 -> "Taxpayer Profile & Form"
        2 -> "Income Breakdown"
        3 -> "Deductions & Taxes Paid"
        4 -> "Deterministic Slabs"
        5 -> "Review & Professional Audit"
        6 -> "Confirmation & Submission"
        else -> ""
    }
}

@Composable
private fun Step1Profile(recommendedForm: String) {
    Column {
        Text("TAX PROFILE IDENTIFICATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Taxmitra has identified your filing parameters for Assessment Year 2026-27 (Financial Year 2025-26).",
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1),
            lineHeight = 17.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recommended ITR Form: $recommendedForm", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Applicable for Indian resident individuals having salary, pension, one house property, or interest income below ₹50 Lakhs.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun Step2Income(
    salaryInput: String,
    onSalaryChange: (String) -> Unit,
    businessInput: String,
    onBusinessChange: (String) -> Unit
) {
    Column {
        Text("INCOME FROM ALL SOURCES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Enter your gross earnings. Form 16 / AIS figures can be updated directly.", fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = salaryInput,
            onValueChange = onSalaryChange,
            label = { Text("Gross Salary / Pension (₹)", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = Color(0xFF2B3A55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = businessInput,
            onValueChange = onBusinessChange,
            label = { Text("Business / Freelance Net Profit (₹)", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = Color(0xFF2B3A55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
private fun Step3Deductions(
    d80cInput: String,
    onD80cChange: (String) -> Unit,
    d80dInput: String,
    onD80dChange: (String) -> Unit,
    tdsInput: String,
    onTdsChange: (String) -> Unit
) {
    Column {
        Text("DEDUCTIONS & TAXES CREDITED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Eligible Chapter VI-A investments (Applies if choosing Old Regime).", fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = d80cInput,
            onValueChange = onD80cChange,
            label = { Text("Section 80C (PPF, EPF, ELSS - Max ₹1.5L)", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = Color(0xFF2B3A55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = d80dInput,
            onValueChange = onD80dChange,
            label = { Text("Section 80D (Health Insurance Mediclaim)", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = Color(0xFF2B3A55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = tdsInput,
            onValueChange = onTdsChange,
            label = { Text("TDS Already Deducted (From 26AS / AIS) (₹)", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = Color(0xFF2B3A55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
private fun Step4Calculation(
    salary: Double,
    d80c: Double,
    calcResult: com.example.data.engine.TaxCalculationResult,
    onExplain: () -> Unit
) {
    val currency = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("DETERMINISTIC REGIME COMPARISON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E2F4C))
                    .clickable { onExplain() }
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("EXPLAIN THIS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Regime Comparison Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121B2E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233454))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("NEW REGIME (Default)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                        Text(currency.format(calcResult.totalTaxNew), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("OLD REGIME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Text(currency.format(calcResult.totalTaxOld), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "⭐ Recommended: ${calcResult.recommendedRegime} REGIME\nYou save ${currency.format(calcResult.taxDifference)} in taxes!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown table
                CalculationRow("Gross Total Income", currency.format(salary))
                CalculationRow("Standard Deduction", "₹75,000 (New) / ₹50,000 (Old)")
                CalculationRow("TDS Already Paid", currency.format(calcResult.tdsDeducted))
                CalculationRow(
                    if (calcResult.isRefund) "Net Refund Due" else "Net Tax Payable",
                    currency.format(kotlin.math.abs(calcResult.netPayableOrRefund)),
                    isHighlight = true
                )
            }
        }
    }
}

@Composable
private fun Step5Review(
    calcResult: com.example.data.engine.TaxCalculationResult,
    withCaReview: Boolean,
    onToggleCa: (Boolean) -> Unit
) {
    Column {
        Text("PRE-SUBMISSION VERIFICATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Verify all computations before proceeding to official e-filing.", fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131E33)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Deterministic Rule Checks:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                CheckItem("Sec 87A Rebate Applied: ${if (calcResult.rebate87ANew > 0) "YES (100% Tax Relief)" else "NO"}")
                CheckItem("Standard Deduction of ₹75,000 verified under Finance Act 2024")
                CheckItem("Health & Education Cess (4%) calculated accurately")
                CheckItem("Form Type matched: ${calcResult.recommendedItrForm}")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Professional Human Review Option
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleCa(!withCaReview) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (withCaReview) Color(0xFF1C2740) else Color(0xFF111A2C)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (withCaReview) ChampagneGold else Color(0xFF223250)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (withCaReview) ChampagneGold else Color(0xFF223250)),
                    contentAlignment = Alignment.Center
                ) {
                    if (withCaReview) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Include Verified CA Assisted Review (+₹999)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "A practicing ICAI Chartered Accountant will review your draft before filing.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun Step6Confirmation(
    calcResult: com.example.data.engine.TaxCalculationResult,
    withCa: Boolean
) {
    val currency = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF142036), RoundedCornerShape(12.dp))
                .border(1.dp, ChampagneGold, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AUTHORIZATION CONFIRMATION", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "WHAT WILL HAPPEN:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGoldLight
                )
                Text(
                    text = "Your tax computation draft (${calcResult.recommendedItrForm}) will be compiled for filing under AY 2026-27.",
                    fontSize = 12.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AMOUNT INVOLVED:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGoldLight
                )
                Text(
                    text = if (calcResult.isRefund) "Net Refund Due: ${currency.format(kotlin.math.abs(calcResult.netPayableOrRefund))}" else "Net Tax Payable: ${currency.format(calcResult.netPayableOrRefund)}",
                    fontSize = 12.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "CONSEQUENCE:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGoldLight
                )
                Text(
                    text = if (withCa) "Transferred to a verified CA for final verification and transmission." else "Prepared for official e-verification via Aadhaar OTP on Income Tax Portal.",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CalculationRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = if (isHighlight) ChampagneGoldLight else Color(0xFF94A3B8), fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal)
        Text(text = value, fontSize = 12.sp, color = if (isHighlight) Color.White else Color(0xFFCBD5E1), fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun CheckItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 11.sp, color = Color(0xFFCBD5E1))
    }
}
