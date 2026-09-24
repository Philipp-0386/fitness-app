# Frontend Planning

This document is for exploring the visual design of the app. Nothing in it is decided. It collects what the app has to handle, where a distinct look could come from, and a few directions to try out before picking one.

The only anchor so far: the current coloring works (dark base, purple accent, blue as a second color). Every direction below keeps a dark base with purple. How the purple is tuned, and whether blue stays, can vary.

---

## Why the current frontend feels generic

The current pages are solid, but they use the patterns almost every generated or template landing page uses:

- Centered hero with a small all-caps label above the headline and one word in gradient text.
- A grid of identical rounded cards, each with icon, title and one line of text (feature cards on the guest home, shortcuts on the user home).
- Stat tiles with a big number and a small label.
- Every card and button lifts on hover (`translateY(-2px)` plus shadow).
- A blue to purple gradient as decoration on buttons and text.
- Geist, the font every new Next.js project starts with.

None of this is wrong, but none of it says "fitness" or "this app". The goal of the exploration is to find the one or two things that make the app recognizable and keep everything else quiet.

Unrelated to the look, there are also inconsistencies to clean up anyway: the sign-up form is a light card with hard-coded colors, the tokens in `globals.css` are not used everywhere, and the modules use five different border radii.

## What any direction has to handle

These come from how the app is used, not from taste. They are the test for every direction.

- **Logging in the gym.** One hand, phone, short attention between sets, bright or dim light. The current set, the numbers and the rest timer must be readable at arm's length and tappable with a thumb.
- **Numbers are the main content.** Weight, reps, RPE, rest time, volume. Whatever the style, numbers need tabular figures and a size hierarchy that makes the important one obvious.
- **Two moods.** Logging is fast and focused. Reviewing progress (records, volume per muscle group, history) is calm and can be denser. The design can treat these differently.
- **Charts.** Progression and volume views need a chart palette that works on dark and fits the accent.
- **Accessibility floor.** Contrast (WCAG AA), visible focus, labels, reduced motion, no information by color alone.

## Where a distinct look can come from

The strongest source is the world of training itself. Things that are characteristic of it:

- **The gym wall clock.** The red LED interval timer on the wall of almost every gym and box. Big segmented digits, the unlit segments faintly visible.
- **Competition scoreboards.** Powerlifting meet displays: name, attempt, weight, three lights for the judges' decision.
- **The training logbook.** Many lifters keep a paper notebook: date, exercise, sets as short lines like `100 x 5`, crossed out when done, notes in the margin.
- **Plates and the barbell.** Plates stacked on a bar, color coded by weight in competition. Loading the bar is a small math problem every set.
- **Chalk, rubber floor, tape markings.** Matte black rubber, white chalk marks, colored tape on the floor for lanes and zones.

## Directions

Three directions, deliberately different from each other. They can also be mixed, for example the structure of one with the numbers of another. For each: the idea, a rough palette, type candidates, a sketch of the workout logging screen (the most characteristic view), and the one element that carries the identity.

### A: Scoreboard

The numbers are displayed like a gym clock or meet scoreboard. Purple takes the place of the red LED. The rest of the interface is very plain so the numbers can be loud.

| Role | Value |
| --- | --- |
| Background | `#0E0C14` (black with a slight purple tint) |
| Panel | `#1A1724` |
| Unlit segment | `#2A2536` |
| Lit / accent | `#B26BFF` |
| Text | `#ECE8F5` |

Type candidates: a condensed display face for numbers (Big Shoulders Display, Oxanium) and a plain sans for UI text (Barlow).

```
+----------------------------------+
| Bench press            set 3 / 5 |
|                                  |
|        82.5  kg                  |
|         x 5                      |
|                                  |
|   [ - ]   [ log set ]   [ + ]    |
|                                  |
|  rest   01:24  ================  |
+----------------------------------+
```

Identity: the digits, with unlit segments visible behind them. Used only for weight, reps and timers, never for body text.

Fit: very good for logging and glanceability. Risk: turns into a gimmick if the display style spreads to headings and labels. Less natural for the calm analysis views.

### B: Logbook

The app as a dark training notebook. Sessions are pages, sets are ruled lines, done sets get struck through in purple ink. Structure comes from lines and rows, not from cards.

| Role | Value |
| --- | --- |
| Background | `#16141B` |
| Page | `#1F1C26` |
| Ruled line | `#2E2A38` |
| Ink (text) | `#E6E1D8` (slightly warm, like chalk) |
| Purple ink | `#9D6BF2` |
| Muted | `#8E889A` |

Type candidates: a sturdy text serif for headings and exercise names (Literata, Source Serif 4) and a sans with good tabular figures for the sets (Public Sans, IBM Plex Sans).

```
+----------------------------------+
| Tue 24 Sep          Push day     |
|----------------------------------|
| Bench press                      |
|   1   80 x 5    ~~done~~         |
|   2   80 x 5    ~~done~~         |
|   3   82.5 x 5  <               |
|   4   __ x __                    |
|----------------------------------|
| Overhead press                   |
|   1   __ x __                    |
+----------------------------------+
```

Identity: the ruled page and the purple strike-through for completed sets.

Fit: very good for history, notes and progression, and it matches how lifters already think about their log. Risk: can feel too quiet or slow for the logging moment. Needs care so the rows stay large enough to tap.

### C: Gym floor

Material and equipment: matte rubber surfaces, chalk-white text, purple as the one painted element, like tape on the floor. The barbell becomes a visual element.

| Role | Value |
| --- | --- |
| Rubber | `#141416` (possibly with a very faint speckle texture) |
| Surface | `#1D1D21` |
| Chalk | `#F1EFEA` |
| Tape / accent | `#8338EC` (the current purple) |
| Steel | `#9AA0A8` |

Type candidates: one family with a width axis, wide and heavy for headings, normal for body (Archivo, Archivo Expanded).

```
+----------------------------------+
| SQUAT                    set 2   |
|                                  |
|  |[][]|=========|[][]|           |
|   20 15  bar 20  15 20   = 100   |
|                                  |
|  100 kg   x 5                    |
|  [ log set ]                     |
|                                  |
|##### purple tape line ###########|
|  next: 100 x 5, rest 2:00        |
+----------------------------------+
```

Identity: the loaded barbell showing which plates go on each side for the current weight. This is useful (plate math), not just decoration.

Fit: strong character and a feature that helps during training. Risk: plate diagrams only make sense for barbell exercises. Competition plate colors (red, blue, yellow, green) compete with purple, so plates would need to stay neutral or be shown in purple shades.

## Signature element candidates

Independent of the direction, ideas for the one memorable thing:

- Rest timer as a full-width element that drains or fills, readable from a distance.
- The personal record moment: the one place with a deliberate animation.
- Plate loading diagram (direction C, but works as a detail in any direction).
- Muscle map on the progress view, with trained muscle groups shaded in purple by weekly volume.
- Strike-through for completed sets (direction B).

Only one or two of these should be loud. The rest of the interface stays calm.

## How to explore

1. Pick two directions (or a mix) that are worth trying.
2. Build the same two screens for each: the workout logging view and the user home. The logging view shows whether a direction works in the gym. The home shows how it handles overview and navigation.
3. Look at them on a phone, ideally in the gym, and compare against the requirements above.
4. Only then define the token set in `globals.css` (colors, type scale, spacing, radii) and move the existing pages over.

Mockups can live outside the app (static HTML pages) so the real frontend stays untouched until a direction is chosen.

## Open questions

- Blue: keep as a second color, or reduce to purple plus neutrals?
- Should logging and analysis look noticeably different (e.g. scoreboard numbers while training, logbook structure for history)?
- Navigation pattern on the phone: bottom tab bar, or something built around the active session?
- Offline during a session: queue sets locally, or block input? This affects how the save state is shown.
- PWA (home screen icon, full screen)?
- Units: kg only, or kg and lbs? This matters for the plate diagram.
