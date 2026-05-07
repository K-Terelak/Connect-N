package ui.composables

import androidx.compose.runtime.Composable
import ui.GameViewModel
import kotlinx.browser.document
import model.BoardCell
import model.Cell
import model.GameState
import model.Player
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.gridTemplateRows
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

@Composable
fun GameBoard(viewModel: GameViewModel) {
    val model = viewModel.gameState
    val config = model.config
    val winCells = (model.state as? GameState.State.Won)?.cells ?: emptySet()
    val isLocked = model.state != GameState.State.InProgress || viewModel.animatingCol != null

    Div(attrs = { classes("game-wrapper") }) {
        Board(model.board, config.rows, config.cols, winCells)
        FallingPieceOverlay(viewModel)
        ColumnZones(viewModel, config.cols, isLocked, model.currentPlayer)
    }
}

@Composable
private fun Board(
    board: List<List<BoardCell>>,
    rows: Int,
    cols: Int,
    winCells: Set<Cell>
) {
    Div(attrs = {
        classes("board")
        style {
            gridTemplateColumns("repeat($cols, 1fr)")
            gridTemplateRows("repeat($rows, 1fr)")
        }
    }) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                BoardCellView(board[row][col], Cell(row, col) in winCells)
            }
        }
    }
}

@Composable
private fun BoardCellView(cell: BoardCell, isWin: Boolean) {
    Div(attrs = {
        classes("cell")
        when (cell) {
            BoardCell.P1 -> classes("p1")
            BoardCell.P2 -> classes("p2")
            BoardCell.EMPTY -> classes("empty")
        }
        if (isWin) classes("win-cell")
    })
}

@Composable
private fun ColumnZones(
    viewModel: GameViewModel,
    cols: Int,
    isLocked: Boolean,
    currentPlayer: Player
) {
    Div(attrs = {
        classes("col-zones")
        style {
            gridTemplateColumns("repeat($cols, 1fr)")
            if (isLocked) property("pointer-events", "none")
        }
    }) {
        for (col in 0 until cols) {
            ColumnZone(col, currentPlayer, viewModel::dropPiece)
        }
    }
}

@Composable
private fun ColumnZone(col: Int, currentPlayer: Player, onDrop: (Int) -> Unit) {
    val arrowClass = if (currentPlayer == Player.ONE) "active-p1" else "active-p2"
    Div(attrs = {
        classes("col-zone")
        onClick { onDrop(col) }
        onMouseEnter {
            (document.getElementById("arrow-$col") as? HTMLElement)
                ?.classList?.add(arrowClass)
        }
        onMouseLeave {
            (document.getElementById("arrow-$col") as? HTMLElement)
                ?.classList?.remove("active-p1", "active-p2")
        }
    })
}
