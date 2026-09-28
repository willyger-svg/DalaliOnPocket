package com.example.core.market

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MarketService {
    private val _currentMarket = MutableStateFlow(Region.DAR_ES_SALAAM)
    val currentMarket: StateFlow<Region> = _currentMarket.asStateFlow()

    fun setMarket(region: Region) {
        if (MarketConfig.activeMarkets.contains(region)) {
            _currentMarket.value = region
        }
    }
}
