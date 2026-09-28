#!/bin/bash
sed -i 's/enum class CustomerTab {/enum class OwnerTab {\n    DASHBOARD,\n    PROPERTIES,\n    ADD_PROPERTY,\n    VIEWINGS,\n    ACCOUNT\n}\n\nenum class GuideTab {\n    DASHBOARD,\n    JOBS,\n    SCHEDULE,\n    EARNINGS,\n    ACCOUNT\n}\n\nenum class CustomerTab {/g' app/src/main/java/com/example/MainActivity.kt
