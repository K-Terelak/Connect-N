package ui.composables

import androidx.compose.runtime.Composable
import model.GameState
import model.GameState.State.Draw
import model.GameState.State.InProgress
import model.Player
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import ui.GameViewModel

@Composable
fun StatusBar(viewModel: GameViewModel) {
    val model = viewModel.gameState
    val won = model.state as? GameState.State.Won

    val p1Active = model.state == InProgress && model.currentPlayer == Player.ONE
    val p2Active = model.state == InProgress && model.currentPlayer == Player.TWO

    Div(attrs = { classes("status-bar") }) {
        Div(attrs = { classes("player-indicator") }) {
            Div(attrs = {
                classes("piece-dot", "p1")
                if (p1Active) classes("active")
            })
            Span { Text("PLAYER 1") }
        }

        Div(attrs = {
            classes("status-msg")
            when {
                won?.player == Player.ONE -> classes("p1-win")
                won?.player == Player.TWO -> classes("p2-win")
                model.state == Draw -> classes("draw")
            }
        }) {
            Text(
                value = when {
                    won != null -> "PLAYER ${if (won.player == Player.ONE) 1 else 2} WINS!"
                    model.state == Draw -> "IT'S A DRAW!"
                    else -> "PLAYER ${if (model.currentPlayer == Player.ONE) 1 else 2}'S TURN"
                }
            )
        }

        Div(attrs = { classes("player-indicator") }) {
            Span { Text("PLAYER 2") }
            Div(attrs = {
                classes("piece-dot", "p2")
                if (p2Active) classes("active")
            })
        }
    }
}
