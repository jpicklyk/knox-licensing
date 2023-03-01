package net.sfelabs.knoxmoduleshowcase.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val disclaimer = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed accumsan " +
        "neque vel felis luctus, condimentum dignissim felis tempus. Orci varius natoque penatibus " +
        "et magnis dis parturient montes, nascetur ridiculus mus. Vivamus libero nisl, mollis a " +
        "posuere nec, dignissim ac nisl. Nullam rhoncus, neque quis condimentum scelerisque, nulla " +
        "nisi congue nibh, sed accumsan urna lacus in nulla. Nullam eget massa consectetur, cursus " +
        "nulla quis, mollis nisl. Maecenas tempor, turpis non rhoncus pharetra, lorem leo dictum " +
        "erat, et consequat lectus enim a quam. Etiam vulputate justo in erat hendrerit, non " +
        "vestibulum enim tempor.\n" +
        "\n" +
        "Sed consectetur eleifend risus, vel egestas mi ultricies non. Nulla vitae sodales lacus. " +
        "Interdum et malesuada fames ac ante ipsum primis in faucibus. Mauris dignissim diam at " +
        "erat tincidunt aliquam. Aenean condimentum hendrerit purus, nec placerat nibh lobortis " +
        "accumsan. Nulla et ligula eget magna lacinia auctor non in quam. Aenean facilisis " +
        "scelerisque nunc, at congue lorem tempor sit amet. Proin ac bibendum leo. In hendrerit " +
        "scelerisque mi.\n"

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AboutScreen() {
    OutlinedCard(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(135.dp)
                )
            }
            Row() {
                Text(text = disclaimer, style = MaterialTheme.typography.bodySmall)
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /*TODO*/ }) {
                    Text(text = "I Agree")

                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewAboutScreen() {
    AboutScreen()
}