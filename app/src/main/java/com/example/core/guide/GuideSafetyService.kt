package com.example.core.guide

import com.example.data.model.GuideIncident
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object GuideSafetyService {

    private val _reportedIncidents = MutableStateFlow<List<GuideIncident>>(emptyList())
    val reportedIncidents: StateFlow<List<GuideIncident>> = _reportedIncidents.asStateFlow()

    fun reportSafetyIssue(jobId: String, description: String) {
        val incident = GuideIncident(
            jobId = jobId,
            category = "SAFETY_ISSUE",
            description = description
        )
        _reportedIncidents.value = _reportedIncidents.value + incident
    }

    fun reportIncident(jobId: String, category: String, description: String) {
        val incident = GuideIncident(
            jobId = jobId,
            category = category,
            description = description
        )
        _reportedIncidents.value = _reportedIncidents.value + incident
    }
}
