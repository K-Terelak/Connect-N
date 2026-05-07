package local

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.browser.localStorage
import model.GameConfig
import model.GameState

class LocalGameStorageImplTest {

    @Test
    fun `should round-trip state after save and load`() {
        clearStorage()
        val storage = LocalGameStorageImpl()
        val state = GameState(config = GameConfig(rows = 4, cols = 4, winCondition = 3))

        storage.save(state)
        val loaded = storage.load()

        assertEquals(state, loaded)
        clearStorage()
    }

    @Test
    fun `should return null when load gets invalid json`() {
        clearStorage()
        localStorage.setItem(STORAGE_KEY, "{not-valid-json}")
        val storage = LocalGameStorageImpl()

        val loaded = storage.load()

        assertNull(loaded)
        clearStorage()
    }

    private fun clearStorage() {
        localStorage.removeItem(STORAGE_KEY)
    }

    private companion object {
        private const val STORAGE_KEY = "connect-n"
    }
}
