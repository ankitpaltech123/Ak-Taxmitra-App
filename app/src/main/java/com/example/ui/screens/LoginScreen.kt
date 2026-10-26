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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.WholeAppLogoView
import com.example.ui.theme.AppColors
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.MidnightNavy

@Composable
fun LoginScreen(
    onLoginSuccess: (phoneOrEmail: String, role: UserRole) -> Unit,
    onSkipToApp: () -> Unit,
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = AppColors.isDark
    val focusManager = LocalFocusManager.current

    var inputIdentifier by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.SALARIED) }
    var showOtpStep by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top action row: Theme Switcher & Skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(36.dp)
                    .background(AppColors.chipBackground, CircleShape)
                    .border(1.dp, AppColors.chipBorder, CircleShape)
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = AppColors.goldAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = "Skip to App →",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.goldAccent,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSkipToApp() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("skip_login_button")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // HERO CARD: WHOLE IMAGE WITH LOGO (Prominently displayed)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = if (isDark) 0.dp else 4.dp, shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF101A2D) else Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isDark) Color(0xFF223456) else Color(0xFFE2E8F0)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Whole Image with Logo Component
                WholeAppLogoView(
                    emblemSize = 130.dp,
                    showTagline = true,
                    isDarkCanvas = isDark,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Trust Badge under the logo
                Box(
                    modifier = Modifier
                        .background(
                            if (isDark) Color(0xFF1E2D47) else Color(0xFFF1F5F9),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF10B981) else Color(0xFF059669),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Official Govt. Tax & GSTN Ecosystem",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SIGN IN SECTION
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.cardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (showOtpStep) "Enter OTP Verification Code" else "Sign In or Register",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
                Text(
                    text = if (showOtpStep)
                        "We sent a 6-digit code to ${inputIdentifier.ifBlank { "+91 98765 43210" }}"
                    else
                        "Enter your mobile number or PAN to continue",
                    fontSize = 11.5.sp,
                    color = AppColors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!showOtpStep) {
                    // Mobile / PAN text field
                    OutlinedTextField(
                        value = inputIdentifier,
                        onValueChange = { inputIdentifier = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_input_field"),
                        placeholder = {
                            Text(
                                "98765 43210 or ABCDE1234F",
                                fontSize = 13.sp,
                                color = AppColors.textMuted
                            )
                        },
                        leadingIcon = {
                            Row(
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🇮🇳 +91", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .height(16.dp)
                                        .width(1.dp)
                                        .background(AppColors.cardBorder)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                showOtpStep = true
                            }
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.goldAccent,
                            unfocusedBorderColor = AppColors.cardBorder,
                            focusedContainerColor = AppColors.inputBackground,
                            unfocusedContainerColor = AppColors.inputBackground,
                            focusedTextColor = AppColors.textPrimary,
                            unfocusedTextColor = AppColors.textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Continue with OTP Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            showOtpStep = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("get_otp_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.goldAccent)
                    ) {
                        Text(
                            text = "Get OTP Verification Code",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    // OTP Input step
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { if (it.length <= 6) otpCode = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input_field"),
                        placeholder = {
                            Text("1 2 3 4 5 6 (Any code for demo)", fontSize = 13.sp, color = AppColors.textMuted)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onLoginSuccess(inputIdentifier.ifBlank { "+91 98765 43210" }, selectedRole)
                            }
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.goldAccent,
                            unfocusedBorderColor = AppColors.cardBorder,
                            focusedContainerColor = AppColors.inputBackground,
                            unfocusedContainerColor = AppColors.inputBackground,
                            focusedTextColor = AppColors.textPrimary,
                            unfocusedTextColor = AppColors.textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "← Change number",
                            fontSize = 11.sp,
                            color = AppColors.goldAccent,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { showOtpStep = false }
                        )
                        Text(
                            text = "Resend OTP",
                            fontSize = 11.sp,
                            color = AppColors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onLoginSuccess(inputIdentifier.ifBlank { "+91 98765 43210" }, selectedRole)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("verify_otp_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.goldAccent)
                    ) {
                        Text(
                            text = "Verify & Access AK Taxmitra",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(AppColors.cardBorder))
                    Text(
                        text = "  OR FAST DEMO LOGIN  ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textMuted
                    )
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(AppColors.cardBorder))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // One-tap quick demo roles
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DemoRoleTile(
                        icon = Icons.Default.Person,
                        roleName = "Salaried Taxpayer (Ankit Sharma)",
                        roleBadge = "ITR-1 / 80C & 80D",
                        onClick = {
                            onLoginSuccess("+91 98765 43210", UserRole.SALARIED)
                        }
                    )

                    DemoRoleTile(
                        icon = Icons.Default.Business,
                        roleName = "Business Owner (VastraMitra Apparels)",
                        roleBadge = "GST / MSME / Invoices",
                        onClick = {
                            onLoginSuccess("+91 98111 22334", UserRole.BUSINESS_OWNER)
                        }
                    )

                    DemoRoleTile(
                        icon = Icons.Default.VerifiedUser,
                        roleName = "Chartered Accountant (CA Mode)",
                        roleBadge = "Client Audits / AIS / Notices",
                        onClick = {
                            onLoginSuccess("+91 98222 33445", UserRole.CA)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Trust & Security Guarantee
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = if (isDark) Color(0xFF10B981) else Color(0xFF059669),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "256-Bit Bank-Grade Encryption • Income Tax Act 1961 & GST Act 2017",
                fontSize = 9.5.sp,
                color = AppColors.textMuted,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DemoRoleTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    roleName: String,
    roleBadge: String,
    onClick: () -> Unit
) {
    val isDark = AppColors.isDark
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, AppColors.cardBorder, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.inputBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(AppColors.goldAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.goldAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roleName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
                Text(
                    text = roleBadge,
                    fontSize = 10.sp,
                    color = AppColors.textSecondary
                )
            }
            Text(
                text = "Log In →",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.goldAccent
            )
        }
    }
}
