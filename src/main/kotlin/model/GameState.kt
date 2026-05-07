package model

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val config: GameConfig = GameConfig(),
    val board: List<List<BoardCell>> = List(config.rows) { List(config.cols) { BoardCell.EMPTY } },
    val currentPlayer: Player = Player.ONE,
    val state: State = State.InProgress,
    val score: Map<Player, Int> = mapOf(Player.ONE to 0, Player.TWO to 0),
    val draws: Int = 0
) {
    @Serializable
    sealed interface State {
        @Serializable
        object InProgress : State

        @Serializable
        object Draw : State

        @Serializable
        data class Won(val player: Player, val cells: Set<Cell>) : State
    }
}
