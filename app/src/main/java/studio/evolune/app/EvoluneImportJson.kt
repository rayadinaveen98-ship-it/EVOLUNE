package studio.evolune.app

import org.json.JSONArray
import org.json.JSONObject
import studio.evolune.data.*
import studio.evolune.domain.MilestoneKind
import java.time.LocalDate
import java.time.ZoneId

/**
 * Strict JSON v1 backup reader, with validation before any database writes.
 * No network or untrusted code execution. External files must never be
 * loaded into the live database without an explicit REPLACE confirmation.
 */
object EvoluneImportJson {
    const val MAX_FILE_BYTES = 10 * 1024 * 1024

    fun decode(source: String): StoredLocalExport {
        require(source.toByteArray(Charsets.UTF_8).size <= MAX_FILE_BYTES) {
            "Backup is too large (10 MB limit). No data was changed."
        }
        try {
            val doc = JSONObject(source)
            require(doc.getString("format") == EvoluneExportJson.FORMAT) {
                "Not an EVOLUNE export"
            }
            require(doc.getInt("schemaVersion") == EvoluneExportJson.SCHEMA_VERSION) {
                "Unsupported backup version"
            }

            fun arr(key: String, max: Int): JSONArray {
                val a = doc.getJSONArray(key)
                require(a.length() <= max) { "$key has too many records" }
                return a
            }
            fun optString(o: JSONObject, name: String, max: Int = 100_000): String? =
                if (o.isNull(name)) null else o.getString(name).also {
                    require(it.length <= max) { "$name is too long" }
                }
            fun required(o: JSONObject, name: String, max: Int = 200): String =
                requireNotNull(optString(o, name, max)).also {
                    require(it.isNotBlank()) { "$name is blank" }
                }
            fun keyId(o: JSONObject, name: String = "id"): String =
                required(o, name, 150)
            fun millis(o: JSONObject, name: String): Long = o.getLong(name).also {
                require(it >= 0) { "$name cannot be negative" }
            }
            fun rowId(o: JSONObject): Long = o.getLong("id").also {
                require(it > 0) { "Invalid database row id" }
            }
            fun date(text: String): LocalDate = LocalDate.parse(text)

            val areas = arr("growthAreas", 500).let { a ->
                (0 until a.length()).map { idx ->
                    val o = a.getJSONObject(idx)
                    GrowthAreaEntity(keyId(o), required(o, "name", 150),
                        optString(o, "description"),
                        millis(o, "createdAtEpochMs"), millis(o, "updatedAtEpochMs"))
                }
            }
            val challenges = arr("challenges", 2000).let { a ->
                (0 until a.length()).map { idx ->
                    val o = a.getJSONObject(idx)
                    ChallengeEntity(
                        id=keyId(o),growthAreaId=optString(o,"growthAreaId",150),
                        title=required(o,"title",150),
                        startDate=required(o,"startDate",16).also(::date),
                        zoneId=required(o,"zoneId",80).also(ZoneId::of),
                        milestoneKind=required(o,"milestoneKind",40)
                            .also { MilestoneKind.valueOf(it) },
                        target=o.getInt("target").also { require(it in 1..3650) },
                        createdAtEpochMs=millis(o,"createdAtEpochMs"),
                        archivedAtEpochMs=if(o.isNull("archivedAtEpochMs")) null
                            else millis(o,"archivedAtEpochMs")
                    )
                }
            }
            val schedules=arr("scheduleVersions",5000).let { a ->
                (0 until a.length()).map { idx ->
                    val o=a.getJSONObject(idx)
                    ScheduleVersionEntity(
                        id=rowId(o),challengeId=keyId(o,"challengeId"),
                        effectiveFrom=required(o,"effectiveFrom",16).also(::date),
                        recurrenceKind=required(o,"recurrenceKind",40),
                        recurrenceArgument=optString(o,"recurrenceArgument",80),
                        action=required(o,"action",500),
                        cueTime=optString(o,"cueTime",20),
                        fallbackAction=optString(o,"fallbackAction",500),
                        createdAtEpochMs=millis(o,"createdAtEpochMs")
                    )
                }
            }
            val practices=arr("practiceRecords",100000).let { a ->
                (0 until a.length()).map { idx ->
                    val o=a.getJSONObject(idx)
                    PracticeRecordEntity(
                        id=rowId(o),challengeId=keyId(o,"challengeId"),
                        localDate=required(o,"localDate",16).also(::date),
                        outcome=required(o,"outcome",35),
                        revision=o.getLong("revision").also { require(it>0) },
                        recordedAtEpochMs=millis(o,"recordedAtEpochMs"),
                        note=optString(o,"note",100_000)
                    )
                }
            }
            val pauses=arr("pauseWindows",5000).let { a ->
                (0 until a.length()).map { idx ->
                    val o=a.getJSONObject(idx)
                    PauseWindowEntity(
                        id=rowId(o),challengeId=keyId(o,"challengeId"),
                        fromDate=required(o,"fromDate",16).also(::date),
                        throughDate=required(o,"throughDate",16).also(::date)
                    ).also { require(!date(it.throughDate).isBefore(date(it.fromDate))) }
                }
            }
            val journal=arr("journalEntries",20000).let { a ->
                (0 until a.length()).map { idx ->
                    val o=a.getJSONObject(idx)
                    JournalEntryEntity(
                        id=keyId(o),challengeId=optString(o,"challengeId",150),
                        growthAreaId=optString(o,"growthAreaId",150),
                        body=required(o,"body",100_000),
                        mood=optString(o,"mood",100),
                        energy=if(o.isNull("energy")) null
                            else o.getInt("energy").also { require(it in 1..5) },
                        createdAtEpochMs=millis(o,"createdAtEpochMs"),
                        updatedAtEpochMs=millis(o,"updatedAtEpochMs")
                    ).also { require(it.updatedAtEpochMs>=it.createdAtEpochMs) }
                }
            }

            fun <T> unique(values:List<T>, what:String) {
                require(values.size==values.toSet().size) { "Duplicate $what" }
            }
            unique(areas.map { it.id }, "growth area id")
            unique(challenges.map { it.id }, "challenge id")
            unique(schedules.map { it.id }, "schedule row id")
            unique(practices.map { it.id }, "practice row id")
            unique(pauses.map { it.id }, "pause row id")
            unique(journal.map { it.id }, "journal id")
            unique(schedules.map { it.challengeId to it.effectiveFrom }, "schedule date")
            unique(practices.map { Triple(it.challengeId,it.localDate,it.revision) }, "practice revision")
            unique(pauses.map { Triple(it.challengeId,it.fromDate,it.throughDate) }, "pause window")

            val areaIds=areas.map { it.id }.toSet()
            val challengeById=challenges.associateBy { it.id }
            challenges.forEach { ch ->
                require(ch.growthAreaId==null || ch.growthAreaId in areaIds) {
                    "Challenge references an unknown growth area"
                }
                val rows=schedules.filter { it.challengeId==ch.id }
                val plan=StorageCodec.plan(
                    StorageCodec.PersistedPlan(ch.id,ch.title,ch.startDate,ch.zoneId,
                        ch.milestoneKind,ch.target),
                    rows.map {
                        StorageCodec.PersistedSchedule(it.effectiveFrom,it.recurrenceKind,
                            it.recurrenceArgument,it.action,it.cueTime,it.fallbackAction)
                    }
                )
                require(plan.schedules.isNotEmpty() && plan.schedules.first().effectiveFrom==plan.startDate)
            }
            require(schedules.all { it.challengeId in challengeById }) {
                "Unknown challenge referenced by schedule"
            }
            practices.forEach { record ->
                val challenge=requireNotNull(challengeById[record.challengeId]) {
                    "Unknown challenge referenced by practice"
                }
                val on=date(record.localDate)
                require(!on.isBefore(date(challenge.startDate))) {
                    "Practice predates its challenge"
                }
                if(challenge.milestoneKind=="CALENDAR_DAYS") {
                    require(on.isBefore(date(challenge.startDate).plusDays(challenge.target.toLong()))) {
                        "Practice is after its challenge period"
                    }
                }
                StorageCodec.record(StorageCodec.PersistedLog(
                    record.challengeId,record.localDate,record.outcome,record.revision,
                    record.recordedAtEpochMs,record.note))
            }
            require(pauses.all { it.challengeId in challengeById }) {
                "Unknown challenge referenced by pause"
            }
            journal.forEach {
                require(it.challengeId==null || it.challengeId in challengeById) {
                    "Journal references an unknown challenge"
                }
                require(it.growthAreaId==null || it.growthAreaId in areaIds) {
                    "Journal references an unknown growth area"
                }
            }

            return StoredLocalExport(areas,challenges,schedules,practices,pauses,journal)
        } catch (error:Exception) {
            throw IllegalArgumentException(
                "Backup failed validation. No data was changed: " +
                (error.message ?: "Invalid structure"),error
            )
        }
    }
}
