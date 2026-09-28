package com.example.data.repository

import com.example.data.model.ChatAttachmentType
import com.example.data.model.GuideChatMessage
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object GuideChatRepository {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val initialMessages = listOf(
        GuideChatMessage(
            id = "MSG-1001",
            bookingCode = "DOP-BK-9182",
            propertyTitle = "Mikocheni B Beachfront Villa",
            senderRole = UserRole.GUIDE,
            senderName = "Rashid 'Dalali' Mwita (Guide)",
            messageText = "Habari Bwana Baraka! Naitwa Rashid Mwita, DoP Field Guide wako rasmi. Nimepokea dispatch ya kukutembeza na kukukabidhi funguo za ukaguzi wa Mikocheni B Beachfront Villa leo saa 10:00 Jioni.",
            timestamp = "Saa 09:30 Alasiri",
            isRead = true
        ),
        GuideChatMessage(
            id = "MSG-1002",
            bookingCode = "DOP-BK-9182",
            propertyTitle = "Mikocheni B Beachfront Villa",
            senderRole = UserRole.CUSTOMER,
            senderName = "Baraka Mushi (Mteja)",
            messageText = "Habari Rashid. Asante sana kwa taarifa. Naomba kuuliza kama ninaweza kuja na fundi wangu kuangalia wiring ya umeme na bomba za maji?",
            timestamp = "Saa 09:34 Alasiri",
            isRead = true
        ),
        GuideChatMessage(
            id = "MSG-1003",
            bookingCode = "DOP-BK-9182",
            propertyTitle = "Mikocheni B Beachfront Villa",
            senderRole = UserRole.GUIDE,
            senderName = "Rashid 'Dalali' Mwita (Guide)",
            messageText = "Bila shaka yoyote mkuu! Mwenye nyumba ametoa ruhusa kamili na Luku iko na units 85 tayari kwa majaribio ya taa na AC. Pia DAWASA maji yanatoka kwa presha nzuri.",
            timestamp = "Saa 09:36 Alasiri",
            isRead = true,
            attachmentType = ChatAttachmentType.QUICK_NOTE,
            attachmentData = "Luku Units: 85 • DAWASA Meter: Safi • Maji 24/7"
        ),
        GuideChatMessage(
            id = "MSG-1004",
            bookingCode = "DOP-BK-9182",
            propertyTitle = "Mikocheni B Beachfront Villa",
            senderRole = UserRole.GUIDE,
            senderName = "Rashid 'Dalali' Mwita (Guide)",
            messageText = "Nimetuma eneo kamili la kukutana njia panda ya Shoppers Plaza mkabala na duka la dawa. Nitakuwa nimevaa Reflective Vest ya DoP yenye nembo ya kijani.",
            timestamp = "Saa 09:40 Alasiri",
            isRead = true,
            attachmentType = ChatAttachmentType.LOCATION_PIN,
            attachmentData = "Shoppers Plaza Mikocheni, Dar es Salaam (-6.7621, 39.2458)"
        )
    )

    private val _messages = MutableStateFlow<List<GuideChatMessage>>(initialMessages)
    val messages: StateFlow<List<GuideChatMessage>> = _messages.asStateFlow()

    private fun currentTimeFormatted(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return "Saa " + sdf.format(Date())
    }

    fun sendMessage(
        bookingCode: String,
        propertyTitle: String,
        senderRole: UserRole,
        senderName: String,
        text: String,
        attachmentType: ChatAttachmentType = ChatAttachmentType.NONE,
        attachmentData: String? = null
    ) {
        if (text.isBlank() && attachmentType == ChatAttachmentType.NONE) return

        val newMsg = GuideChatMessage(
            bookingCode = bookingCode,
            propertyTitle = propertyTitle,
            senderRole = senderRole,
            senderName = senderName,
            messageText = text.trim(),
            timestamp = currentTimeFormatted(),
            isRead = true,
            attachmentType = attachmentType,
            attachmentData = attachmentData
        )

        _messages.value = _messages.value + newMsg

        // Auto-reply simulation to make communication vivid and responsive
        scope.launch {
            delay(1300)
            generateAutoReply(bookingCode, propertyTitle, senderRole, text)
        }
    }

    private fun generateAutoReply(
        bookingCode: String,
        propertyTitle: String,
        senderRole: UserRole,
        incomingText: String
    ) {
        val lower = incomingText.lowercase()
        val (replyRole, replyName, replyText) = if (senderRole == UserRole.CUSTOMER) {
            val response = when {
                lower.contains("getini") || lower.contains("nimefika") ->
                    "Nimekuona! Nasogea getini hapa nikiwa na funguo za nyumba."
                lower.contains("foleni") || lower.contains("dakika") || lower.contains("chelewa") ->
                    "Hakuna shida kabisa mkuu, jipatie muda. Mimi nipo hapa eneo la tukio nakusubiri kwa utulivu."
                lower.contains("njia") || lower.contains("wapi") || lower.contains("direction") ->
                    "Fuata lami ya Mikocheni kwa Warioba, ukifika kwenye kibao cha zahanati pinda kulia nyumba ya tatu ya ukuta mweupe na geti jeusi."
                lower.contains("mwenye nyumba") ->
                    "Mwenye nyumba yuko karibu na tayari amethibitisha bei ya kodi ni Sh 1,200,000 kwa mwezi na miezi 6 inakubalika."
                lower.contains("picha") ->
                    "Nimekutumia picha na uhakiki wa geti na barabara ya kuingia. Barabara ni lami safi mpaka getini."
                else ->
                    "Safi sana! Nimepata ujumbe wako. Nipo hapa tayari kukuongoza na kujibu swali lolote la ziada kuhusu nyumba hii ya ${propertyTitle}."
            }
            Triple(UserRole.GUIDE, "Rashid 'Dalali' Mwita (Guide)", response)
        } else {
            // Sent by Guide, replied by Customer
            val response = when {
                lower.contains("nimefika") || lower.contains("eneo") ->
                    "Asante sana Guide Rashid! Nipo hapa nje nimesimama karibu na gari jeusi."
                lower.contains("funguo") ->
                    "Vizuri sana, tufungulie tuanze na sebule kisha vyumba vya juu."
                else ->
                    "Asante kwa taarifa Rashid! Nimeipokea na nafuatilia maelekezo yako."
            }
            Triple(UserRole.CUSTOMER, "Baraka Mushi (Mteja)", response)
        }

        val autoMsg = GuideChatMessage(
            bookingCode = bookingCode,
            propertyTitle = propertyTitle,
            senderRole = replyRole,
            senderName = replyName,
            messageText = replyText,
            timestamp = currentTimeFormatted(),
            isRead = true
        )
        _messages.value = _messages.value + autoMsg
    }
}
