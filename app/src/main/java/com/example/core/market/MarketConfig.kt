package com.example.core.market

enum class Region(val displayName: String) {
    DAR_ES_SALAAM("Dar es Salaam"),
    MOROGORO("Morogoro"),
    DODOMA("Dodoma"),
    ARUSHA("Arusha"),
    MWANZA("Mwanza"),
    MBEYA("Mbeya")
}

object MarketConfig {
    val activeMarkets = listOf(Region.DAR_ES_SALAAM)
    
    fun isMarketActive(region: String): Boolean {
        return activeMarkets.any { it.displayName.equals(region, ignoreCase = true) }
    }
}
