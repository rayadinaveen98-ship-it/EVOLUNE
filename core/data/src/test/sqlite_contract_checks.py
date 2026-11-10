"""Offline SQLite-level contract checks. Not a Room runtime or Android integration test."""
from pathlib import Path
import sqlite3
import tempfile

SCHEMA = Path(__file__).resolve().parents[2] / "sql" / "sqlite_contract_v1.sql"


def connect(path):
    db = sqlite3.connect(path, isolation_level=None)
    db.execute("PRAGMA foreign_keys = ON")
    assert db.execute("PRAGMA foreign_keys").fetchone()[0] == 1
    return db


def run():
    with tempfile.TemporaryDirectory(prefix="evolune_db_test_") as tmp:
        filename = Path(tmp) / "evolune.sqlite3"
        db = connect(filename)
        db.executescript(SCHEMA.read_text())
        assert db.execute("PRAGMA user_version").fetchone()[0] == 1
        assert db.execute("PRAGMA integrity_check").fetchone()[0] == "ok"
        now = 1794355200000

        # Challenge & initial schedule are an all-or-nothing transaction.
        db.execute("BEGIN IMMEDIATE")
        db.execute("INSERT INTO growth_areas VALUES (?,?,?,?,?)", ("mind", "Learning", None, now, now))
        db.execute("INSERT INTO challenges VALUES (?,?,?,?,?,?,?,?,?)", (
            "read", "mind", "Read for 10 minutes", "2026-11-12", "Asia/Kolkata", "CALENDAR_DAYS", 21, now, None))
        db.execute("INSERT INTO schedule_versions (challengeId,effectiveFrom,recurrenceKind,recurrenceArgument,action,cueTime,fallbackAction,createdAtEpochMs) VALUES (?,?,?,?,?,?,?,?)", (
            "read", "2026-11-12", "WEEKDAYS", None, "Read 10 minutes", "21:30", "Read one page", now))
        db.execute("COMMIT")

        # A failed initial schedule must roll back its parent challenge as a unit.
        db.execute("BEGIN IMMEDIATE")
        db.execute("INSERT INTO challenges VALUES (?,?,?,?,?,?,?,?,?)", (
            "rolled-back", "mind", "Incomplete", "2026-11-12", "Asia/Kolkata", "CALENDAR_DAYS", 21, now, None))
        try:
            db.execute("INSERT INTO schedule_versions (challengeId,effectiveFrom,recurrenceKind,action,createdAtEpochMs) VALUES (?,?,?,?,?)", (
                "missing-parent", "2026-11-12", "DAILY", "Impossible", now))
            raise AssertionError("Foreign key violation was accepted")
        except sqlite3.IntegrityError:
            db.execute("ROLLBACK")
        assert db.execute("SELECT COUNT(*) FROM challenges WHERE id='rolled-back'").fetchone()[0] == 0

        # Invalid duplicate schedule cannot rewrite history.
        try:
            db.execute("INSERT INTO schedule_versions (challengeId,effectiveFrom,recurrenceKind,action,createdAtEpochMs) VALUES (?,?,?,?,?)", (
                "read", "2026-11-12", "DAILY", "Overwritten", now))
            raise AssertionError("Duplicate schedule revision was accepted")
        except sqlite3.IntegrityError:
            pass

        db.execute("INSERT INTO schedule_versions (challengeId,effectiveFrom,recurrenceKind,recurrenceArgument,action,createdAtEpochMs) VALUES (?,?,?,?,?,?)", (
            "read", "2026-11-19", "EVERY_N_DAYS", "2", "Read 15 minutes", now))
        db.execute("INSERT INTO practice_records (challengeId,localDate,outcome,revision,recordedAtEpochMs,note) VALUES (?,?,?,?,?,?)", (
            "read", "2026-11-12", "MISSED", 1, now, None))
        db.execute("INSERT INTO practice_records (challengeId,localDate,outcome,revision,recordedAtEpochMs,note) VALUES (?,?,?,?,?,?)", (
            "read", "2026-11-12", "COMPLETED", 2, now + 2000, "Corrected after remembering"))
        db.execute("INSERT INTO pause_windows (challengeId,fromDate,throughDate) VALUES (?,?,?)", (
            "read", "2026-11-21", "2026-11-22"))
        db.execute("INSERT INTO journal_entries VALUES (?,?,?,?,?,?,?,?)", (
            "journal-1", "read", "mind", "Quiet evening reflection", "calm", 4, now, now))
        db.close()

        # Simulate process death / restart: on-disk storage must survive a new connection.
        db = connect(filename)
        assert db.execute("SELECT COUNT(*) FROM challenges").fetchone()[0] == 1
        assert db.execute("SELECT COUNT(*) FROM schedule_versions WHERE challengeId='read'").fetchone()[0] == 2
        revisions = db.execute("SELECT revision,outcome FROM practice_records WHERE challengeId='read' ORDER BY revision").fetchall()
        assert revisions == [(1,"MISSED"),(2,"COMPLETED")], revisions
        latest = db.execute("SELECT outcome FROM practice_records WHERE challengeId=? AND localDate=? ORDER BY revision DESC LIMIT 1", ("read", "2026-11-12")).fetchone()
        assert latest == ("COMPLETED",)
        assert db.execute("SELECT COUNT(*) FROM pause_windows").fetchone()[0] == 1
        assert db.execute("SELECT body,mood,energy FROM journal_entries WHERE id='journal-1'").fetchone() == (
            "Quiet evening reflection", "calm", 4)
        # Unknown/unreported day has no fabricated practice row.
        assert db.execute("SELECT COUNT(*) FROM practice_records WHERE localDate='2026-11-13'").fetchone()[0] == 0

        # Unique revision index prevents accidental overwrite of the correction.
        try:
            db.execute("INSERT INTO practice_records (challengeId,localDate,outcome,revision,recordedAtEpochMs) VALUES (?,?,?,?,?)", (
                "read", "2026-11-12", "PARTIAL", 2, now + 3000))
            raise AssertionError("Duplicate practice revision was accepted")
        except sqlite3.IntegrityError:
            pass

        # Cascade challenge-only records, preserve (unlink) linked journal.
        db.execute("DELETE FROM challenges WHERE id='read'")
        assert db.execute("SELECT COUNT(*) FROM schedule_versions").fetchone()[0] == 0
        assert db.execute("SELECT COUNT(*) FROM practice_records").fetchone()[0] == 0
        assert db.execute("SELECT COUNT(*) FROM pause_windows").fetchone()[0] == 0
        journal_link = db.execute("SELECT challengeId,growthAreaId,body FROM journal_entries WHERE id='journal-1'").fetchone()
        assert journal_link == (None,"mind","Quiet evening reflection")
        db.execute("DELETE FROM growth_areas WHERE id='mind'")
        assert db.execute("SELECT growthAreaId FROM journal_entries WHERE id='journal-1'").fetchone()[0] is None

        db.execute("UPDATE journal_entries SET body='Edited privately',updatedAtEpochMs=? WHERE id='journal-1'", (now + 5000,))
        db.close()
        db = connect(filename)
        assert db.execute("SELECT body FROM journal_entries WHERE id='journal-1'").fetchone()[0] == "Edited privately"
        db.execute("DELETE FROM journal_entries WHERE id='journal-1'")
        assert db.execute("SELECT COUNT(*) FROM journal_entries").fetchone()[0] == 0
        assert db.execute("PRAGMA foreign_key_check").fetchall() == []
        assert db.execute("PRAGMA integrity_check").fetchone()[0] == "ok"
        db.close()

    print("EVOLUNE SQLite contract checks passed: restart, transactional setup, revisions, FK cascade, journal preservation, journal edits/delete.")


if __name__ == "__main__":
    run()
