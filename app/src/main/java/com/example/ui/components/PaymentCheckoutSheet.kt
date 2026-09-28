package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.core.localization.strings
import com.example.data.model.PaymentMethod
import com.example.data.model.RevenueSource
import com.example.data.model.WalletTransaction
import com.example.data.repository.PropertyRepository
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentCheckoutSheet(
    title: String,
    subtitle: String,
    amountTzs: Long,
    source: RevenueSource,
    payerInitialPhone: String = "0754123456",
    lang: AppLanguage = AppLanguage.SWAHILI,
    onDismiss: () -> Unit,
    onPaymentSuccess: (WalletTransaction) -> Unit
) {
    val s = strings(lang)
    var selectedMethod by remember { mutableStateOf(PaymentMethod.M_PESA) }
    var phoneNumber by remember { mutableStateOf(payerInitialPhone) }
    var isProcessing by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf("IDLE") } // IDLE, PROMPT_SENT, CONFIRMED
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = DopOchre,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DopNavyPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = DopTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Amount Summary Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DopSurfaceCard,
                    border = BorderStroke(1.dp, DopBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(s.paymentAmountLabel, fontSize = 11.sp, color = DopTextSecondary)
                            Text(
                                if (lang == AppLanguage.SWAHILI) source.labelSw else source.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 10.sp,
                                color = DopOchre,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = DoPStrings.tzs(amountTzs),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DopTrustGreen
                        )
                    }
                }

                if (currentStep == "IDLE") {
                    Text(
                        text = s.paymentSelectMethod,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DopNavyPrimary
                    )

                    // Payment Provider selector list
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PaymentMethod.values().forEach { method ->
                            val isSelected = method == selectedMethod
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) DopOchreContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isSelected) BorderStroke(1.5.dp, DopOchre) else BorderStroke(0.5.dp, DopBorderSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedMethod = method }
                                    .testTag("pay_method_${method.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedMethod = method }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = method.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) DopNavyPrimary else DopTextPrimary
                                        )
                                        Text(
                                            text = "${method.operatorName} • ${method.prefixHint}",
                                            fontSize = 10.sp,
                                            color = DopTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Phone Number / Account Input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text(s.paymentPhoneLabel) },
                        placeholder = { Text(s.paymentPhonePlaceholder) },
                        leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = DopNavyPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_phone_input"),
                        singleLine = true
                    )
                } else if (currentStep == "PROMPT_SENT") {
                    // Simulating USSD Push confirmation dialog
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DopNavyPrimary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = DopOchre, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = s.paymentUssdPromptSent,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (lang == AppLanguage.SWAHILI)
                                    "Tafadhali weka nenosiri kwenye simu yako ($phoneNumber) ili kuidhinisha muamala wa ${DoPStrings.tzs(amountTzs)} kwenda Dalalion Pocket."
                                else
                                    "Please enter your mobile money PIN on your handset ($phoneNumber) to approve the payment of ${DoPStrings.tzs(amountTzs)} to Dalalion Pocket.",
                                color = DopNeutralPearl.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (currentStep == "IDLE") {
                Button(
                    onClick = {
                        isProcessing = true
                        currentStep = "PROMPT_SENT"
                        coroutineScope.launch {
                            delay(1600) // Realistic USSD push delay
                            val tx = PropertyRepository.recordDirectPayment(
                                amountTzs = amountTzs,
                                payerPhone = phoneNumber,
                                method = selectedMethod,
                                source = source,
                                description = "$title - ${selectedMethod.title}"
                            )
                            currentStep = "CONFIRMED"
                            delay(400)
                            isProcessing = false
                            onPaymentSuccess(tx)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DopTrustGreen),
                    enabled = phoneNumber.isNotBlank() && !isProcessing,
                    modifier = Modifier.testTag("confirm_payment_sheet_btn")
                ) {
                    Text(s.payNowButton, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isProcessing) {
                TextButton(onClick = onDismiss) {
                    Text(s.paymentCancelButton)
                }
            }
        }
    )
}
