package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.auth.AuthManager

// ============================================================
// PINK ADMIN THEME PALETTE
// ============================================================
private val AdminPinkCanvas = Color(0xFF190614)
private val AdminPinkSurface = Color(0xFF2B0A22)
private val AdminPinkSurfaceElevated = Color(0xFF380E2D)
private val AdminPinkPrimary = Color(0xFFFF2A85)
private val AdminPinkPrimaryDark = Color(0xFFC2185B)
private val AdminPinkAccent = Color(0xFFFF4081)
private val AdminPinkGlow = Color(0xFFFF80AB)
private val AdminPinkLight = Color(0xFFFCE4EC)
private val AdminPinkBorder = Color(0xFFFF4081).copy(alpha = 0.38f)

/**
 * Dedicated PINK ADMIN LOGIN PAGE triggered ONLY via the 14-tap sequence on the DoP brand logo.
 * Never accessible via normal UI, menus, or mobile account roles.
 * 
 * Features:
 * - Visually distinct Pink administrative theme
 * - Supports Email OR Phone Number + Password
 * - Server-authoritative authentication via AdminAuthRepository
 * - Enforces role = ADMIN and status = ACTIVE
 * - Lockout and rate-limiting enforcement
 * - Single-use 60s OTC web delegation to external admin portal (admin.dalalionpocket.com)
 */
@Composable
fun AdminAccessScreen(
    onCancel: () -> Unit,
    onAuthenticationSuccess: () -> Unit
) {
    var identifierInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var isAuthenticating by remember { mutableStateOf(false) }

    val adminError by AuthManager.adminAuthError.collectAsState()
    val isLockedOut = AuthManager.isLockedOut()
    val lockoutSeconds = AuthManager.getLockoutSecondsRemaining()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AdminPinkCanvas,
                        Color(0xFF22061C),
                        Color(0xFF140310)
                    )
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Pink Security Shield Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(AdminPinkSurfaceElevated)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AdminPinkPrimary.copy(alpha = 0.4f),
                                    AdminPinkSurface
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Admin Security Shield",
                        tint = AdminPinkPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = AdminPinkSurfaceElevated,
                    border = BorderStroke(1.dp, AdminPinkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AdminPinkPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RESTRICTED GATEWAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminPinkLight,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = AdminPinkSurfaceElevated,
                    border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AUDITED ACCESS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB9F6CA),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prominent Title
            Text(
                text = "ADMIN ACCESS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Lango Kuu la Usimamizi • Dalalion Pocket Enterprise",
                fontSize = 12.sp,
                color = AdminPinkGlow.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Security Disclaimer Notice Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AdminPinkSurfaceElevated,
                border = BorderStroke(1.dp, AdminPinkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = AdminPinkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ILANI YA USALAMA / SECURITY NOTICE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = AdminPinkGlow
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Lango hili limetengwa kwa ajili ya Wasimamizi Wakuu (DoP Admins) walioidhinishwa tu. Majaribio yote ya kuingia hurekodiwa kielektroniki kwa ukaguzi wa kisheria.",
                            fontSize = 11.sp,
                            color = AdminPinkLight.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Admin Credentials Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = AdminPinkSurface,
                border = BorderStroke(1.5.dp, AdminPinkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Identifier Field
                    Text(
                        text = "Kitambulisho cha Msimamizi (Email au Namba ya Simu)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AdminPinkLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = identifierInput,
                        onValueChange = { identifierInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_identifier_input"),
                        placeholder = { Text("admin@dalalionpocket.tz au +255...", color = AdminPinkLight.copy(alpha = 0.4f)) },
                        leadingIcon = {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AdminPinkPrimary)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AdminPinkPrimary,
                            unfocusedBorderColor = AdminPinkBorder,
                            cursorColor = AdminPinkPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nenosiri la Usimamizi (Admin Password)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AdminPinkLight
                        )
                        TextButton(
                            onClick = { showForgotPasswordDialog = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Usaidizi?", fontSize = 11.sp, color = AdminPinkGlow, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_password_input"),
                        placeholder = { Text("Weka nenosiri la usimamizi", color = AdminPinkLight.copy(alpha = 0.4f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Key, contentDescription = null, tint = AdminPinkPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Badili mwonekano wa nenosiri",
                                    tint = AdminPinkGlow
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (!isLockedOut && identifierInput.isNotBlank() && passwordInput.isNotBlank()) {
                                isAuthenticating = true
                                val success = AuthManager.authenticateAdmin(identifierInput, passwordInput)
                                isAuthenticating = false
                                if (success) onAuthenticationSuccess()
                            }
                        }),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AdminPinkPrimary,
                            unfocusedBorderColor = AdminPinkBorder,
                            cursorColor = AdminPinkPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Error / Lockout Display
                    if (adminError != null || isLockedOut) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFF1744).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFFF1744).copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isLockedOut) "Mfumo umefungwa kwa sekunde $lockoutSeconds." else adminError ?: "",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFF5252),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Sign In Button
                    Button(
                        onClick = {
                            isAuthenticating = true
                            val success = AuthManager.authenticateAdmin(identifierInput, passwordInput)
                            isAuthenticating = false
                            if (success) onAuthenticationSuccess()
                        },
                        enabled = !isLockedOut && !isAuthenticating && identifierInput.isNotBlank() && passwordInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AdminPinkPrimary,
                            disabledContainerColor = AdminPinkPrimary.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("admin_authenticate_btn")
                    ) {
                        if (isAuthenticating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Thibitisha na Ufungue (Admin Sign In)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Development Testing Shortcut
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AdminPinkSurfaceElevated.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, AdminPinkBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Utambulisho Rasmi wa Majaribio (Development Only):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminPinkGlow
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Email: admin@dalalionpocket.tz  •  Nenosiri: Admin@DoP2026",
                        fontSize = 10.sp,
                        color = AdminPinkLight.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = {
                            identifierInput = "admin@dalalionpocket.tz"
                            passwordInput = "Admin@DoP2026"
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Tumia Hizi Kujaribu (Fill Credentials)", fontSize = 11.sp, color = AdminPinkPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cancel / Return to Login
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AdminPinkLight),
                border = BorderStroke(1.dp, AdminPinkLight.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_cancel_btn")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ghairi na Urudi Kwenye Programu ya Simu", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Help Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text("Urejeshaji wa Nenosiri la Usimamizi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text(
                        text = "Kwa sababu za kiusalama na kisheria, akaunti za usimamizi mkuu haziwezi kuwekwa nenosiri jipya moja kwa moja kwenye simu ya mkononi.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Wasiliana na Idara ya Mifumo na Teknolojia ya DoP Tanzania kupitia: security-ops@dalalionpocket.tz au nambari ya simu ya dharura ya usimamizi.",
                        fontSize = 12.sp,
                        color = AdminPinkPrimaryDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showForgotPasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPinkPrimary)
                ) {
                    Text("Nimeelewa", color = Color.White)
                }
            }
        )
    }
}
