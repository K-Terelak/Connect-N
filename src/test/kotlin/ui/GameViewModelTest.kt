package ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import local.LocalGameStorageImpl
import model.BoardCell
import model.GameConfig
import model.GameState
import model.Player

class GameViewModelTest {

    @Test
    fun `should reset board and keep score and draws after newGame`() {
        val config = GameConfig(rows = 4, cols = 4, winCondition = 3)
        val vm = GameViewModel(storage = LocalGameStorageImpl())

        vm.updateConfig(config)

        vm.newGame()

        val state = vm.gameState
        assertEquals(config, state.config)
        assertEquals(GameState.State.InProgress, state.state)
        assertTrue(state.board.flatten().all { it == BoardCell.EMPTY })
        assertNull(vm.animatingCol)
        assertNull(vm.animatingRow)
    }

    @Test
    fun `should reset state and score when updateConfig called`() {
        val vm = GameViewModel(storage = LocalGameStorageImpl())
        val newConfig = GameConfig(rows = 8, cols = 8, winCondition = 4)

        vm.updateConfig(newConfig)

        val state = vm.gameState
        assertEquals(newConfig, state.config)
        assertEquals(0, state.draws)
        assertEquals(0, state.score[Player.ONE])
        assertEquals(GameState.State.InProgress, state.state)
        assertTrue(state.board.flatten().all { it == BoardCell.EMPTY })
        assertNull(vm.animatingCol)
        assertNull(vm.animatingRow)
    }

    @Test
    fun `should start animation when dropPiece called`() {
        val config = GameConfig(rows = 7, cols = 7, winCondition = 4)
        val vm = GameViewModel(storage = LocalGameStorageImpl())
        vm.updateConfig(config)

        vm.dropPiece(0)

        assertEquals(0, vm.animatingCol)
        assertEquals(6, vm.animatingRow)
    }
}
