package net.sfelabs.common.knox

class KnoxApi(
    val name: String,
    val description: String?,
    val knoxComponentType: KnoxComponentType,
    val functionExists: Boolean = true,
    val functionName: String? = null,
    val callingClass: String? = null,
    val onChanged: ((Any) -> Unit)
)