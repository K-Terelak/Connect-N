import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import ui.GameViewModel
import ui.composables.ConfigPanel
import ui.composables.DropArrows
import ui.composables.GameBoard
import ui.composables.ScoreBar
import ui.composables.StatusBar

fun main() {
    val viewModel = GameViewModel()

    renderComposable(rootElementId = "root") {
        Div(attrs = { classes("app") }) {
            ConfigPanel(viewModel)
            StatusBar(viewModel)
            ScoreBar(viewModel)
            DropArrows(viewModel)
            GameBoard(viewModel)
        }
    }
}
