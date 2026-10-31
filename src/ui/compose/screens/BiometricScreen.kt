package ui.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enekocm.securevault.R
import ui.compose.components.PrimaryButton
import ui.compose.theme.LightRed
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme

// 1:1 of biometric_sugestion.xml
@Composable
fun BiometricScreen(
    buttonText: String,
    buttonEnabled: Boolean,
    onEnable: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightRed)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Image(
            painter = painterResource(R.drawable.baseline_fingerprint_24),
            contentDescription = stringResource(R.string.fingerprint_icon),
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.enable_fingerprint),
            color = Red, fontSize = 24.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.fingerprint_description),
            color = Red, fontSize = 16.sp,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(48.dp))
        PrimaryButton(
            text = buttonText.ifEmpty { stringResource(R.string.enable_fingerprint_login) },
            onClick = onEnable,
            enabled = buttonEnabled,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.maybe_later),
            color = Red, fontSize = 16.sp,
            modifier = Modifier
                .clickable(onClick = onSkip)
                .padding(8.dp)
                .height(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BiometricPreview() {
    SecureVaultTheme(darkTheme = false) {
        BiometricScreen(buttonText = "", buttonEnabled = true, onEnable = {}, onSkip = {})
    }
}
