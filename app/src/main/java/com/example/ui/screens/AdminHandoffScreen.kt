package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.auth.AuthManager
import com.example.ui.components.DopBadge
import com.example.ui.components.DopBentoCard
import com.example.ui.theme.*

/**
 * Admin Web Handoff Delegation Screen.
 * Demonstrates the architectural handoff to the separate Admin Web Application.
 * Admin operations and dashboard do NOT render on the mobile client.
 */
@Composable
fun AdminHandoffScreen(
    onExitHandoff: () -> Unit
) {
    val handoffSession by AuthManager.adminHandoffSession.collectAsState()
    val context = LocalContext.current

    val session = handoffSession ?: return

    var secondsLeft by remember {
        mutableLongStateOf(
            ((session.expiresAt - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
        )
    }

    LaunchedEffect(session.expiresAt) {
        while (secondsLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            secondsLeft = ((session.expiresAt - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNavyPrimary)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Handoff Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(DopTrustGreen.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = null,
                    tint = DopTrustGreenLight,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            DopBadge("DELEGATED TO EXTERNAL WEB PORTAL", Icons.Default.Language, DopTrustGreenLight, DopNavyElevated)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Kikao Kimekabidhiwa Tovuti Rasmi",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "DoP Enterprise Administration Portal (admin.dalalionpocket.com)",
                fontSize = 12.sp,
                color = DopNeutralPearl.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Architectural Explanation Card
            DopBentoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DopNavyElevated
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DopTrustGreenLight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Uthibitishaji Salama Umefaulu",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mtumiaji: ${session.adminEmail}\nMsimamizi: ${session.adminName}",
                    fontSize = 11.sp,
                    color = DopOchreLight,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Divider(color = DopBorderSubtle.copy(alpha = 0.2f))

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Msimbo wa Mara Moja (One-Time Single-Use Code):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopNeutralPearl.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, DopOchre.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = session.oneTimeCode,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = DopOchreLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (secondsLeft > 0) "Muda uliosalia: sekunde $secondsLeft (Inaisha baada ya sekunde 60)" else "Msimbo umemalizika muda wake",
                            fontSize = 10.sp,
                            color = if (secondsLeft > 10) DopTrustGreenLight else DopError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Kwa matakwa ya usalama wa kibenki na sera za Play Store, dashibodi ya usimamizi mkuu haifungukiwi ndani ya programu ya simu (Mobile App). Badala yake, mfumo unafanya handoff salama kwenda kwenye seva ya wavuti.",
                    fontSize = 11.sp,
                    color = DopNeutralPearl.copy(alpha = 0.75f),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action: Open Browser
            Button(
                onClick = {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(session.handoffUrl))
                    try {
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Kivinjari cha tovuti hakikupatikana.", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DopOchre),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_launch_browser_btn")
            ) {
                Icon(Icons.Default.Launch, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fungua Tovuti ya Usimamizi (Launch Web Portal)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DopNavyPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Copy Link
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("DoP Admin Handoff", session.handoffUrl)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Kiungo cha tovuti kimenakiliwa!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, DopOchre.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("admin_copy_link_btn")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = DopOchreLight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nakili Kiungo Salama (Copy URL)", fontSize = 12.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Terminate Handoff & Return
            TextButton(
                onClick = onExitHandoff,
                modifier = Modifier.testTag("admin_exit_handoff_btn")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = DopNeutralPearl.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Maliza Kikao na Rudi Kwenye Simu (Return to App)", fontSize = 12.sp, color = DopNeutralPearl.copy(alpha = 0.85f))
            }
        }
    }
}
