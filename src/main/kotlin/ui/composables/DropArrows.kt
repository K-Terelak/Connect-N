package ui.composables

import androidx.compose.runtime.Composable
import ui.GameViewModel
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun DropArrows(viewModel: GameViewModel) {
    val cols = viewModel.gameState.config.cols

    Div(attrs = {
        classes("drop-arrows")
        style { gridTemplateColumns("repeat($cols, 1fr)") }
    }) {
        repeat(cols) { col ->
            Div(attrs = {
                classes("drop-arrow")
                id("arrow-$col")
            }) { Text("▼") }
        }
    }
}
