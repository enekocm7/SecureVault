package ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.enekocm.securevault.R
import ui.compose.components.PrimaryButton
import ui.compose.components.VaultTextField
import ui.compose.theme.LightRed
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme

// 1:1 of login_screen.xml
@Composable
fun LoginScreen(
    photoUrl: String?,
    showBiometric: Boolean,
    onLogin: (String) -> Unit,
    onBiometric: () -> Unit,
    onAccountClick: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightRed)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onAccountClick)
                )
            } else {
                IconButton(onClick = onAccountClick, modifier = Modifier.size(42.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_account_circle),
                        contentDescription = null,
                        tint = Red,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(66.dp))
        Text(
            text = stringResource(R.string.login_to_your_vault),
            color = Red, fontSize = 24.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        VaultTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.master_password),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrect = false),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = stringResource(R.string.toggle_visibility),
                        tint = Red
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(0.95f)
        )
        Spacer(modifier = Modifier.height(50.dp))
        PrimaryButton(
            text = stringResource(R.string.login),
            onClick = { onLogin(password) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        if (showBiometric) {
            Spacer(modifier = Modifier.height(24.dp))
            IconButton(onClick = onBiometric, modifier = Modifier.size(72.dp)) {
                Icon(
                    painter = painterResource(R.drawable.baseline_fingerprint_24),
                    contentDescription = stringResource(R.string.biometric_login),
                    tint = Red,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    SecureVaultTheme(darkTheme = false) {
        LoginScreen(photoUrl = null, showBiometric = true, onLogin = {}, onBiometric = {}, onAccountClick = {})
    }
}
