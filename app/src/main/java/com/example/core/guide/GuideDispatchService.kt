package com.example.core.guide

import com.example.data.model.GuideJobStatus
import com.example.data.model.ViewingBooking
import com.example.data.model.ViewingStatus
import com.example.data.repository.PropertyRepository
import com.example.core.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay

/**
 * Service to handle dispatching, tracking, and completing Guide assignments.
 */
object GuideDispatchService {
    
    // In a real app, this would be observing backend WebSockets/FCM for new job offers
    private val _availableJobs = MutableStateFlow<List<ViewingBooking>>(emptyList())
    val availableJobs: StateFlow<List<ViewingBooking>> = _availableJobs.asStateFlow()

    private val _earnings = MutableStateFlow<Long>(0L)
    val earnings: StateFlow<Long> = _earnings.asStateFlow()

    fun loadMockJobs() {
        val jobs = PropertyRepository.viewingBookings.value.filter { 
            it.status == ViewingStatus.REQUESTED || it.status == ViewingStatus.CONFIRMED 
        }
        _availableJobs.value = jobs
    }

    suspend fun acceptJob(bookingCode: String) {
        val user = AuthManager.currentUser.value ?: return
        if (user.guideCapability == com.example.data.model.GuideCapabilityStatus.SUSPENDED) {
            return
        }
        delay(500)
        PropertyRepository.advanceViewingStatus(bookingCode, ViewingStatus.GUIDE_ASSIGNED)
        _availableJobs.value = _availableJobs.value.filter { it.bookingCode != bookingCode }
    }

    suspend fun declineJob(bookingCode: String) {
        delay(300)
        _availableJobs.value = _availableJobs.value.filter { it.bookingCode != bookingCode }
    }

    fun completeViewing(bookingCode: String) {
        PropertyRepository.advanceViewingStatus(bookingCode, ViewingStatus.COMPLETED)
        _earnings.value += 2500L
    }
}
