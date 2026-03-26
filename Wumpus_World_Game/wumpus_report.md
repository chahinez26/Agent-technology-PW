# Wumpus World — Project Report

**Course:** Metaheuristics & Intelligent Systems  
**Language:** Python 3  
**Library:** Pygame  
**Grid size:** 4×4  

---

## 1. Overview

This project implements a classic **Wumpus World** simulation, a well-known AI problem from Russell & Norvig's *Artificial Intelligence: A Modern Approach*. The environment is a 4×4 grid containing hidden dangers (pits, a Wumpus monster) and a gold treasure. An autonomous logical agent navigates the grid, reasons about what it perceives, and tries to grab the gold and escape safely — all without a map.

The implementation includes:
- A fully autonomous AI agent with logical inference
- A real-time Pygame graphical interface visible to the spectator (full world revealed)
- Custom sprites with PNG image support and automatic fallback drawing
- Several logic bug fixes over the initial version

---

## 2. Environment

The world is randomly generated at each new game. The grid contains:

| Element | Count | Effect on agent |
|---------|-------|-----------------|
| Wumpus  | 1     | Kills the agent on contact |
| Pits    | 3     | Kill the agent on contact |
| Gold    | 1     | Must be grabbed and brought to (0,0) |
| Start   | (0,0) | Safe, entry and exit point |

**Perceptions** the agent receives in each cell:

- **Breeze** — one or more adjacent pits
- **Stench** — Wumpus is in an adjacent cell
- **Glitter** — gold is in this cell
- **Scream** — the Wumpus was killed (after a successful arrow shot)

The agent has **one arrow**, which it can shoot in a straight line to kill the Wumpus.

---

## 3. Agent Architecture

The agent follows a **deliberative architecture**: it maintains an internal knowledge base, performs logical inference, builds a plan, and executes it step by step.

### 3.1 Knowledge Base

The agent tracks:
- `visited` — set of explored cells
- `perceptions` — stored percepts for every visited cell
- `safe_cells` — cells proven safe (no pit, no Wumpus)
- `valid_wumpus` — set of cells where the Wumpus could still be

### 3.2 Wumpus Localisation

At each new cell, the agent rebuilds the `valid_wumpus` set from scratch using all stored stench observations:

```
For each visited cell with stench  → Wumpus must be in one of its neighbours
For each visited cell without stench → Wumpus is NOT in any of its neighbours
```

The intersection of all these constraints gives the current candidate set. This approach is sound and avoids the bug of a simple `intersection_update` that loses information from earlier observations.

### 3.3 Pit Inference (Model Checking)

The agent uses **model checking** over all possible pit configurations in the frontier (cells adjacent to visited ones). For each configuration:

1. It checks consistency with all breeze/no-breeze observations
2. It respects the constraint of at most 3 pits total
3. A frontier cell is declared **safe from pits** if it is pit-free in every valid model

A cell is added to `safe_cells` only if it is safe from both pits and the Wumpus.

### 3.4 Decision Logic

`decide_action()` follows this priority order:

1. **Grab** the gold if glitter is perceived
2. **Return to (0,0)** and climb out if gold is held
3. **Explore** the closest unvisited safe cell (BFS)
4. **Shoot** the Wumpus if its position is exactly known and the arrow is available
5. **Retreat** to (0,0) and climb out if no safe moves remain

### 3.5 Path Planning

Paths are computed using **BFS** restricted to `safe_cells`. The resulting sequence of coordinates is converted into a series of `TURN_LEFT`, `TURN_RIGHT`, and `FORWARD` actions by simulating the agent's orientation step by step.

---

## 4. Bugs Fixed

Several logical and implementation issues were corrected during development:

| # | Bug | Fix |
|---|-----|-----|
| 1 | `intersection_update` on Wumpus candidates lost previous stench data | Rebuild candidate set from all observations each time |
| 2 | Pit model-checking ignored adjacents that lie outside the frontier | Distinguish frontier cells from unknown-outside cells; a breeze is consistent if any unknown outside exists |
| 3 | 180° turns in `convert_path_to_actions` were done in one step | Replaced with two successive `TURN_RIGHT` calls |
| 4 | Shooting direction was simulated without including turn actions already in the plan | Added `_sim_dir_after()` to properly simulate final orientation |
| 5 | Agent at (0,0) wanting to retreat would never climb because `plan_path_to((0,0))` returns `[]` (falsy) | Check `current_pos() == (0,0)` before path planning and climb directly |

---

## 5. Graphical Interface

The interface is built with **Pygame** and divided into two areas:

### 5.1 Grid (left)

- **All elements are always visible** to the spectator (Wumpus, pits, gold), regardless of whether the agent has visited the cell
- Visited cells are shown in light gray; unvisited safe cells in pale green; unknown cells in dark gray
- The start cell (0,0) has a green border
- Percept labels (`~brise~`, `~puant~`) appear in visited cells
- `?W` marks cells where the agent still considers the Wumpus might be
- The agent sprite is drawn in its current cell; a small arrow icon indicates it still has its arrow
- When the agent dies, a game-over message is displayed as an overlay — no death sprite

### 5.2 Info Panel (right)

Displays in real time:
- Position, direction, score
- Arrow and gold status
- Number of safe cells known
- Number of Wumpus candidate cells
- Current agent status and last action message
- Upcoming planned actions (up to 6)

### 5.3 Sprites & Images

Images are loaded from PNG files placed in the same folder as the script. If a file is missing, a fallback sprite is drawn programmatically. Supported files:

```
agent.png   wumpus.png   gold.png   pit.png   arrow.png
```

---

## 6. Controls

| Key | Action |
|-----|--------|
| `SPACE` | Execute one step (manual mode) |
| `P` | Toggle auto-play / manual |
| `+` / `-` | Increase / decrease auto-play speed |
| `R` | Start a new game |

---

## 7. Scoring

| Event | Points |
|-------|--------|
| Each action taken | −1 |
| Arrow fired | −10 |
| Wumpus killed | +500 |
| Gold grabbed | +1000 |
| Climbing out with gold | +100 |
| Death (pit or Wumpus) | −1000 |

---

## 8. Limitations & Possible Extensions

- The agent cannot take risks — it will retreat rather than enter an uncertain cell, even if that means leaving the gold behind
- With bad random layouts (gold surrounded by pits), the agent may never reach the gold
- Possible extensions include: probabilistic reasoning to allow calculated risk-taking, a larger grid, multiple agents, or a manual play mode

---

## 9. File Structure

```
wumpus.py       ← main file (game, agent, world, rendering)
agent.png       ← agent sprite (optional)
wumpus.png      ← Wumpus sprite (optional)
gold.png        ← gold sprite (optional)
pit.png         ← pit sprite (optional)
arrow.png       ← arrow sprite (optional)
```

All game logic, AI inference, and rendering are contained in a single Python file for simplicity.
