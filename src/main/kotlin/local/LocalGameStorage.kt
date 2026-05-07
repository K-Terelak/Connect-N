package local

import model.GameState

interface LocalGameStorage {
    fun save(state: GameState)
    fun load(): GameState?
}
