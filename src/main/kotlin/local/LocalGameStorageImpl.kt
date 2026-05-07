package local

import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json
import model.GameState

private const val STORAGE_KEY = "connect-n"

class LocalGameStorageImpl(
    private val json: Json = Json { ignoreUnknownKeys = true }
) : LocalGameStorage {

    override fun save(state: GameState) {
        try {
            localStorage.setItem(STORAGE_KEY, json.encodeToString<GameState>(state))
        } catch (e: Exception) {
            console.warn("Failed to save state: ${e.message}")
        }
    }

    override fun load(): GameState? {
        return try {
            val raw = localStorage.getItem(STORAGE_KEY) ?: return null
            json.decodeFromString<GameState>(raw)
        } catch (e: Exception) {
            console.warn("Failed to load state: ${e.message}")
            null
        }
    }
}
