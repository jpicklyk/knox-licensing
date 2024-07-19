package net.sfelabs.core.domain.use_case

import kotlinx.coroutines.flow.Flow
import net.sfelabs.core.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetPreferenceUseCase<T> @Inject constructor(
    private val repository: PreferencesRepository
){
    operator fun invoke(key: String, defaultValue: T): Flow<T> {
        return repository.getValue(key, defaultValue)
    }
}