package net.sfelabs.knox_tactical.domain.model

sealed class AutoConnectionState(val state: Int) {
    companion object {
        operator fun invoke(type: Int): AutoConnectionState {
            return if (type  == 0) OFF else ON
        }
    }
    data object ON: AutoConnectionState(1)
    data object OFF: AutoConnectionState(0)
}
