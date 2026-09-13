# Jraffic

A JavaFX simulation of a four-way road intersection controlled by an adaptive traffic-light system. Cars are spawned by the user, pick a random route (straight / left / right) at spawn time, and are driven through the junction by a simple congestion-aware light controller that keeps every lane below its maximum capacity.

## Features

- Two intersecting two-way roads (N/S and E/W), one lane per direction.
- Traffic lights (red/green only) at each of the four entry points.
- Adaptive light controller:
  - Picks the next direction to serve based on waiting cars and how long a direction has been waiting (`CongestionStrategy`), with a simpler `RoundRobinStrategy` also available.
  - Extends the green phase automatically when a lane is at full capacity, so no lane ever overflows.
  - Uses a short all-red phase between direction changes to guarantee no two conflicting flows are ever green at once.
- Cars:
  - Spawned via keyboard, each with a fixed speed and a randomly assigned route.
  - Color-coded by the turn they will make.
  - Keep a safety gap from the vehicle ahead and stop at a red light / stopped car.
  - Cannot be spammed into existence: a new car only spawns if there's enough room behind the last one in that lane.
- Dynamic lane capacity computed from lane length, car length and safety gap (`capacity = floor(lane_length / (car_length + safety_gap))`).

## Vehicle colors

Each car's color reflects the route it was randomly assigned at spawn (route can't change afterward):

| Route          | Color  | Asset               |
|----------------|--------|----------------------|
| Straight       | Green  | `assets/green_car.png`  |
| Left turn      | Red    | `assets/red_car.png`    |
| Right turn     | Yellow | `assets/yellow_car.png` |

## Controls

| Key       | Action                                             |
|-----------|-----------------------------------------------------|
| `↑`       | Spawn a car coming from the **south**, random route |
| `↓`       | Spawn a car coming from the **north**, random route |
| `→`       | Spawn a car coming from the **west**, random route  |
| `←`       | Spawn a car coming from the **east**, random route  |
| `R`       | Spawn a car from a **random** direction, random route |
| `C`       | Remove all cars from the simulation                 |
| `Esc`     | End the simulation                                  |

## Requirements

- JDK 17 or newer (uses Java `switch` expressions and pattern-style `switch`).
- The [`javafx.sh`](javafx.sh) script handles downloading JavaFX 21.0.12 for Linux x64 — no manual JavaFX install needed on a compatible system.

## Getting started

```bash
# 1. Download and extract the JavaFX SDK into the project folder
./javafx.sh install

# 2. Compile and run the simulation
./javafx.sh run
```

`javafx.sh install` downloads `javafx-sdk-21.0.12` (Linux x64) from the official Gluon distribution and extracts it into the current directory (skipped if it's already there). `javafx.sh run` compiles every `.java` file into `out/` and launches `Main` with the JavaFX `javafx.controls` module on the module path.

If you're on a different OS/architecture, download the matching [JavaFX SDK](https://gluonhq.com/products/javafx/) yourself, extract it, and adjust `PATH_TO_FX` in `javafx.sh` (or run the two commands from `javafx.sh run` manually) to point at your `lib` folder.

## Project structure

```
Main.java                     Application entry point (JavaFX bootstrap)
config/
  Config.java                 All tunable constants (window size, road width, car speed, timings, colors...)
input/
  KeyPressedHandler.java      Maps key presses to spawn requests / exit
model/
  Direction.java              NORTH / SOUTH / EAST / WEST + rotation helpers
  Turn.java                   STRAIGHT / LEFT / RIGHT + exit direction resolution
  Car.java                    Car position, movement and turning logic
  Lane.java                   A queue of cars on one approach, spacing and capacity logic
simulation/
  CarSpawner.java             Spawns a car in a lane if there's room
  Intersection.java           Moves cars from "in" lanes into the intersection, then to "out" lanes
  Simulation.java             Ties lanes, spawner, light controller and intersection together each frame
traffic/
  TrafficLight.java           State of a single light (direction + color)
  LightColor.java             RED / GREEN
  LightPhase.java             GREEN / ALL_RED
  LightController.java        Drives the green/all-red cycle and congestion-based green extension
  TrafficControlStrategy.java Strategy interface for picking the next direction to serve
  CongestionStrategy.java     Default strategy: prioritizes longest-waiting, then busiest, direction
  RoundRobinStrategy.java     Alternative strategy: simple fixed-order round robin
  LightObserver.java          Callback interface for light color changes
render/
  SimulationRenderer.java     Builds the JavaFX scene graph
  RoadRenderer.java           Draws the roads and lane markings
  LightsRenderer.java         Draws and updates the traffic light housings
  CarRenderer.java            Creates/updates/removes car sprites each frame
  TimeRenderer.java           On-screen elapsed time / debug display
  AnimationLoop.java          JavaFX AnimationTimer driving the simulation update + render loop
assets/                       Car and light sprites
javafx.sh                     Helper script: install and run
```

## How the traffic light logic avoids congestion

- Each lane's capacity is derived from its physical length and the car length + safety gap (see `Lane.getCapacity()`), so it directly reflects the **Dynamic Congestion Rule** from the spec.
- `LightController` normally grants each direction a fixed green duration (`NORMAL_GREEN_DURATION_SECONDS`), then switches to a brief all-red phase before choosing the next direction.
- If the currently green lane is full (`Lane.isFull()`), its green phase is extended to `FULL_LANE_GREEN_DURATION_SECONDS` instead of ending on schedule, giving it more time to drain before congestion can exceed capacity.
- The default strategy (`CongestionStrategy`) chooses the next direction to serve by first checking for any direction that has been waiting an unusually long time, then falling back to the direction with the most waiting cars — this prevents starvation while still favoring busier approaches.
- The all-red phase between every green phase guarantees that two conflicting streams of traffic are never green at the same time, preventing collisions in the intersection.

## Notes

- No emergency/special vehicles: every car follows the same rules.
- Once a car is spawned, its route (and therefore its color) is fixed for its whole trip.