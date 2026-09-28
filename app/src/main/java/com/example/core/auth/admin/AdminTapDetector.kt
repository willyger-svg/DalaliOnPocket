package com.example.core.auth.admin

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Reusable, isolated 14-tap detector for hidden administrative access.
 * 
 * Rules:
 * - Exactly 14 taps required to trigger
 * - Discrete: No counter, no visual cues, no hints
 * - Inactivity reset: If delay between taps exceeds [resetTimeoutMs], counter resets
 * - Clean execution: Triggers [onAdminTrigger] on the 14th tap and resets
 */
class AdminTapDetector(
    val targetTaps: Int = 14,
    val resetTimeoutMs: Long = 2500L,
    private val onAdminTrigger: () -> Unit
) {
    var tapCount: Int = 0
        private set
    var lastTapTimestamp: Long = 0L
        private set

    /**
     * Records a tap. Returns true ONLY when the [targetTaps] threshold is reached.
     */
    @Synchronized
    fun registerTap(currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        if (lastTapTimestamp > 0L && (currentTimeMs - lastTapTimestamp > resetTimeoutMs)) {
            // Sequence timed out, reset and count this as tap #1
            tapCount = 1
        } else {
            tapCount++
        }
        lastTapTimestamp = currentTimeMs

        if (tapCount >= targetTaps) {
            tapCount = 0
            lastTapTimestamp = 0L
            onAdminTrigger()
            return true
        }
        return false
    }

    /**
     * Resets tap state
     */
    @Synchronized
    fun reset() {
        tapCount = 0
        lastTapTimestamp = 0L
    }
}

/**
 * Non-blocking Compose modifier that detects rapid, discrete taps on the designated hidden area
 * without swallowing parent scroll events or interfering with adjacent interactive controls.
 */
fun Modifier.adminSecretTap(detector: AdminTapDetector): Modifier = this.pointerInput(detector) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val up = waitForUpOrCancellation()
        if (up != null) {
            detector.registerTap()
        }
    }
}
