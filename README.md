# Fallen Ascendants

**Fallen Ascendants** is an auto battle trading card game built with **Java** and **LibGDX** as a final project for the Data Structures course.

The game combines card collection, deck building, faction synergy, automatic turn-based combat, and data structure-based battle systems.

---

## Game Overview

In a fallen celestial world, divine factions fight for control after the collapse of the heavens.

Players build a deck of character cards, place them into battle formation, and watch the combat unfold automatically. Each card has its own faction, role, rarity, stats, skills, passive effects, and status interactions.

The battle system is fully automated. Players focus on preparing their deck and formation before the fight begins.

---

## Core Gameplay

1. Build a deck from available cards.
2. Enter battle with 8 cards.
3. Five cards are placed on the active field.
4. Three cards are stored as reserve.
5. Cards attack automatically based on turn priority.
6. When an active card is defeated, a reserve card replaces it.
7. The battle ends when one side has no remaining alive cards.

---

## Main Features

* Auto battle combat system
* Turn-based battle flow
* Priority Queue turn order based on SPD
* Aggro-based target selection
* Active skills
* Passive skills
* Death / reactive skills
* Status effects

    * Burn
    * Poison
    * Slow
    * Stun
* Shield system
* Reserve card replacement
* Graveyard system
* Faction synergy system
* Faction counter graph
* Graph traversal using DFS
* Random deck generation for testing
* Battle log system for debugging and demonstration

---

## Theme: Fallen Celestial

The game uses a dark fantasy celestial theme where fallen divine factions compete for survival and power.

### Factions

* Celestial Remnants
* Void Corrupted
* Abyssal Church
* Dragon Lineage
* Mortal Ascendants
* Shadow Conclave

Each faction has its own identity and counter relationship.

---

## Card System

Each card contains:

* Name
* Faction
* Role
* Rarity
* HP
* ATK
* DEF
* SPD
* Aggro
* Active skill
* Passive skill
* Death skill
* Status effects
* Shield value

### Roles

* Tank
* DPS
* Assassin
* Mage
* Support
* Healer

### Rarity

* Common
* Rare
* Epic
* Legendary

---

## Battle System

The battle system is handled automatically by the program.

### Turn Order

Turn order uses a **Priority Queue** based on card SPD.

Cards with higher SPD act earlier. If two cards have the same SPD, the card that entered the queue first acts first.

The system also prints the upcoming queue during battle so the turn order can be observed clearly.

Example:

```text
Upcoming Priority Queue:
1. Shadow Saint | SPD: 120 | Effective SPD: 120
2. Rift Duelist | SPD: 112 | Effective SPD: 112
3. Silent Blade | SPD: 100 | Effective SPD: 100
```

### Targeting

Basic attacks use an aggro-based targeting system. Cards with higher aggro are more likely to be targeted.

### Status Effects

Status effects are processed at the start of a card's turn.

* Burn deals fixed damage.
* Poison deals damage over time.
* Slow reduces effective SPD.
* Stun makes the card skip its turn.

---

## Faction Synergy

Faction synergy activates when multiple active cards from the same faction are on the field.

Only active field cards are counted. Reserve cards do not activate synergy.

Example:

```text
SHADOW_CONCLAVE synergy active: 2/5 cards, ATK +10%
```

---

## Faction Counter Graph

The faction counter system is implemented using a **directed graph**.

Each faction is a node, and each counter relationship is a directed edge.

### Counter Relations

| Attacker Faction   | Target Faction     | Counter Effect        |
| ------------------ | ------------------ | --------------------- |
| Celestial Remnants | Abyssal Church     | Heal lowest HP ally   |
| Abyssal Church     | Mortal Ascendants  | Poison target         |
| Mortal Ascendants  | Dragon Lineage     | Chance to stun target |
| Dragon Lineage     | Void Corrupted     | Burn target           |
| Void Corrupted     | Shadow Conclave    | Slow target           |
| Shadow Conclave    | Celestial Remnants | Bonus damage          |

Counter effects only trigger from basic attacks and only when HP damage is successfully dealt.

---

## Graph Traversal

The faction counter graph supports **Depth First Search traversal**.

Example traversal from `CELESTIAL_REMNANTS`:

```text
Faction Counter Graph DFS Traversal:
Start from: CELESTIAL_REMNANTS
- Visit CELESTIAL_REMNANTS
  Edge: CELESTIAL_REMNANTS -> ABYSSAL_CHURCH
- Visit ABYSSAL_CHURCH
  Edge: ABYSSAL_CHURCH -> MORTAL_ASCENDANTS
- Visit MORTAL_ASCENDANTS
  Edge: MORTAL_ASCENDANTS -> DRAGON_LINEAGE
- Visit DRAGON_LINEAGE
  Edge: DRAGON_LINEAGE -> VOID_CORRUPTED
- Visit VOID_CORRUPTED
  Edge: VOID_CORRUPTED -> SHADOW_CONCLAVE
- Visit SHADOW_CONCLAVE
  Edge: SHADOW_CONCLAVE -> CELESTIAL_REMNANTS
```

This proves that the faction relationship is modeled as a graph, not just as static conditional logic.

---

## Data Structure Implementation

| Data Structure     | Implementation                                    |
| ------------------ | ------------------------------------------------- |
| ADT                | Card, Skill, BattleField, Deck, Player            |
| Array / List       | Active field, reserve, graveyard, card collection |
| Priority Queue     | Turn order system based on SPD                    |
| HashMap            | Card database lookup                              |
| Graph              | Faction counter system                            |
| DFS Traversal      | Faction counter graph traversal                   |
| Linear Progression | Card level upgrade system                         |

---

## Project Structure

```text
FallenAscendants
├── core
│   └── src/main/java/com/fallenascendants
│       ├── battle
│       ├── debug
│       ├── enumtype
│       ├── model
│       └── screen
├── lwjgl3
│   └── Desktop launcher
└── assets
```

### Main Packages

| Package    | Description                                                                                                 |
| ---------- | ----------------------------------------------------------------------------------------------------------- |
| `battle`   | Battle manager, damage calculation, targeting, turn queue, skill resolver, status resolver, faction systems |
| `model`    | Core game objects such as Card, Skill, Deck, BattleField, and StatusEffect                                  |
| `enumtype` | Enum definitions for faction, role, rarity, skill type, target type, and status type                        |
| `debug`    | Battle tester for running combat simulation in console                                                      |
| `screen`   | LibGDX screens                                                                                              |

---

## Tech Stack

* Java 21
* LibGDX
* Gradle
* IntelliJ IDEA
* Git
* GitHub

---

## How to Run

### 1. Clone the Repository

```bash
git clone <repository-url>
cd FallenAscendants
```

### 2. Open the Project

Open the project using **IntelliJ IDEA**.

### 3. Run Desktop Launcher

Run the LWJGL3 launcher:

```text
lwjgl3/src/main/java/com/fallenascendants/lwjgl3/Lwjgl3Launcher.java
```

Or run using Gradle:

```bash
./gradlew lwjgl3:run
```

On Windows:

```bash
gradlew.bat lwjgl3:run
```

---

## Current Development Status

### Completed

* Card model
* Skill model
* BattleField system
* Random deck generation
* Auto battle system
* Priority Queue turn order
* Aggro targeting
* Damage calculation
* Shield system
* Status effect system
* Passive skill system
* Death skill system
* Reserve replacement
* Graveyard system
* Faction synergy
* Faction counter graph
* DFS graph traversal
* Battle log output

### Planned / Optional

* Collection screen
* Deck builder screen
* Reward selection screen
* Save and load progress
* Card level upgrade UI
* Full visual battle screen

---

## Team Notes

This project focuses on implementing game logic using data structures. The battle tester is currently used to validate the combat system, turn order, faction effects, status effects, and card replacement behavior before moving deeper into UI implementation.

---

## Documentation

* [Data Structure Documentation](docs/STRUCTURE_DATA.md)

---

## Project Purpose

This project was created as a Data Structures final project to demonstrate how abstract data types, arrays/lists, priority queues, hash maps, graphs, and graph traversal can be applied in a playable game system.
