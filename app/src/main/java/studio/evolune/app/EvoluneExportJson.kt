package studio.evolune.app

import org.json.JSONArray
import org.json.JSONObject
import studio.evolune.data.StoredLocalExport
import java.time.Instant

/**
 * Versioned user-controlled JSON export; no analytics, account or upload.
 * Contains potentially sensitive reflections and must be saved only via a user-selected SAF URI.
 *
 * This format is export-only in this revision. Never claim restore until a separately
 * validated importer and foreign-key/migration tests have been implemented.
 */
object EvoluneExportJson {
    const val FORMAT = "EVOLUNE_LOCAL_EXPORT"
    const val SCHEMA_VERSION = 1

    fun encode(rows: StoredLocalExport, exportedAt: Instant = Instant.now()): String {
        fun item(vararg fields: Pair<String, Any?>): JSONObject {
            val obj = JSONObject()
            fields.forEach { (key, value) -> obj.put(key, value ?: JSONObject.NULL) }
            return obj
        }
        fun <T> items(source: List<T>, encode: (T) -> JSONObject): JSONArray {
            val array = JSONArray()
            source.forEach { array.put(encode(it)) }
            return array
        }
        val root = item(
            "format" to FORMAT,
            "schemaVersion" to SCHEMA_VERSION,
            "exportedAtUtc" to exportedAt.toString(),
            "privacyNote" to "Private local data. Handle and store securely. No online upload performed."
        )
        root.put("growthAreas", items(rows.growthAreas) { a ->
            item("id" to a.id, "name" to a.name, "description" to a.description,
                "createdAtEpochMs" to a.createdAtEpochMs,
                "updatedAtEpochMs" to a.updatedAtEpochMs)
        })
        root.put("challenges", items(rows.challenges) { c ->
            item("id" to c.id, "growthAreaId" to c.growthAreaId,
                "title" to c.title, "startDate" to c.startDate, "zoneId" to c.zoneId,
                "milestoneKind" to c.milestoneKind, "target" to c.target,
                "createdAtEpochMs" to c.createdAtEpochMs,
                "archivedAtEpochMs" to c.archivedAtEpochMs)
        })
        root.put("scheduleVersions", items(rows.schedules) { s ->
            item("id" to s.id, "challengeId" to s.challengeId,
                "effectiveFrom" to s.effectiveFrom, "recurrenceKind" to s.recurrenceKind,
                "recurrenceArgument" to s.recurrenceArgument,
                "action" to s.action, "cueTime" to s.cueTime,
                "fallbackAction" to s.fallbackAction,
                "createdAtEpochMs" to s.createdAtEpochMs)
        })
        root.put("practiceRecords", items(rows.practices) { r ->
            item("id" to r.id, "challengeId" to r.challengeId,
                "localDate" to r.localDate, "outcome" to r.outcome,
                "revision" to r.revision, "recordedAtEpochMs" to r.recordedAtEpochMs,
                "note" to r.note)
        })
        root.put("pauseWindows", items(rows.pauses) { p ->
            item("id" to p.id, "challengeId" to p.challengeId,
                "fromDate" to p.fromDate, "throughDate" to p.throughDate)
        })
        root.put("journalEntries", items(rows.journal) { j ->
            item("id" to j.id, "challengeId" to j.challengeId,
                "growthAreaId" to j.growthAreaId, "body" to j.body,
                "mood" to j.mood, "energy" to j.energy,
                "createdAtEpochMs" to j.createdAtEpochMs,
                "updatedAtEpochMs" to j.updatedAtEpochMs)
        })
        return root.toString(2)
    }
}
