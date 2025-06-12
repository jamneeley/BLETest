package com.example.bletest.model

enum class LionDeviceType {
    Sanctuary,
    UT1300,
    UT3500,
    Summit,
    Safari,
    ;

    companion object {
        fun from(name: String): LionDeviceType? {
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
