package net.sfelabs.knox_tactical.domain.model

class VersionInfo private constructor(
    val description: String,
    val releaseVersion: Int,
    val generation: Int,
    val buildNumber: String,
    val additionalInfo: Map<String, String>
) {
    class Builder {
        private var description: String = ""
        private var releaseVersion: Int = 0
        private var generation: Int = 0
        private var buildNumber: String = ""
        private val additionalInfo: MutableMap<String, String> = mutableMapOf()

        fun description(description: String) = apply { this.description = description }
        fun releaseVersion(releaseVersion: Int) = apply { this.releaseVersion = releaseVersion }
        fun generation(generation: Int) = apply { this.generation = generation }
        fun buildNumber(buildNumber: String) = apply { this.buildNumber = buildNumber }
        fun addInfo(key: String, value: String) = apply { this.additionalInfo[key] = value }
        fun build() = VersionInfo(description, releaseVersion, generation, buildNumber, additionalInfo)

        companion object {
            fun createDefaultVersion(buildNumber: String) = Builder()
                .description("Unknown")
                .releaseVersion(0)
                .generation(0)
                .buildNumber(buildNumber)
                .build()

        }
    }

    val isTacticalEdition get() = generation > 0
}