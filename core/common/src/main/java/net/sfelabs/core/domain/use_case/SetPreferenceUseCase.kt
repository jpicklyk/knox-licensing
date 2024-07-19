package net.sfelabs.core.domain.use_case

import net.sfelabs.core.domain.repository.PreferencesRepository
import javax.inject.Inject

class SetPreferenceUseCase<T> @Inject constructor(
    private val repository: PreferencesRepository
){
    suspend operator fun invoke(key: String, value: T) {
        repository.setValue(key, value)
    }
}