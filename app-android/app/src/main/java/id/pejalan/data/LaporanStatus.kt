package id.pejalan.data

enum class LaporanStatus {
    PENDING,     // photo captured + saved; classification queued
    CLASSIFIED,  // Gemma done; all fields populated
    FAILED;      // Gemma errored or photo unreadable; user can retry/edit

    companion object {
        fun fromString(s: String?): LaporanStatus =
            values().firstOrNull { it.name == s?.trim()?.uppercase() } ?: CLASSIFIED
    }
}
