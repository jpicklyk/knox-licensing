package net.sfelabs.knox_tactical.domain.model

sealed class AutoConnectionState(val state: Int) {
    companion object {
        operator fun invoke(type: Int): AutoConnectionState {
            return if (type  == 0) OFF else ON
        }
    }
    object ON: AutoConnectionState(1)
    object OFF: AutoConnectionState(0)
}
