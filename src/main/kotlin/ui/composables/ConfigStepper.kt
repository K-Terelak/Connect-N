package ui.composables

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text

@Composable
fun ConfigStepper(
    label: String,
    value: Int,
    min: Int,
    max: Int,
    onChange: (Int) -> Unit
) {
    Div(attrs = { classes("config-group") }) {
        Label { Text(value = label) }

        Div(attrs = { classes("stepper") }) {
            Button(attrs = {
                classes("btn-step")
                onClick { if (value > min) onChange(value - 1) }
            }) {
                Text(value = "−")
            }

            Label { Text(value = value.toString()) }

            Button(attrs = {
                classes("btn-step")
                onClick { if (value < max) onChange(value + 1) }
            }) {
                Text(value = "+")
            }
        }
    }
}
