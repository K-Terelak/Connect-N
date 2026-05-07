package ui.composables

import androidx.compose.runtime.Composable
import ui.GameViewModel
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun ConfigPanel(viewModel: GameViewModel) {
    val cfg = viewModel.gameState.config

    Div(attrs = { classes("config-panel") }) {
        ConfigStepper(
            label = "Columns",
            value = cfg.cols,
            min = cfg.colsMin,
            max = cfg.colsMax,
        ) { newValue ->
            val newCfg = cfg.copy(cols = newValue)
            viewModel.updateConfig(newCfg)
        }

        ConfigStepper(
            label = "Rows",
            value = cfg.rows,
            min = cfg.rowsMin,
            max = cfg.rowsMax
        ) { newValue ->
            val newCfg = cfg.copy(rows = newValue)
            viewModel.updateConfig(newCfg)
        }

        ConfigStepper(
            label = "Connect",
            value = cfg.winCondition,
            min = cfg.winConditionMin,
            max = cfg.winConditionMax
        ) { newValue ->
            viewModel.updateConfig(cfg.copy(winCondition = newValue))
        }

        Button(attrs = {
            classes("btn-new")
            onClick { viewModel.newGame() }
        }) {
            Text(value = "NEW GAME")
        }
    }
}
