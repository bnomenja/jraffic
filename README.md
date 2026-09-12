# Jraffic

Simulation of an intersection between two perpendicular roads (North/South/
East/West), each road having one incoming lane and one outgoing lane per
direction, with traffic lights and vehicles that can go straight, turn left,
or turn right.

This document describes:
1. how to run the project,
2. how everything that's been done so far fits together,
3. what's left to do relative to the subject,
4. how to split that remaining work between two people.

---

## 1. Running the project

The project uses JavaFX (SDK 21). To run th eproject you need to download it
yourself because it depends on the OS and achitecture. There is a tuto for it
in the intra. Just follow it or ask chatGPT I don't know. but don't use Maven
or graddle or any frameworks, I don't want to explain that in the audit... 

Keyboard controls once the window is open:

| Key     | Effect                                              |
|---------|-----------------------------------------------------|
| `↑`     | spawns a vehicle coming from the **South**           |
| `↓`     | spawns a vehicle coming from the **North**           |
| `→`     | spawns a vehicle coming from the **West**            |
| `←`     | spawns a vehicle coming from the **East**            |
| `r`     | spawns a vehicle coming from a random direction      |
| `Esc`   | exits the simulation                                 |

---

## 2. Current architecture

The code is organized into 5 packages, with a simulation loop that runs
independently from the graphical rendering.

```
Main
 ├─ Simulation          (state + logic, no JavaFX dependency except Config)
 ├─ SimulationRenderer   (draws whatever Simulation holds)
 └─ KeyPressedHandler    (translates key presses into calls to Simulation)

AnimationLoop (60 fps) --> simulation.update(now) then renderer.render()
```

### 2.1 `model` — the data

- **`Direction`** (`NORTH, SOUTH, EAST, WEST`): represents both the **side
  of the board** a car comes from (a `NORTH` car spawns at the top of the
  screen) and, by reusing the same enum, the key used to store `outLanes`.
  `turnRight()` / `turnLeft()` / `opposite()` provide the rotations needed
  for trajectory calculations.

- **`Turn`** (`STRAIGHT, LEFT, RIGHT`): converts an origin direction into an
  exit direction via `exitDirection(origin)`.

- **`Lane`**: a simple container of `Car` (`List<Car>`). `isFull()` always
  returns `false` for now — **this is a point to implement** (see §3).

- **`Car`**: position `(x, y)`, fixed speed (`Config.CAR_SPEED`), and all the
  movement geometry:
  - `computeSpawnPoint(origin)`: spawn point based on direction.
  - `computeTurnPoint(origin, exitDirection)`: the point (at the edge of the
    intersection) where the car changes axis if it's turning. This point is
    `null` for a `STRAIGHT` trip (no axis change needed).
  - `move(dt)`: advances the car; if it has reached its `turnPoint`, it
    switches to the axis of its `exitDirection`.
- **Same-lane safety distance**: `Lane.moveCars` advances vehicles from
  front to back and prevents a following vehicle from getting closer than
  `CAR_LENGTH + SAFETY_GAP`. For incoming lanes, the lead car is also capped
  before the stop line whenever its signal is red; queued cars inherit that
  stop through the same safety-distance rule.

### 2.2 `simulation` — the orchestration

- **`CarSpawner`**: creates a `Car` with a random turn (`Turn.random`) and
  adds it to the corresponding `inLane`. A *time-based* cooldown
  (`Config.SPAWN_COOLDOWN_MS`) prevents keyboard spam — but this is **not**
  a real safety-distance concept (see §3).

- **`Intersection`** *(new)*: handles only the **geometric transfer** of
  cars between lanes, with no collision or light logic. A car belongs to
  only one collection at a time:

  ```
  inLanes[origin]  --(hasLeftInLane)-->  Intersection.carsInside  --(isReoriented)-->  outLanes[exitDirection]
  ```

  - `hasLeftInLane(car)`: true when the car's position crosses the edge of
    the intersection zone closest to its `origin`.
  - `isReoriented(car)`: true when, once oriented towards its
    `exitDirection`, it crosses the opposite edge of the zone.
  - The intersection zone is a simple `ROAD_WIDTH × ROAD_WIDTH` square
    centered on the screen (`Config.ROAD_WIDTH`, `Config.WINDOW_WIDTH/HEIGHT`).
  - `getCarsInside()` exposes the cars currently in the intersection: this
    is the natural entry point for wiring in a future collision-checking
    logic (see §3 and §4).

- **`Simulation`**: holds the `EnumMap<Direction, Lane>` `inLanes`/
  `outLanes`, the `CarSpawner`, the `LightController`, and the
  `Intersection`. `update(now)` computes the elapsed `dt` then:
  1. moves lane cars with same-lane clearance and intersection cars via
     `Car.move(dt)`,
  2. applies the lane transfers (`Intersection.transferCars()`),
  3. removes from `outLanes` any car that has exited the field (outside the
     window bounds) — **this is the final removal of vehicles**, there's
     nowhere else they disappear.

### 2.3 `traffic` — the lights (minimal state, no real logic yet)

- **`LightColor`** (`RED, GREEN`).
- **`TrafficLight`**: one color per `Direction`.
- **`LightController`**: at this stage, **randomly** changes each light's
  color every `CHANGE_INTERVAL_SECONDS` (2s), independently of each other
  and independently of any real traffic. No link to `Lane`, no notion of
  capacity. **This is a stub to be replaced** (§3).

### 2.4 `render` — the display (JavaFX)

- **`RoadRenderer`**: draws the roads, central square, and four stop lines.
- **`LightsRenderer`**: one circle per direction, colored according to
  `LightController.getColor(dir)`. Computed positions, no assets.
- **`CarRenderer`**: syncs one JavaFX `Rectangle` per `Car` (created/moved/
  removed based on the ids seen in `inLanes` + `outLanes` +
  `Intersection.carsInside`). Color based on the turn (`STRAIGHT` = blue,
  `LEFT` = orange, `RIGHT` = green).
- **`SimulationRenderer`** / **`AnimationLoop`**: tie everything together,
  running on every frame (`AnimationTimer`).

### 2.5 `input` / `config`

- **`KeyPressedHandler`**: translates the arrow keys and `r`/`Esc` into
  calls to `CarSpawner`.
- **`Config`**: all the constants (window, road, lights, cooldown, speed).
  `CAR_LENGTH` and `SAFETY_GAP` define the same-lane clearance. Capacity
  constants and collision-related values remain to be added.

---

## 3. Where we stand relative to the subject

| Subject requirement                                         | Status                                   |
|-------------------------------------------------------------|------------------------------------------|
| Two intersecting roads, 1 lane per direction                | ✅ done (`RoadRenderer`, `Lane`)          |
| Straight / left / right, fixed trajectory                   | ✅ done (`Turn`, `Car`)                   |
| Keyboard spawn (arrows, `r`, `Esc`), anti-spam               | ✅ done (`KeyPressedHandler`, *time-based* cooldown) |
| Vehicle color based on route                                 | ✅ done (`CarRenderer.colorForTurn`)      |
| Fixed velocity per vehicle                                   | ✅ done (`Config.CAR_SPEED`)              |
| 2-color lights, positioned at the entry of each lane          | ✅ display done, ⚠️ dummy logic (random)  |
| **Safety distance between vehicles in the same lane**        | ✅ done (`Lane.moveCars`)                 |
| **Stop line + real stopping at a red light**                 | ✅ done (`Lane`, `Simulation`, `RoadRenderer`) |
| **No collisions inside the intersection**                    | ❌ not done (geometric transfer only, no conflict check) |
| **Lights avoiding collisions + adapting to congestion**      | ❌ not done (currently random, no link to lanes) |
| **Dynamic capacity formula `capacity = floor(lane_length/(vehicle_length+safety_gap))`** | ❌ not done (`Lane.isFull()` always returns `false`) |
| Polished UI / animations / assets (bonus)                    | ❌ not done (raw rectangles + circles)    |

In short: **the skeleton (movement, lane transfer, spawning, basic
rendering) is in place**. What's missing is everything that makes the
simulation actually *safe* and *smart*: safety distances, a stop line,
anti-collision inside the intersection, and a real congestion-aware light
logic.

---

## 4. Splitting the remaining work 

The proposed split separates **vehicle behavior** (Person A) from **light
decisions + interface** (Person B), because these are two fairly
independent algorithms that communicate through a simple interface:
*Person A needs to know whether their direction's light is green or red;
Person B needs to know how many vehicles are waiting in each lane.* As long
as this contract is respected, both people can move forward in parallel.

### 🧑‍💻 Badr — Vehicles, safety, stop line

Goal: no car should ever hit another one, or even visually overlap another
one, whether in a lane or in the intersection.

1. **Intra-lane safety distance**
   - In `Lane` (or in `Simulation.syncLane`), before calling `car.move(dt)`,
     compare each car to the one ahead of it on the same axis (the lane is
     ordered by spawn position, so "ahead" = the car furthest along the
     movement axis).
   - If the gap becomes smaller than `vehicle_length + safety_gap`, the car
     must not advance (or advances at the speed of the car in front,
     depending on how fine-grained you want it).
   - You'll need to add some state like `boolean stopped` or
     `double speedFactor` on `Car`, plus a public method so external logic
     (lane, stop line, light) can trigger it without `Car` needing to know
     about `Lane`/`LightController`.

2. **Stop line**
   - Define a fixed position per direction (just before the edge of the
     intersection zone handled by `Intersection`, so likely at
     `centerX/centerY ± ROAD_WIDTH/2` along the relevant axis — consistent
     with the thresholds already used in `Intersection.hasLeftInLane`).
   - A car approaching this line must stop **if its direction's light is
     red**, and resume as soon as it turns green (and the way ahead is
     clear, cf. point 1).
   - This needs to read the light's state:
     `LightController.getColor(direction)` (already exposed by Person B, no
     API change needed).

3. **Anti-collision inside the intersection**
   - As discussed, with at most 2 to 4 cars at once in
     `Intersection.carsInside`, a pairwise geometric check is enough:
     compare `(x, y)` positions (optionally projected `dt` ahead) and
     block/slow down a car if it gets too close to another one whose
     trajectory crosses its own.
   - This remains a safety net independent of the lights (useful in case
     Person B's light logic ever mistakenly allows two conflicting
     directions at the same time).

4. **Dynamic lane capacity**
   - Implement `Lane.isFull()` using the subject's formula:
     `capacity = floor(lane_length / (vehicle_length + safety_gap))`.
   - `lane_length` = distance between the spawn point and the stop line
     (computable from `Config`).
   - `vehicle_length` and `safety_gap`: new constants to add to `Config`.
   - `CarSpawner.trySpawn` must refuse to spawn if `lane.isFull()` — this
     replaces/complements the current time-based cooldown, which is still
     useful to prevent keyboard spam but doesn't guarantee a real safety
     distance between two closely-spawned vehicles.

**Files involved**: `model/Car.java`, `model/Lane.java`,
`simulation/Intersection.java`, `simulation/CarSpawner.java`,
`simulation/Simulation.java`, `config/Config.java`.

### 🧑‍💻 Amine — Traffic lights, congestion, interface

Goal: replace the current random logic with a real one that avoids
conflicts between directions and absorbs congestion, then polish the
rendering.

1. **Real light logic**
   - Replace the random draw in `LightController.update()` with logic that
     guarantees that at most **non-conflicting** directions are green at
     the same time (e.g. North/South green together while East/West are
     red, then the reverse — a simple two-phase cycle is enough to satisfy
     the subject, a system based on pairs of opposite directions).
   - This choice of phases depends on the possible trajectories (straight,
     left, right): make sure no green combination lets two trajectories
     cross without a red light preventing it.

2. **Congestion adaptation**
   - `LightController` needs to read each `Lane`'s occupancy
     (`lane.getCars().size()`, or better, a count of cars waiting at the
     stop line once Person A has introduced it).
   - Use the same capacity formula as Person A (`Config`, to be coordinated
     together to avoid duplicating constants): if a lane approaches its
     capacity, extend its green time (or shorten the others') rather than
     following a fixed timer.
   - The subject explicitly requires congestion to **stay below capacity**:
     this should be verifiable in practice by spamming `r` and checking
     that no lane overflows.

3. **Interface / rendering (bonus but expected for the final grade)**
   - Replace the raw rectangles/circles with real sprites (see the asset
     links in the subject) in `CarRenderer` / `LightsRenderer`.
   - Add information useful for the audit: number of cars waiting per
     direction, clearly displayed light color, possibly a timer before the
     next change.
   - Optional: small animations (light color transition, wheel rotation,
     etc.) once the logic is stable — don't start with this.

**Files involved**: `traffic/LightController.java`,
`traffic/TrafficLight.java`, `render/LightsRenderer.java`,
`render/CarRenderer.java`, `render/RoadRenderer.java`, `config/Config.java`.

### Coordination points between A and B

- **`Config`**: both people will add constants there (`VEHICLE_LENGTH`,
  `SAFETY_GAP`, light phase durations...) — split this to avoid unnecessary
  Git conflicts.
- **API contract not to break**:
  - `LightController.getColor(Direction)` remains the only entry point
    Person A should use to know a light's state.
  - `Lane.getCars()` / `Lane.isFull()` remain the entry points Person B
    should use to know a lane's occupancy.
- Work on separate branches and resync regularly, since the stop line (A)
  and the light logic (B) ultimately need to produce a consistent visual
  result together.
