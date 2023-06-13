package net.sfelabs.knoxmoduleshowcase.app.presentation.screens.logviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sfelabs.android_log_wrapper.Log
import net.sfelabs.android_log_wrapper.Priority.ASSERT.toPriority
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class LogViewerViewModel @Inject constructor(
    private val log: Log
): ViewModel() {

    fun generateLogLine() {
        viewModelScope.launch(Dispatchers.IO) {
            val priority = Random.nextInt(2, 8).toPriority()
            log.println(priority, message = "Log line generated at: "+ LocalDateTime.now())
        }

    }
}