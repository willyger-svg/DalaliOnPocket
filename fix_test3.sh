#!/bin/bash
sed -i 's/val bookingCode = "MOCK-" + (1000..9999).random()/val mockProperty = PropertyRepository.properties.value.first()\n        val job = PropertyRepository.bookViewing(mockProperty, "CUST-1", "John Doe", ViewingType.DOP_ASSISTED_VISIT, "2023-11-01", "10:00", "Masaki", "")\n        PropertyRepository.advanceViewingStatus(job.bookingCode, ViewingStatus.REQUESTED)/g' app/src/test/java/com/example/GuideDispatchTest.kt
sed -i '/val mockJob = ViewingBooking/d' app/src/test/java/com/example/GuideDispatchTest.kt
sed -i '/PropertyRepository.javaClass.getDeclaredField/d' app/src/test/java/com/example/GuideDispatchTest.kt
