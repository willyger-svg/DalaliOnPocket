package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.guide.GuideDispatchService
import com.example.ui.components.DopBentoCard
import com.example.ui.theme.*

@Composable
fun GuideEarningsScreen() {
    val earnings by GuideDispatchService.earnings.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Guide Earnings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        }
        
        item {
            DopBentoCard(backgroundColor = DopNavyPrimary) {
                Column {
                    Text("Total Approved Earnings", fontSize = 12.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("TSh ${earnings}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DopTrustGreenLight)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Earnings are automatically paid to your M-Pesa account.", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                }
            }
        }
        
        item {
            Text("PENDING", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            EarningItemCard("DOP-BK-9182", "01 Nov 2023", 2500, "Pending")
        }
        
        item {
            Text("APPROVED", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            EarningItemCard("DOP-BK-4421", "30 Oct 2023", 2500, "Approved")
        }
    }
}

@Composable
fun EarningItemCard(jobId: String, date: String, amount: Int, status: String) {
    DopBentoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(jobId, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopNavyPrimary)
                Text(date, fontSize = 12.sp, color = DopTextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("TSh $amount", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopGuideAmberDark)
                Text(status, fontSize = 12.sp, color = if (status == "Approved") DopTrustGreen else DopTextMuted)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = DopTextMuted.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total customer fee:", fontSize = 11.sp, color = DopTextSecondary)
            Text("TSh 5,000", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Proposed Guide earning:", fontSize = 11.sp, color = DopTextSecondary)
            Text("TSh 2,500", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DopGuideAmberDark)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("DoP platform fee:", fontSize = 11.sp, color = DopTextSecondary)
            Text("TSh 2,500", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        }
    }
}
