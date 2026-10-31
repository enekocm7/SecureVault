package ui.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enekocm.securevault.R
import ui.compose.theme.LightRed
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme

// 1:1 of cloud_suggestion.xml (and login-mode variant from CloudActivity.changeLoginAppearance)
@Composable
fun CloudScreen(
    isLoginMode: Boolean,
    signInEnabled: Boolean,
    onGoogleSignIn: () -> Unit,
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
        if (!isLoginMode) {
            Image(
                painter = painterResource(R.drawable.cloud_24dp),
                contentDescription = stringResource(R.string.cloud_icon),
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(26.dp))
        }
        Text(
            text = if (isLoginMode) stringResource(R.string.login) else stringResource(R.string.enable_cloud_sync),
            color = Red,
            fontSize = if (isLoginMode) 48.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isLoginMode) stringResource(R.string.login_description) else stringResource(R.string.cloud_description),
            color = Red,
            fontSize = if (isLoginMode) 24.sp else 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(48.dp))
        GoogleButton(
            enabled = signInEnabled,
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isLoginMode) stringResource(R.string.cancel) else stringResource(R.string.maybe_later),
            color = Red, fontSize = 16.sp,
            modifier = Modifier
                .clickable(onClick = onSkip)
                .padding(8.dp)
                .height(48.dp)
        )
    }
}

@Composable
fun GoogleButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(20.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1F1F1F)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDADCE0))
    ) {
        Image(
            painter = painterResource(R.drawable.google_logo_small),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 0.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(stringResource(R.string.continue_with_google), fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun CloudPreview() {
    SecureVaultTheme(darkTheme = false) {
        CloudScreen(isLoginMode = false, signInEnabled = true, onGoogleSignIn = {}, onSkip = {})
    }
}
