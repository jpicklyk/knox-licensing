//import net.sfelabs.knoxmoduleshowcase.configureFlavors


import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import net.sfelabs.implementation.configureKotlinAndroid
import net.sfelabs.implementation.configurePrintApksTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            val extension = extensions.getByType<ApplicationExtension>()

            //extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(extension)
                extension.defaultConfig.targetSdk = 33
                //configureFlavors(this)
            //}
            val componentsExtension = extensions.getByType<ApplicationAndroidComponentsExtension>()
            //extensions.configure<ApplicationAndroidComponentsExtension> {
                configurePrintApksTask(componentsExtension)
            //}


        }
    }

}