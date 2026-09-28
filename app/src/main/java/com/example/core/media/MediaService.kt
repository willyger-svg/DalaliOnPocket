package com.example.core.media

import kotlinx.coroutines.delay
import java.util.UUID

enum class MediaStorageType {
    LOCAL_DEVICE,
    REMOTE_CDN
}

data class UploadedMedia(
    val id: String = UUID.randomUUID().toString(),
    val storageType: MediaStorageType,
    val localUri: String? = null,
    val remoteUrl: String? = null,
    val isUploaded: Boolean = false,
    val uploadProgress: Float = 0f
)

/**
 * Clean abstraction for handling Media Uploads.
 * Prevents pretending that local files are immediately available on a production backend.
 */
object MediaService {
    
    suspend fun uploadLocalMedia(localUri: String): UploadedMedia {
        // Simulate upload process
        delay(1500)
        
        // In a real implementation, this would upload to S3/Firebase Storage
        // and return the remote URL. For now, we mock the result.
        return UploadedMedia(
            storageType = MediaStorageType.REMOTE_CDN,
            localUri = localUri,
            remoteUrl = "https://example.com/mock-upload-${UUID.randomUUID().toString().take(6)}.jpg",
            isUploaded = true,
            uploadProgress = 1.0f
        )
    }

    fun getLocalPreview(localUri: String): UploadedMedia {
        return UploadedMedia(
            storageType = MediaStorageType.LOCAL_DEVICE,
            localUri = localUri,
            remoteUrl = null,
            isUploaded = false,
            uploadProgress = 0f
        )
    }
}
