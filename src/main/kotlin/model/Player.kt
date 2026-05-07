package model

import kotlinx.serialization.Serializable

@Serializable
enum class Player {
    ONE,
    TWO;

    companion object {
        fun Player.toCell(): BoardCell = when (this) {
            ONE -> BoardCell.P1
            TWO -> BoardCell.P2
        }
    }
}
