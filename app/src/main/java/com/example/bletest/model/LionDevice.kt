package com.example.bletest.model


enum class DeviceCategory {
    Sanctuary,
    LithiumBattery,
    Generator,
}


enum class LionDevice {
    Sanctuary,
    UT1300,
    UT3500,
    Summit,
    Safari,
    ;

    val category: DeviceCategory
        get() =
            when (this) {
                Sanctuary -> DeviceCategory.Sanctuary
                UT1300 -> DeviceCategory.LithiumBattery
                UT3500 -> DeviceCategory.LithiumBattery
                Summit -> DeviceCategory.Generator
                Safari -> DeviceCategory.Generator
            }

    companion object {
        fun from(name: String): LionDevice? {
            val lowercased = name.lowercase()
            return if (lowercased.contains("summit")) {
                Summit
            } else if (lowercased.contains("safari")) {
                Safari
            } else if (lowercased.contains("1300")) {
                UT1300
            } else if (lowercased.contains("ut3500")) {
                UT3500
            } else if (lowercased.contains("sanctuary")) {
                Sanctuary
            } else {
                null
            }
        }
    }
}
