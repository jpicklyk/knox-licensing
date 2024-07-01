package net.sfelabs.core.knoxfeature

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.Test

class KonsistArchitectureTest {
    private val data = Layer("Data", "net.sfelabs.core.knoxfeature.data..")
    private val domain = Layer("Domain", "net.sfelabs.core.knoxfeature.domain..")
    private val model = Layer("Model", "net.sfelabs.core.knoxfeature.model..")
    private val scope = Konsist
        .scopeFromProject()

    @Test
    fun `verify clean architecture`() {
        scope.assertArchitecture {
            //domain.dependsOn(data)
            domain.dependsOn(model)
            //data.dependsOnNothing()
        }
    }
}