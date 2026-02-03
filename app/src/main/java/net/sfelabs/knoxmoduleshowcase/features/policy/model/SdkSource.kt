package net.sfelabs.knoxmoduleshowcase.features.policy.model

/**
 * Identifies the SDK source of a policy component.
 *
 * Used to categorize policies based on which Knox SDK they originate from,
 * enabling UI filtering and tab organization.
 */
enum class SdkSource(val displayName: String) {
    /** Knox Tactical SDK (specialized tactical features) */
    TACTICAL("TE Policies"),

    /** Knox Enterprise SDK (base Knox functionality) */
    ENTERPRISE("Base Policies");

    companion object {
        /**
         * Determines SDK source from the component's class package name.
         *
         * The KSP-generated policy components are placed in predictable packages:
         * - `net.sfelabs.knox_tactical.generated.policy.*` for tactical
         * - `net.sfelabs.knox_enterprise.generated.policy.*` for enterprise
         */
        fun fromComponent(component: Any): SdkSource {
            val packageName = component::class.java.packageName
            return when {
                packageName.contains("knox_tactical") -> TACTICAL
                else -> ENTERPRISE
            }
        }
    }
}
