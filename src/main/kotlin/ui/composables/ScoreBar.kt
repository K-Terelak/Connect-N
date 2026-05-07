package ui.composables

import androidx.compose.runtime.Composable
import ui.GameViewModel
import model.Player
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun ScoreBar(viewModel: GameViewModel) {
    val state = viewModel.gameState

    Div(attrs = { classes("score-bar") }) {
        Span(attrs = { classes("s1") }) {
            Text(value = "P1 WINS: ")
            Span { Text(value = (state.score[Player.ONE] ?: 0).toString()) }
        }

        Span(attrs = { classes("sd") }) {
            Text(value = "DRAWS: ")
            Span { Text(value = state.draws.toString()) }
        }

        Span(attrs = { classes("s2") }) {
            Text(value = "P2 WINS: ")
            Span { Text(value = (state.score[Player.TWO] ?: 0).toString()) }
        }
    }
}
