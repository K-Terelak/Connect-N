# Connect‑N

A simple Connect‑N game implemented with Compose HTML

# Table of contents
- [Game description](#game-description)
- [Deployment](#deployment)
- [Project structure](#project-structure-tree)
- [Run](#run)
- [Tests](#tests)
- [Local state persistence](#local-state-persistence)
- [Game configuration and limits](#game-configuration-and-limits)
- [Debugging and useful info](#debugging-and-useful-info)

## Game description

This project implements the classic Connect‑N game (connect N in a row) with a UI built using Compose for Web. Two players alternate placing pieces into columns. The goal is to align the required number of pieces horizontally, vertically or diagonally.

## Deployment

The project is deployed on GitHub Pages:

https://k-terelak.github.io/Connect-N/
## Project structure (tree)

Important source files and folders (paths relative to repository root):
```
- src/main/kotlin
  - main.kt — application entry, creates GameViewModel and renders composables
  - local/
    - LocalGameStorageImpl.kt — implementation of browser LocalStorage persistence
  - model/
    - GameConfig.kt — default values and limits for game configuration
    - GameState.kt — serializable game state (board, score, current player...)
    - BoardCell.kt, Cell.kt, Player.kt — helper models
  - ui/
    - GameLogic.kt — game logic
    - GameViewModel.kt — view model managing state and persistence
    - composables/ — UI components (ConfigPanel, GameBoard, ScoreBar, StatusBar, DropArrows, etc.)

- src/test/kotlin
  - ui/GameLogicTest.kt — unit tests for game logic
  - ui/GameViewModelTest.kt — tests for `GameViewModel` (no browser required)
```
## Run

### Run application in a browser:

```bash
./gradlew jsBrowserDevelopmentRun
```

After starting, open the address shown in the logs (usually http://localhost:8080).

### Build bundle (webpack):

```bash
./gradlew jsBrowserProductionWebpack
```

The output will be placed under `build/dist` (or as indicated by Gradle output).

## Tests

Run Kotlin/JS unit tests:

```bash
./gradlew jsTest
```

## Local state persistence

Game state is persisted to the browser LocalStorage. Implementation is in `src/main/kotlin/local/LocalGameStorageImpl.kt` and it uses the key:

```
connect-n
```

Because of this:
- refreshing the page restores the last saved game state
- changing the configuration creates a new game state and overwrites the saved state

## Game configuration and limits

Default values and limits taken from `model/GameConfig.kt`:

- Defaults:
    - columns (`cols`): 7
    - rows (`rows`): 6
    - connect / winCondition (`winCondition`): 4

- UI limits:
    - `rowsMin`: 6
    - `rowsMax`: 15
    - `colsMin`: 6
    - `colsMax`: 15
    - `winConditionMin`: 3
    - `winConditionMax`: 6

You can change these values in the configuration panel in the UI (`ConfigPanel`) — changing them resets the board with the new configuration.

## Debugging and useful info

- Persistence: `LocalGameStorageImpl` uses `kotlinx.serialization.Json` and catches errors during save/load.
- Drop animation: animation duration depends on the row number (`animDuration` in `GameViewModel`) and the move commit is delayed by animation duration + 40ms.
- Templates and styles: `src/main/resources/index.html`, `src/main/resources/styles.css` (adjust for custom styling).

