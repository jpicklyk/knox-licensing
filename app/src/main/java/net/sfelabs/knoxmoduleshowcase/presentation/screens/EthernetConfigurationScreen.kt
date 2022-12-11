package net.sfelabs.knoxmoduleshowcase.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import net.sfelabs.android_log_wrapper.presentation.LogViewer
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.components.EthernetConfiguration
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.components.EthernetList

@Composable
fun EthernetConfigurationScreen(
) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    Column() {
        EthernetConfiguration()
        //LogViewer()
        EthernetList()
        /*
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row() {
                    EthernetConfiguration()
                    LogViewer()
                }
            } else -> {
                EthernetConfiguration()
                LogViewer()
            }
        }
    */
   }
}
