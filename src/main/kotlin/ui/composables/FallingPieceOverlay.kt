package ui.composables

import androidx.compose.runtime.Composable
import ui.GameViewModel
import model.Player
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.gridTemplateRows
import org.jetbrains.compose.web.dom.Div

@Composable
fun FallingPieceOverlay(viewModel: GameViewModel) {
    val animCol = viewModel.animatingCol ?: return
    val animRow = viewModel.animatingRow ?: return
    val player = viewModel.gameState.currentPlayer
    val config = viewModel.gameState.config
    val duration = (60 * (animRow + 1)).coerceIn(280, 550)

    Div(attrs = {
        classes("falling-overlay")
        style {
            gridTemplateColumns("repeat(${config.cols}, 1fr)")
            gridTemplateRows("repeat(${config.rows}, 1fr)")
        }
    }) {
        Div(attrs = {
            classes("falling-piece")
            if (player == Player.ONE) classes("p1") else classes("p2")
            style {
                property("grid-column-start", "${animCol + 1}")
                property("grid-row-start", "${animRow + 1}")
                property("--fall-rows", "${animRow + 1}")
                property("--fall-duration", "${duration}ms")
            }
        })
    }
}
