package ui

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import model.BoardCell
import model.Cell
import model.GameConfig
import model.GameState
import model.Player

class GameLogicTest {

    private lateinit var logic: GameLogicImpl

    @BeforeTest
    fun setUp() {
        logic = GameLogicImpl()
    }

    @Test
    fun `should return bottom-most empty row when getDropRow called`() {
        val config = GameConfig(rows = 4, cols = 4)
        val state = GameState(config = config)

        val row = logic.getDropRow(state.board, 0, config.rows)

        assertEquals(3, row)
    }

    @Test
    fun `should return null when column is full`() {
        val config = GameConfig(rows = 3, cols = 3)
        val board = boardOf(config.rows, config.cols) { _, c ->
            if (c == 0) BoardCell.P1 else BoardCell.EMPTY
        }

        val row = logic.getDropRow(board, 0, config.rows)

        assertEquals(null, row)
    }

    @Test
    fun `should place piece and switch player after dropPiece`() {
        val config = GameConfig(rows = 4, cols = 4, winCondition = 3)
        val state = GameState(config = config)

        val result = logic.dropPiece(state, 0)

        assertEquals(BoardCell.P1, result.board[3][0])
        assertEquals(Player.TWO, result.currentPlayer)
        assertEquals(GameState.State.InProgress, result.state)
    }

    @Test
    fun `should detect win and update score after dropPiece`() {
        val config = GameConfig(rows = 4, cols = 4, winCondition = 3)
        val board = boardOf(config.rows, config.cols) { r, c ->
            if (r == 3 && c in 0..1) BoardCell.P1 else BoardCell.EMPTY
        }
        val state = GameState(
            config = config,
            board = board,
            currentPlayer = Player.ONE,
            score = mapOf(Player.ONE to 0, Player.TWO to 0)
        )

        val result = logic.dropPiece(state, 2)

        assertTrue(result.state is GameState.State.Won)
        val won = result.state
        assertEquals(Player.ONE, won.player)
        assertEquals(1, result.score[Player.ONE])
        assertEquals(Player.ONE, result.currentPlayer)
    }

    @Test
    fun `should detect diagonal win after dropPiece`() {
        val config = GameConfig(rows = 4, cols = 4, winCondition = 3)
        val board = boardOf(config.rows, config.cols) { r, c ->
            when {
                r == 3 && c == 0 -> BoardCell.P1
                r == 2 && c == 1 -> BoardCell.P1
                r == 3 && c == 2 -> BoardCell.P2
                r == 2 && c == 2 -> BoardCell.P2
                else -> BoardCell.EMPTY
            }
        }
        val state = GameState(
            config = config,
            board = board,
            currentPlayer = Player.ONE
        )

        val result = logic.dropPiece(state, 2)

        assertTrue(result.state is GameState.State.Won)
    }

    @Test
    fun `should detect draw when top row becomes full after dropPiece`() {
        val config = GameConfig(rows = 2, cols = 2, winCondition = 3)
        val board = listOf(
            listOf(BoardCell.P1, BoardCell.EMPTY),
            listOf(BoardCell.P2, BoardCell.P1)
        )
        val state = GameState(
            config = config,
            board = board,
            currentPlayer = Player.TWO
        )

        val result = logic.dropPiece(state, 1)

        assertEquals(GameState.State.Draw, result.state)
        assertEquals(1, result.draws)
    }

    @Test
    fun `should update only target cell when placeCell called`() {
        val board = boardOf(2, 2) { _, _ -> BoardCell.EMPTY }

        val updated = logic.placeCell(board, 1, 1, BoardCell.P1)

        assertEquals(BoardCell.P1, updated[1][1])
        assertEquals(BoardCell.EMPTY, updated[0][0])
        assertEquals(BoardCell.EMPTY, updated[1][0])
    }

    @Test
    fun `should collect contiguous cells in both directions when buildLine called`() {
        val config = GameConfig(rows = 4, cols = 4, winCondition = 3)
        val board = boardOf(config.rows, config.cols) { r, c ->
            if (r == 3 && c in 0..2) BoardCell.P1 else BoardCell.EMPTY
        }

        val line = logic.buildLine(board, 3, 1, 0, 1, BoardCell.P1, config)

        val expected = setOf(Cell(3, 0), Cell(3, 1), Cell(3, 2))
        assertEquals(3, line.size)
        assertTrue(line.containsAll(expected))
    }

    @Test
    fun `should return winning line when checkWin finds match`() {
        val config = GameConfig(rows = 4, cols = 4, winCondition = 3)
        val board = boardOf(config.rows, config.cols) { r, c ->
            if (r == 3 && c in 0..2) BoardCell.P1 else BoardCell.EMPTY
        }

        val win = logic.checkWin(board, 3, 1, BoardCell.P1, config)

        assertEquals(3, win.size)
    }

    @Test
    fun `should return InProgress when resolveState has no win or draw`() {
        val config = GameConfig(rows = 4, cols = 4, winCondition = 3)
        val board = boardOf(config.rows, config.cols) { r, c ->
            if (r == 3 && c == 0) BoardCell.P1 else BoardCell.EMPTY
        }

        val state = logic.resolveState(board, 3, 0, Player.ONE, config)

        assertEquals(GameState.State.InProgress, state)
    }

    @Test
    fun `should detect draw when top row is full`() {
        val board = listOf(
            listOf(BoardCell.P1, BoardCell.P2),
            listOf(BoardCell.EMPTY, BoardCell.P1)
        )

        assertTrue(logic.isDraw(board))
        assertFalse(logic.isDraw(boardOf(2, 2) { _, _ -> BoardCell.EMPTY }))
    }

    @Test
    fun `should keep same player when nextPlayer called after win or draw`() {
        val winState = GameState.State.Won(Player.ONE, emptySet())

        assertEquals(Player.ONE, logic.nextPlayer(Player.ONE, winState))
        assertEquals(Player.TWO, logic.nextPlayer(Player.TWO, GameState.State.Draw))
    }

    @Test
    fun `should update score or draws only after terminal state`() {
        val score = mapOf(Player.ONE to 1, Player.TWO to 2)
        val winState = GameState.State.Won(Player.ONE, emptySet())

        val updatedScore = logic.updatedScore(score, Player.ONE, winState)
        val unchangedScore = logic.updatedScore(score, Player.ONE, GameState.State.InProgress)
        val updatedDraws = logic.updatedDraws(2, GameState.State.Draw)
        val unchangedDraws = logic.updatedDraws(2, GameState.State.InProgress)

        assertEquals(2, updatedScore[Player.ONE])
        assertEquals(score, unchangedScore)
        assertEquals(3, updatedDraws)
        assertEquals(2, unchangedDraws)
    }

    private fun boardOf(rows: Int, cols: Int, cellAt: (Int, Int) -> BoardCell): List<List<BoardCell>> =
        List(rows) { r -> List(cols) { c -> cellAt(r, c) } }
}
