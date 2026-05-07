package ui

import model.BoardCell
import model.Cell
import model.GameConfig
import model.GameState
import model.Player
import model.Player.Companion.toCell

/**
 * Pure game rules engine used by the UI layer.
 */
interface GameLogic {
    /**
     * Applies a move for the current player by dropping a piece into [col].
     *
     * If the column is full, the state is returned unchanged. On a valid move,
     * it produces a new immutable [GameState] with updated board, turn,
     * score/draw counters, and resolved win/draw status.
     */
    fun dropPiece(model: GameState, col: Int): GameState

    /**
     * Returns the target row index for a drop in [col], or null if the column is full.
     *
     * The search starts from the bottom row and stops at the first empty cell.
     */
    fun getDropRow(board: List<List<BoardCell>>, col: Int, rows: Int): Int?
}

class GameLogicImpl : GameLogic {

    /**
     * Applies a move and returns the new immutable game state.
     */
    override fun dropPiece(model: GameState, col: Int): GameState {
        val row = getDropRow(model.board, col, model.config.rows) ?: return model
        val newBoard = placeCell(model.board, row, col, model.currentPlayer.toCell())
        val newState = resolveState(newBoard, row, col, model.currentPlayer, model.config)
        return model.copy(
            board = newBoard,
            currentPlayer = nextPlayer(model.currentPlayer, newState),
            state = newState,
            score = updatedScore(model.score, model.currentPlayer, newState),
            draws = updatedDraws(model.draws, newState)
        )
    }

    /**
     * Finds the first available row for a given column, scanning from bottom to top.
     */
    override fun getDropRow(board: List<List<BoardCell>>, col: Int, rows: Int): Int? =
        (rows - 1 downTo 0).firstOrNull { board[it][col] == BoardCell.EMPTY }

    /**
     * Checks if the last move creates a winning line and returns the winning cells.
     *
     * The scan considers four directions (horizontal, vertical, two diagonals)
     * and includes the origin cell at ([row], [col]).
     */
    fun checkWin(
        board: List<List<BoardCell>>,
        row: Int,
        col: Int,
        cell: BoardCell,
        config: GameConfig
    ): List<Cell> {
        val directions = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        return directions
            .map { (dr, dc) -> buildLine(board, row, col, dr, dc, cell, config) }
            .firstOrNull { it.size >= config.winCondition }
            ?: emptyList()
    }

    /**
     * A draw occurs when the top row is fully occupied.
     */
    fun isDraw(board: List<List<BoardCell>>): Boolean =
        board[0].all { it != BoardCell.EMPTY }

    /**
     * Returns a new board with the [cell] placed at ([row], [col]).
     */
    fun placeCell(
        board: List<List<BoardCell>>,
        row: Int,
        col: Int,
        cell: BoardCell
    ): List<List<BoardCell>> = board.mapIndexed { r, rowList ->
        if (r != row) rowList
        else rowList.mapIndexed { c, v -> if (c == col) cell else v }
    }

    /**
     * Resolves the next [GameState.State] after a move by checking win and draw.
     */
    fun resolveState(
        board: List<List<BoardCell>>,
        row: Int,
        col: Int,
        player: Player,
        config: GameConfig
    ): GameState.State {
        val winCells = checkWin(board, row, col, player.toCell(), config)
        return when {
            winCells.isNotEmpty() -> GameState.State.Won(player, winCells.toSet())
            isDraw(board) -> GameState.State.Draw
            else -> GameState.State.InProgress
        }
    }

    /**
     * Advances the turn only if the game is still in progress.
     */
    fun nextPlayer(current: Player, state: GameState.State): Player =
        if (state == GameState.State.InProgress)
            if (current == Player.ONE) Player.TWO else Player.ONE
        else current

    /**
     * Increments the winner score when the state is [GameState.State.Won].
     */
    fun updatedScore(
        score: Map<Player, Int>,
        player: Player,
        state: GameState.State
    ): Map<Player, Int> = if (state is GameState.State.Won) {
        score + (player to (score[player]!! + 1))
    } else score

    /**
     * Increments draw counter when the state is [GameState.State.Draw].
     */
    fun updatedDraws(draws: Int, state: GameState.State): Int =
        if (state == GameState.State.Draw) draws + 1 else draws

    /**
     * Builds a line of contiguous cells matching [cell] in the given direction.
     *
     * The line grows in both positive and negative directions from the origin.
     */
    fun buildLine(
        board: List<List<BoardCell>>,
        row: Int, col: Int,
        dr: Int, dc: Int,
        cell: BoardCell,
        config: GameConfig
    ): List<Cell> {
        val cells = mutableListOf(Cell(row, col))
        for (dir in listOf(-1, 1)) {
            var r = row + dr * dir
            var c = col + dc * dir
            while (r in 0 until config.rows && c in 0 until config.cols && board[r][c] == cell) {
                cells.add(Cell(r, c))
                r += dr * dir
                c += dc * dir
            }
        }
        return cells
    }
}
