package com.example.data.repository

import com.example.data.model.PropertyShort
import com.example.data.model.ShortComment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Repository for Property Shorts (TikTok / YouTube Shorts style real estate walkthroughs)
 */
object PropertyShortsRepository {

    private val initialShorts = listOf(
        PropertyShort(
            id = "SHT-MSK-001",
            propertyId = "DOP-TZA-DAR-000184",
            propertyTitle = "Masaki Sunset Executive 3-Bedroom Apartment",
            ownerName = "Eng. Grace Ndesamburo",
            ownerPhone = "+255 784 567 890",
            ownerAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=200&q=80",
            location = "Masaki, Kinondoni, Dar es Salaam",
            priceDisplay = "TZS 1,800,000 / mwezi",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=800&q=80",
            description = "Tazama sebule na balcony yenye upepo mwanana wa bahari! Ina AC kila chumba, swimming pool na jenereta ya dharura. Karibu ukague leo.",
            roomHighlights = listOf("Vyumba 2 Master", "Sebule ya Kisasa", "Balcony ya Bahari", "Gym & Pool"),
            tags = listOf("#Masaki", "#Apartment", "#OceanView", "#DarEsSalaam", "#DoPShorts"),
            likesCount = 384,
            isLiked = false,
            viewsCount = 4210,
            commentsCount = 29,
            comments = listOf(
                ShortComment(userName = "Baraka Mushi", userRole = "Mpangaji", comment = "Kodi inajumuisha bili ya ulinzi na usafi?", timeAgo = "Saa 1 iliyopita"),
                ShortComment(userName = "Mohamed Guide", userRole = "DoP Guide", comment = "Nipo Masaki sasa hivi, mteja anayetaka kuona ninafungua milango mara moja!", timeAgo = "Dakika 30 zilizopita")
            ),
            createdAt = "Saa 3 zilizopita"
        ),
        PropertyShort(
            id = "SHT-MKC-002",
            propertyId = "DOP-TZA-DAR-000209",
            propertyTitle = "Mikocheni Garden Villa ya Kisasa",
            ownerName = "Mama Halima Mgaza",
            ownerPhone = "+255 713 999 888",
            ownerAvatarUrl = "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=200&q=80",
            location = "Mikocheni B, Kinondoni, Dar es Salaam",
            priceDisplay = "TZS 1,200,000 / mwezi",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80",
            description = "Nyumba ya peke yake ndani ya fensi salama! Tiles mpya, jiko la kisasa lenye makabati ya mbao, tanki la lita 5000 na geti la rimoti.",
            roomHighlights = listOf("Vyumba 3 (2 Master)", "Tiles za Uhispania", "Maji DAWASA 24/7", "Parking Magari 3"),
            tags = listOf("#Mikocheni", "#Townhouse", "#NyumbaBinafsi", "#KupangaDar"),
            likesCount = 219,
            isLiked = true,
            viewsCount = 2940,
            commentsCount = 18,
            comments = listOf(
                ShortComment(userName = "Dkt. Kelvin", userRole = "Mteja", comment = "Inaruhusu wanyama wa kufugwa (pet-friendly)?", timeAgo = "Saa 4 zilizopita")
            ),
            createdAt = "Jana"
        ),
        PropertyShort(
            id = "SHT-SNZ-003",
            propertyId = "DOP-TZA-DAR-000341",
            propertyTitle = "Fremu Mpya ya Biashara Sinza Mori",
            ownerName = "Rashid 'Dalali' Mwita",
            ownerPhone = "+255 682 555 111",
            ownerAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80",
            location = "Sinza Mori, Ubungo, Dar es Salaam",
            priceDisplay = "TZS 600,000 / mwezi",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=800&q=80",
            description = "Fremu ya kioo barabara kuu ya Sinza Mori! Eneo lina mzunguko mkubwa sana wa watu. Inafaa kwa duka la nguo, salon, maduka ya vifaa au duka la dawa.",
            roomHighlights = listOf("Milango ya Kioo (Glass Front)", "Luku Inajitegemea", "Choo cha Ndani", "Parking ya Wateja"),
            tags = listOf("#Sinza", "#Fremu", "#Biashara", "#CommercialSpace"),
            likesCount = 512,
            isLiked = false,
            viewsCount = 6100,
            commentsCount = 42,
            comments = listOf(
                ShortComment(userName = "Aisha Salum", userRole = "Mjasiriamali", comment = "Je, kuna malipo ya miezi 6 au lazima mwaka?", timeAgo = "Saa 2 zilizopita")
            ),
            createdAt = "Siku 2 zilizopita"
        ),
        PropertyShort(
            id = "SHT-OYB-004",
            propertyId = "DOP-TZA-DAR-000412",
            propertyTitle = "Executive Studio Kijitonyama - Fully Furnished",
            ownerName = "Eng. Grace Ndesamburo",
            ownerPhone = "+255 784 567 890",
            ownerAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=200&q=80",
            location = "Kijitonyama, Kinondoni, Dar es Salaam",
            priceDisplay = "TZS 500,000 / mwezi",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
            description = "Studio ya kisasa iliyokamilika kila kitu (kitanda, sofa, friji, TV, microwave, AC na internet ya fiber). Wahi sasa!",
            roomHighlights = listOf("Fully Furnished", "WiFi Bure", "AC & Maji Moto", "Ulinzi Mkali"),
            tags = listOf("#Kijitonyama", "#StudioApartment", "#Furnished", "#DoPShorts"),
            likesCount = 188,
            isLiked = false,
            viewsCount = 1980,
            commentsCount = 11,
            comments = emptyList(),
            createdAt = "Siku 3 zilizopita"
        )
    )

    private val _shorts = MutableStateFlow(initialShorts)
    val shorts: StateFlow<List<PropertyShort>> = _shorts.asStateFlow()

    /**
     * Property Owner posts a new short video
     */
    fun postShort(newShort: PropertyShort) {
        val currentList = _shorts.value.toMutableList()
        currentList.add(0, newShort) // Add to top of feed
        _shorts.value = currentList
    }

    /**
     * Like or unlike a property short video
     */
    fun toggleLike(shortId: String) {
        _shorts.value = _shorts.value.map { short ->
            if (short.id == shortId) {
                val newLiked = !short.isLiked
                val newCount = if (newLiked) short.likesCount + 1 else maxOf(0, short.likesCount - 1)
                short.copy(isLiked = newLiked, likesCount = newCount)
            } else {
                short
            }
        }
    }

    /**
     * Add comment or inquiry to a short video
     */
    fun addComment(shortId: String, userName: String, userRole: String, text: String) {
        if (text.isBlank()) return
        val newComment = ShortComment(
            userName = userName,
            userRole = userRole,
            comment = text.trim()
        )
        _shorts.value = _shorts.value.map { short ->
            if (short.id == shortId) {
                short.copy(
                    commentsCount = short.commentsCount + 1,
                    comments = listOf(newComment) + short.comments
                )
            } else {
                short
            }
        }
    }

    /**
     * Delete a property short video
     */
    fun deleteShort(shortId: String) {
        _shorts.value = _shorts.value.filter { it.id != shortId }
    }
}
