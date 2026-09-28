package com.example.core.media

import kotlinx.coroutines.delay
import java.util.UUID

enum class VirtualTourType {
    RECORDED_WALKTHROUGH,
    TOUR_360,
    LIVE_VIRTUAL_VIEWING
}

data class VirtualTour(
    val id: String = UUID.randomUUID().toString(),
    val propertyId: String,
    val type: VirtualTourType,
    val mediaUrl: String? = null,
    val externalUrl: String? = null,
    val durationSeconds: Int = 0,
    val title: String = "",
    val isLiveReady: Boolean = false
)

/**
 * Service abstraction for handling Virtual Tours.
 * In the future, this will connect to real 360 viewer backends and WebRTC live streaming.
 */
object VirtualTourService {
    
    suspend fun createVirtualTour(propertyId: String, type: VirtualTourType, title: String, mediaUrl: String? = null): VirtualTour {
        // Simulate network delay
        delay(1000)
        return VirtualTour(
            propertyId = propertyId,
            type = type,
            title = title,
            mediaUrl = mediaUrl,
            isLiveReady = type == VirtualTourType.LIVE_VIRTUAL_VIEWING
        )
    }

    suspend fun fetchToursForProperty(propertyId: String): List<VirtualTour> {
        delay(500)
        // Mock data
        return emptyList()
    }
}
