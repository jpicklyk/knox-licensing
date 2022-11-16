package net.sfelabs.common.knox

sealed class KnoxComponentType {
    //<out T: Any, R>
    data class BooleanComponent(val checkedState: Boolean? = null): KnoxComponentType()
    data class SpinnerComponent(
        val checkedState: Boolean? = null,
        val lteBandState: Int = 0
    ): KnoxComponentType()

}
