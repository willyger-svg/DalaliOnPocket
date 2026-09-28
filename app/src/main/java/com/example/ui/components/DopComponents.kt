package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.core.localization.strings
import com.example.data.model.LocationPrivacy
import com.example.data.model.TransactionType
import com.example.ui.theme.*

@Composable
fun DopBentoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun DopBadge(
    text: String,
    icon: ImageVector? = null,
    color: Color = DopTrustGreen,
    backgroundColor: Color = DopTrustGreenContainer,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DopExpressBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = DopOchreContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "DoP Express",
                tint = DopOchre,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "DoP EXPRESS",
                color = DopOchre,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun LocationPrivacyChip(privacy: LocationPrivacy, modifier: Modifier = Modifier) {
    val (title, icon, color, bg) = when (privacy) {
        LocationPrivacy.EXACT -> Quad("Eneo Halisi (Exact)", Icons.Default.CheckCircle, DopTrustGreen, DopTrustGreenContainer)
        LocationPrivacy.APPROXIMATE -> Quad("Makadirio (~500m)", Icons.Default.NearMe, DopOchre, DopOchreContainer)
        LocationPrivacy.HIDDEN -> Quad("Wilaya Pekee (Protected)", Icons.Default.Shield, DopNavyElevated, DopNeutralPearl)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = title, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun DopPriceDisplay(
    priceTzs: Long,
    transactionType: TransactionType,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = DoPStrings.tzs(priceTzs),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DopNavyPrimary
        )
        if (transactionType == TransactionType.RENT) {
            Text(
                text = " " + DoPStrings.perMonth(lang),
                fontSize = 12.sp,
                color = DopTextSecondary,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun SyncStatusChip(
    isConnected: Boolean,
    lang: AppLanguage = AppLanguage.SWAHILI,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val s = strings(lang)
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isConnected) DopTrustGreenContainer else Color(0xFFFEE2E2),
        border = BorderStroke(1.dp, if (isConnected) DopTrustGreenLight else Color(0xFFFCA5A5))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) DopTrustGreen else DopError)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isConnected) s.syncOnline else s.syncOffline,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isConnected) DopTrustGreen else DopError
            )
        }
    }
}
