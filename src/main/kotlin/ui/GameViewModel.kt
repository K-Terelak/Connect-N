package ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import local.LocalGameStorage
import local.LocalGameStorageImpl
import model.GameConfig
import model.GameState

class GameViewModel(
    private val storage: LocalGameStorage = LocalGameStorageImpl(),
    private val gameLogic: GameLogic = GameLogicImpl(),
    private val schedule: (delayMs: Int, block: () -> Unit) -> Unit = { delay, block ->
        window.setTimeout({ block() }, delay)
    },
) {

    var gameState by mutableStateOf(storage.load() ?: GameState())
        private set

    var animatingCol by mutableStateOf<Int?>(null)
        private set

    var animatingRow by mutableStateOf<Int?>(null)
        private set

    fun dropPiece(col: Int) {
        if (!canDrop()) return
        val targetRow = gameLogic.getDropRow(gameState.board, col, gameState.config.rows) ?: return
        startAnimation(col, targetRow)
        scheduleCommit(col, animDuration(targetRow))
    }

    fun newGame() {
        gameState = GameState(
            config = gameState.config,
            score = gameState.score,
            draws = gameState.draws
        )
        clearAnimation()
        storage.save(gameState)
    }

    fun updateConfig(config: GameConfig) {
        gameState = GameState(config = config)
        clearAnimation()
        storage.save(gameState)
    }

    private fun canDrop() = gameState.state == GameState.State.InProgress && animatingCol == null

    private fun startAnimation(col: Int, row: Int) {
        animatingCol = col
        animatingRow = row
    }

    private fun clearAnimation() {
        animatingCol = null
        animatingRow = null
    }

    private fun animDuration(targetRow: Int) = (60 * (targetRow + 1)).coerceIn(280, 550)

    private fun scheduleCommit(col: Int, duration: Int) {
        schedule(duration + 40) {
            gameState = gameLogic.dropPiece(gameState, col)
            clearAnimation()
            storage.save(gameState)
        }
    }
}
