#!/bin/bash
sed -i 's/var currentTab by remember { mutableStateOf(CustomerTab.HOME) }/var currentTab by remember { mutableStateOf(CustomerTab.HOME) }\n    var currentOwnerTab by remember { mutableStateOf(OwnerTab.DASHBOARD) }\n    var currentGuideTab by remember { mutableStateOf(GuideTab.DASHBOARD) }/g' app/src/main/java/com/example/MainActivity.kt
