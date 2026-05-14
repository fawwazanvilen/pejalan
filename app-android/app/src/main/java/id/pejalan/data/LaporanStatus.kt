package id.pejalan.data

enum class LaporanStatus {
    DRAFT,       // user-held; not yet "submitted" to the audit corpus. excluded from Linimasa / Peta / stats.
    PENDING,     // submitted; classification queued
    CLASSIFIED,  // classifier done; all fields populated
    FAILED;      // classifier errored or photo unreadable; user can retry/edit

    companion object {
        // Unknown values fall back to CLASSIFIED so a future enum rename doesn't crash on read.
        fun fromString(s: String?): LaporanStatus =
            values().firstOrNull { it.name == s?.trim()?.uppercase() } ?: CLASSIFIED
    }
}
