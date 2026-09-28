package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.core.auth.AuthManager
import com.example.core.auth.AuthResult
import com.example.core.auth.admin.AdminTapDetector
import com.example.core.auth.admin.adminSecretTap
import com.example.core.localization.AppLanguage
import com.example.core.localization.strings
import com.example.data.model.UserRole
import com.example.ui.components.DopBadge
import com.example.ui.components.DopBentoCard
import com.example.ui.theme.*

// ============================================================
// 1. SPLASH SCREEN
// ============================================================
@Composable
fun SplashScreen(onContinue: () -> Unit) {
    LaunchedEffect(Unit) {
        // Automatically progress after brief splash
        kotlinx.coroutines.delay(1800)
        onContinue()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // High-resolution architectural photography background
        AsyncImage(
            model = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.bg_auth_realestate),
            error = painterResource(id = R.drawable.bg_auth_realestate)
        )

        // Rich navy gradient overlay scrim for contrast and high-end feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DopNavyPrimary.copy(alpha = 0.82f),
                            DopNavyElevated.copy(alpha = 0.90f),
                            DopNavyDark.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DopOchre),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HomeWork,
                    contentDescription = "DoP Logo",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Dalalion Pocket",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tanzania PropTech Ecosystem",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = DopOchreLight,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                color = DopOchre,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Inapakia mazingira salama ya upangaji...",
                fontSize = 11.sp,
                color = DopNeutralPearl.copy(alpha = 0.85f)
            )
        }

        // Tap to skip splash immediately
        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .testTag("splash_continue_btn")
        ) {
            Text(
                text = "Bofya hapa kuendelea →",
                fontSize = 12.sp,
                color = DopNeutralPearl.copy(alpha = 0.7f)
            )
        }
    }
}

// ============================================================
// 2. ONBOARDING SCREEN
// ============================================================
@Composable
fun OnboardingScreen(
    lang: AppLanguage = AppLanguage.SWAHILI,
    onFinished: () -> Unit,
    onLoginClick: () -> Unit
) {
    val s = strings(lang)
    var activePage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        Triple(
            s.onboardingSlide1Title,
            s.onboardingSlide1Desc,
            Icons.Default.Verified
        ),
        Triple(
            s.onboardingSlide2Title,
            s.onboardingSlide2Desc,
            Icons.Default.HomeWork
        ),
        Triple(
            s.onboardingSlide3Title,
            s.onboardingSlide3Desc,
            Icons.Default.DirectionsWalk
        )
    )

    val backgroundPhotos = listOf(
        "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=1200&q=80",
        "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&q=80",
        "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=1200&q=80"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Dynamic architectural background photo per slide
        AsyncImage(
            model = backgroundPhotos.getOrElse(activePage) { backgroundPhotos[0] },
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.bg_auth_realestate),
            error = painterResource(id = R.drawable.bg_auth_realestate)
        )

        // Navy twilight scrim overlay for contrast & readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DopNavyPrimary.copy(alpha = 0.70f),
                            DopNavyElevated.copy(alpha = 0.85f),
                            DopNavyDark.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DopOchre),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.HomeWork, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(s.appTitle, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DopNavyElevated.copy(alpha = 0.8f)
                ) {
                    TextButton(
                        onClick = onFinished,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(s.onboardingSkip, color = DopNeutralPearl, fontSize = 12.sp)
                    }
                }
            }

            // Center Carousel Feature Card
            val currentFeature = pages[activePage]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DopNavyPrimary.copy(alpha = 0.88f)),
                border = BorderStroke(1.dp, DopOchre.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(DopNavyElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(currentFeature.third, contentDescription = null, tint = DopOchreLight, modifier = Modifier.size(30.dp))
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = currentFeature.first,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentFeature.second,
                        fontSize = 13.sp,
                        color = DopNeutralPearl.copy(alpha = 0.90f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Indicator dots
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        pages.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (index == activePage) 26.dp else 8.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (index == activePage) DopOchre else DopNeutralPearl.copy(alpha = 0.4f))
                            )
                        }
                    }
                }
            }

            // Bottom Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                if (activePage < pages.size - 1) {
                    Button(
                        onClick = { activePage++ },
                        colors = ButtonDefaults.buttonColors(containerColor = DopNavyElevated),
                        border = BorderStroke(1.dp, DopOchre.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_next_btn")
                    ) {
                        Text(s.onboardingNext, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = DopOchreLight, modifier = Modifier.size(18.dp))
                    }
                } else {
                    Button(
                        onClick = onFinished,
                        colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_get_started_btn")
                    ) {
                        Text(s.onboardingGetStarted, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onLoginClick,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DopNeutralPearl.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DopNavyPrimary.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("onboarding_login_btn")
                ) {
                    Text(s.onboardingAlreadyHaveAccount, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

// ============================================================
// 3. LOGIN SCREEN
// ============================================================
@Composable
fun LoginScreen(
    lang: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToAdminLogin: () -> Unit = { AuthManager.navigateToAdminLogin() }
) {
    val s = strings(lang)
    var phoneInput by remember { mutableStateOf("0754210334") }
    var passwordInput by remember { mutableStateOf("Baraka@2026") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPhoneInput by remember { mutableStateOf("") }
    var forgotSuccessMsg by remember { mutableStateOf<String?>(null) }

    val adminTapDetector = remember {
        AdminTapDetector(
            targetTaps = 14,
            resetTimeoutMs = 2500L,
            onAdminTrigger = onNavigateToAdminLogin
        )
    }

    val authResult by AuthManager.authResult.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // High-resolution architectural photography background
        AsyncImage(
            model = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.bg_auth_realestate),
            error = painterResource(id = R.drawable.bg_auth_realestate)
        )

        // Navy blue twilight overlay scrim for contrast and legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DopNavyPrimary.copy(alpha = 0.76f),
                            DopNavyPrimary.copy(alpha = 0.88f),
                            DopNavyPrimary.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Language Switcher Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("login_hidden_admin_trigger")
                        .adminSecretTap(adminTapDetector)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DopOchre),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.HomeWork, contentDescription = "DoP Brand Logo", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(s.appTitle, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DopNavyElevated.copy(alpha = 0.85f),
                    modifier = Modifier.clickable {
                        onLanguageChange(if (lang == AppLanguage.SWAHILI) AppLanguage.ENGLISH else AppLanguage.SWAHILI)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp), tint = DopOchreLight)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (lang == AppLanguage.SWAHILI) "🇹🇿 Kiswahili" else "🇬🇧 English",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Prominent DoP Brand Mark (Designated 14-tap hidden interaction area)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DopOchre)
                    .adminSecretTap(adminTapDetector)
                    .testTag("login_brand_logo_main"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HomeWork,
                    contentDescription = "DoP Brand Mark",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title & Greeting
            Text(
                text = s.loginWelcomeTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = s.loginWelcomeSubtitle,
                fontSize = 13.sp,
                color = DopNeutralPearl.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Login Form Card
            DopBentoCard(modifier = Modifier.fillMaxWidth()) {
                // Phone input
                Text(s.loginPhoneLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopNavyPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_phone_input"),
                    placeholder = { Text(s.loginPhonePlaceholder) },
                    leadingIcon = {
                        Row(
                            modifier = Modifier.padding(start = 12.dp, end = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🇹🇿", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+255", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopNavyPrimary)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(s.loginPasswordLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopNavyPrimary)
                    TextButton(
                        onClick = { showForgotPasswordDialog = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(s.loginForgotPassword, fontSize = 11.sp, color = DopOchre, fontWeight = FontWeight.SemiBold)
                    }
                }
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    placeholder = { Text(s.loginPasswordPlaceholder) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DopTextSecondary) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = DopTextSecondary
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        AuthManager.login(phoneInput, passwordInput)
                    }),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Error display
                if (authResult is AuthResult.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DopError.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, DopError.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DopError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = (authResult as AuthResult.Error).message,
                                fontSize = 11.sp,
                                color = DopError
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Login Button
                val isLoading = authResult is AuthResult.Loading
                Button(
                    onClick = { AuthManager.login(phoneInput, passwordInput) },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_btn")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(s.loginSubmitButton, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Official Starter Accounts Showcase Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DopNavyElevated.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, DopOchre.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = s.loginStarterAccountsTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DopOchreLight
                        )
                        DopBadge(text = s.loginStarterRoleBadge, color = DopOchre, backgroundColor = DopOchreContainer)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Customer Account
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                phoneInput = "0754210334"
                                passwordInput = "Baraka@2026"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(DopOchreContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = DopOchre, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Baraka Elias Mushi (${s.roleCustomerTitle})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${s.loginPhoneLabel}: 0754210334  •  ${s.loginPasswordLabel}: Baraka@2026",
                                    fontSize = 10.sp,
                                    color = DopNeutralPearl.copy(alpha = 0.8f)
                                )
                            }
                            Text(s.loginSelectAccount, fontSize = 10.sp, color = DopOchreLight, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Owner Account
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                phoneInput = "0784567890"
                                passwordInput = "Grace@2026"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(DopNavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HomeWork, contentDescription = null, tint = DopOchreLight, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Eng. Grace Ndesamburo (${s.roleOwnerTitle})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${s.loginPhoneLabel}: 0784567890  •  ${s.loginPasswordLabel}: Grace@2026",
                                    fontSize = 10.sp,
                                    color = DopNeutralPearl.copy(alpha = 0.8f)
                                )
                            }
                            Text(s.loginSelectAccount, fontSize = 10.sp, color = DopOchreLight, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Guide Account
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                phoneInput = "0655789012"
                                passwordInput = "Kimbau@2026"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(DopTrustGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mohamed Said Kimbau (${s.roleGuideTitle})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${s.loginPhoneLabel}: 0655789012  •  ${s.loginPasswordLabel}: Kimbau@2026",
                                    fontSize = 10.sp,
                                    color = DopNeutralPearl.copy(alpha = 0.8f)
                                )
                            }
                            Text(s.loginSelectAccount, fontSize = 10.sp, color = DopOchreLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Up Link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(s.loginNoAccountPrompt, fontSize = 12.sp, color = DopNeutralPearl.copy(alpha = 0.85f))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = s.loginSignUpAction,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopOchreLight,
                    modifier = Modifier
                        .clickable { onNavigateToSignUp() }
                        .testTag("navigate_to_signup_btn")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Terms and Privacy Note
            Text(
                text = s.loginTermsNote,
                fontSize = 10.sp,
                color = DopNeutralPearl.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                forgotSuccessMsg = null
            },
            title = { Text(s.forgotPasswordTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text(
                        text = s.forgotPasswordDesc,
                        fontSize = 12.sp,
                        color = DopTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotPhoneInput,
                        onValueChange = { forgotPhoneInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(s.loginPhonePlaceholder) },
                        label = { Text(s.loginPhoneLabel) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    if (forgotSuccessMsg != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = forgotSuccessMsg!!,
                            color = DopTrustGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotPhoneInput.isNotBlank()) {
                            forgotSuccessMsg = "${s.forgotPasswordSuccessMsg} ($forgotPhoneInput)"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary)
                ) {
                    Text(s.forgotPasswordSubmitButton)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showForgotPasswordDialog = false
                    forgotSuccessMsg = null
                }) {
                    Text(s.dialogCloseButton)
                }
            }
        )
    }
}

// ============================================================
// ============================================================
// 4. SIGN UP SCREEN (BASE CUSTOMER ACCOUNT BY DEFAULT)
// ============================================================
@Composable
fun SignUpScreen(
    lang: AppLanguage = AppLanguage.SWAHILI,
    onNavigateToLogin: () -> Unit
) {
    val s = strings(lang)
    var fullName by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }

    val authResult by AuthManager.authResult.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // High-resolution architectural photography background
        AsyncImage(
            model = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.bg_auth_realestate),
            error = painterResource(id = R.drawable.bg_auth_realestate)
        )

        // Navy blue twilight overlay scrim for contrast and legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DopNavyPrimary.copy(alpha = 0.80f),
                            DopNavyPrimary.copy(alpha = 0.90f),
                            DopNavyPrimary.copy(alpha = 0.97f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToLogin) {
                    Icon(Icons.Default.ArrowBack, contentDescription = s.dialogCloseButton, tint = Color.White)
                }
                Text(
                    text = s.signUpTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(48.dp)) // balance icon
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = s.signUpTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = s.signUpSubtitle,
                fontSize = 12.sp,
                color = DopNeutralPearl.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            DopBentoCard(modifier = Modifier.fillMaxWidth()) {
                // Capability explanation banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DopCustomerBlueContainer,
                    border = BorderStroke(1.dp, DopCustomerBlueBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DopCustomerBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = s.signUpBaseAccountBannerTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DopCustomerBlueDark
                            )
                            Text(
                                text = s.signUpBaseAccountBannerDesc,
                                fontSize = 10.sp,
                                color = DopTextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Full name
                Text(s.signUpFullNameLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text(s.signUpFullNamePlaceholder) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone number
                Text(s.signUpPhoneLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    placeholder = { Text(s.signUpPhonePlaceholder) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email (optional)
                Text(s.signUpEmailLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = { Text(s.signUpEmailPlaceholder) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                Text(s.signUpPasswordLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    placeholder = { Text(s.signUpPasswordPlaceholder) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_password_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password
                Text(s.signUpConfirmPasswordLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DopNavyPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = confirmPasswordInput,
                    onValueChange = { confirmPasswordInput = it },
                    placeholder = { Text(s.signUpConfirmPasswordPlaceholder) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_confirm_password_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Terms Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                        modifier = Modifier.testTag("terms_checkbox")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = s.signUpTermsAgreement,
                        fontSize = 11.sp,
                        color = DopTextSecondary,
                        lineHeight = 16.sp
                    )
                }

                // Error display
                if (authResult is AuthResult.Error) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = (authResult as AuthResult.Error).message,
                        fontSize = 11.sp,
                        color = DopError
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Registration Button (Always creates CUSTOMER account)
                val isLoading = authResult is AuthResult.Loading
                Button(
                    onClick = {
                        AuthManager.registerCustomer(
                            fullName = fullName,
                            phoneInput = phoneInput,
                            emailInput = emailInput,
                            passwordInput = passwordInput,
                            confirmPasswordInput = confirmPasswordInput,
                            acceptedTerms = acceptedTerms
                        )
                    },
                    enabled = !isLoading && acceptedTerms,
                    colors = ButtonDefaults.buttonColors(containerColor = DopCustomerBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("signup_submit_btn")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(s.signUpSubmitButton, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

        Spacer(modifier = Modifier.height(24.dp))

        // Existing account link
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(s.signUpAlreadyHaveAccount, fontSize = 12.sp, color = DopNeutralPearl.copy(alpha = 0.85f))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = s.signUpSignInAction,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DopOchreLight,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
}

@Composable
private fun RoleSelectionCard(
    role: UserRole,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) DopNavyPrimary else DopSurfaceCard,
        border = BorderStroke(
            1.5.dp,
            if (isSelected) DopOchre else DopBorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("role_select_${role.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) DopNavyElevated else DopOchreContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) DopOchreLight else DopOchre,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isSelected) Color.White else DopNavyPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = if (isSelected) DopNeutralPearl.copy(alpha = 0.8f) else DopTextSecondary,
                    lineHeight = 15.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = { onClick() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = DopOchre,
                    unselectedColor = DopTextSecondary
                )
            )
        }
    }
}

// ============================================================
// 5. ADMIN BLOCKED SCREEN (MANDATE 1 & 7)
// ============================================================
@Composable
fun AdminBlockedScreen(onReturnToLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNavyPrimary)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        DopBentoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DopNavyElevated
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(DopOchre.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = DopOchre, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "DoP Administration Portal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DopOchreContainer
            ) {
                Text(
                    text = "SEPARATE WEB PLATFORM REQUIRED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DopOchre,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Admin accounts are managed through the DoP Administration Portal.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mfumo huu wa simu (Mobile Application) umejengwa kwa ajili ya Wateja (Customers), Wenye Nyumba (Owners), na Waongozaji (Guides) pekee. Wasimamizi wakuu wa mfumo wanatumia tovuti rasmi ya usimamizi iliyo salama.",
                fontSize = 12.sp,
                color = DopNeutralPearl.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onReturnToLogin,
                colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_return_login_btn")
            ) {
                Text("Rudi Kwenye Kuingia (Return to Login)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
