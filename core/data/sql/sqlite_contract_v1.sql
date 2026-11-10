-- EVOLUNE local persistence contract v1: SQLite behavioral tests only.
-- Room generated schema must be exported and diffed against this contract during Android build.
-- Do not apply this file as a Room migration; version 1 is a fresh database.
PRAGMA foreign_keys = ON;
CREATE TABLE growth_areas (
    id TEXT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    createdAtEpochMs INTEGER NOT NULL,
    updatedAtEpochMs INTEGER NOT NULL
);
CREATE TABLE challenges (
    id TEXT NOT NULL PRIMARY KEY,
    growthAreaId TEXT,
    title TEXT NOT NULL,
    startDate TEXT NOT NULL,
    zoneId TEXT NOT NULL,
    milestoneKind TEXT NOT NULL,
    target INTEGER NOT NULL,
    createdAtEpochMs INTEGER NOT NULL,
    archivedAtEpochMs INTEGER,
    FOREIGN KEY(growthAreaId) REFERENCES growth_areas(id) ON DELETE SET NULL
);
CREATE INDEX index_challenges_growthAreaId ON challenges(growthAreaId);
CREATE TABLE schedule_versions (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    challengeId TEXT NOT NULL,
    effectiveFrom TEXT NOT NULL,
    recurrenceKind TEXT NOT NULL,
    recurrenceArgument TEXT,
    action TEXT NOT NULL,
    cueTime TEXT,
    fallbackAction TEXT,
    createdAtEpochMs INTEGER NOT NULL,
    FOREIGN KEY(challengeId) REFERENCES challenges(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX index_schedule_versions_challengeId_effectiveFrom ON schedule_versions(challengeId, effectiveFrom);
CREATE TABLE practice_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    challengeId TEXT NOT NULL,
    localDate TEXT NOT NULL,
    outcome TEXT NOT NULL,
    revision INTEGER NOT NULL,
    recordedAtEpochMs INTEGER NOT NULL,
    note TEXT,
    FOREIGN KEY(challengeId) REFERENCES challenges(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX index_practice_records_challengeId_localDate_revision ON practice_records(challengeId, localDate, revision);
CREATE TABLE pause_windows (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    challengeId TEXT NOT NULL,
    fromDate TEXT NOT NULL,
    throughDate TEXT NOT NULL,
    FOREIGN KEY(challengeId) REFERENCES challenges(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX index_pause_windows_challengeId_fromDate_throughDate ON pause_windows(challengeId, fromDate, throughDate);
CREATE TABLE journal_entries (
    id TEXT NOT NULL PRIMARY KEY,
    challengeId TEXT,
    growthAreaId TEXT,
    body TEXT NOT NULL,
    mood TEXT,
    energy INTEGER,
    createdAtEpochMs INTEGER NOT NULL,
    updatedAtEpochMs INTEGER NOT NULL,
    FOREIGN KEY(challengeId) REFERENCES challenges(id) ON DELETE SET NULL,
    FOREIGN KEY(growthAreaId) REFERENCES growth_areas(id) ON DELETE SET NULL
);
CREATE INDEX index_journal_entries_challengeId ON journal_entries(challengeId);
CREATE INDEX index_journal_entries_growthAreaId ON journal_entries(growthAreaId);
PRAGMA user_version = 1;
