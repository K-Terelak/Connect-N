package model

import kotlinx.serialization.Serializable

@Serializable
data class GameConfig(
    val cols: Int = 7,
    val rows: Int = 6,
    val winCondition: Int = 4,
    val winConditionMin: Int = 3,
    val winConditionMax: Int = 6,
    val rowsMin: Int = 6,
    val rowsMax: Int = 15,
    val colsMin: Int = 6,
    val colsMax: Int = 15,
)
