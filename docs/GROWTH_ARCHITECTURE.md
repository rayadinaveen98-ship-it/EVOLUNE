# Growth architecture — v0.1

**Status:** Proposed product architecture for review (2026-11-11). Not implemented or approved feature scope.
**Purpose:** Explain how a self-development OS differs from a to-do list or habit tracker.

## Product mental model
**Growth area -> personally meaningful direction -> one action-based challenge -> daily practice -> reflection and adaptation -> continued growth.**

The user owns the goals, difficulty, schedule and records. The software provides structure, evidence-informed prompts and an understandable history without prescribing an identity or pace.

## Proposed top-level experiences
| Experience | Core question | What it should contain |
| --- | --- | --- |
| **Today** | What matters for me today? | Small set of due actions, one clear next step, optional reflection; no infinite feed |
| **My Growth** | Why am I doing this? | Self-selected growth areas, intentions, current practices and longer-term perspective |
| **Challenges** | What am I practicing? | Create, edit, pause, resume, archive; action planning; milestone reviews |
| **Journal** | What am I learning about myself? | Optional private freeform reflection, mood/energy labels, user-owned notes |
| **Insights** | What's actually changing? | Transparent adherence, perceived effort, changes over time, obstacles; descriptive, not diagnostic |
| **Library** | What can I try next? | Optional research-linked educational guides and examples; not a dump of 1,000 tasks |

These are **conceptual destinations**, not an approved navigation bar or screen count.

## Growth areas (starter examples, editable)
- **Body & energy:** gentle movement, hydration, rest rhythm, basic self-care.
- **Mind & awareness:** attention practice, journaling, stress-awareness exercises (wellness, not therapy).
- **Daily foundations:** sleep routines, planning, tidying, sustainable routines.
- **Learning & craft:** reading, creativity, study, skills.
- **Connection:** keeping in touch, active listening, intentional social time.
- **Confidence & courage:** small everyday behavioral experiments and personal commitments.

Users may rename, hide, combine or add areas. There is no 'must fix your whole life' questionnaire. No medical plans, intensive training programs, or weight-focused normative defaults.

## First-run experience (candidate)
1. Welcome: choose **start a challenge** or **explore first**; skip account and any sensitive questions.
2. Select one area or write their own.
3. State a personal reason in their own words (optional).
4. Choose one **repeatable action**, not a vague identity goal.
5. Choose realistic frequency/duration; optional if-then cue and a smaller backup action.
6. Show an honest preview of scheduled opportunities and first practice.
7. Enter Today with one obvious next step; all preferences editable later.

### Optional lightweight barrier reflection, inspired by COM-B
Never assign a psychological label or 'score.' Instead offer:
- **I don't know how to start** (capability) -> give a simple example or small tutorial.
- **My schedule/environment gets in the way** (opportunity) -> suggest a new cue/time or environment adjustment.
- **I don't feel motivated right now** (motivation) -> revisit why it matters or offer a gentler action.
- **None / something else** -> freeform or skip.

This is a design interpretation of a behavior model, **not a validated in-app assessment**.

## Challenge-to-growth relationship
A growth area can contain several challenges, but **one active focus** is the default onboarding suggestion to reduce overwhelm; users should be able to choose more later. A challenge is a finite *practice period* or a specific number of sessions. Completing it produces a review, not a claim of permanent habit formation.

## Distinctive experience principles
- **User choice:** meaningful defaults without compulsory programs.
- **Non-punitive recovery:** users can document missed days and resume or adapt with history preserved.
- **Truthful progress:** scheduled, completed, partial, skipped, intentionally rested and paused are not mixed.
- **Optional reflection:** journal and emotion inputs never block practice logging.
- **Respectful autonomy:** no guilt copy, 'broken streak' shame, forced rewards, or time-in-app optimization.
- **Visual growth:** nature/miniature-world metaphors reflect user-visible milestones, never manufacture achievements.

## Boundaries
This is not Backlot (creator/task workflow), not the martial arts training app, and not the comprehensive workout coaching app. Evolune owns the *self-development and behavior change experience*; detailed training content may be separate later. It is not clinical advice or treatment.

## Research/approval still needed
Primary user and initial growth-area set; exact first-run branching; whether to display optional journaling on Today; multi-challenge interaction; content licensing; privacy/data-storage strategy; art direction. Prototype before finalizing.
