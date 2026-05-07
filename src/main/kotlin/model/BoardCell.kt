package model

import kotlinx.serialization.Serializable

@Serializable
enum class BoardCell {
    EMPTY, P1, P2;
}
