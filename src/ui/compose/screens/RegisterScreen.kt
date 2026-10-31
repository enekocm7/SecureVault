package ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enekocm.securevault.R
import domain.model.PasswordStrength
import ui.compose.components.PrimaryButton
import ui.compose.components.VaultTextField
import ui.compose.theme.LightRed
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme

// 1:1 of create_master_password.xml
@Composable
fun RegisterScreen(
    strength: PasswordStrength?,
    onPasswordChange: (String) -> Unit,
    onContinue: (master: String, confirm: String) -> Unit,
    title: String = ""
) {
    var master by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var masterVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightRed)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title.ifEmpty { stringResource(R.string.create_master_password) },
            color = Red,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        VaultTextField(
            value = master,
            onValueChange = { master = it; onPasswordChange(it) },
            label = stringResource(R.string.master_password),
            visualTransformation = if (masterVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { masterVisible = !masterVisible }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = stringResource(R.string.toggle_visibility),
                        tint = Red
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(0.95f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        VaultTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = stringResource(R.string.confirm_password),
            visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = stringResource(R.string.toggle_visibility),
                        tint = Red
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(0.95f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${stringResource(R.string.strength)} ${strength?.label.orEmpty()}",
            color = Red,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (strength?.progress ?: 0) / 100f },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = strength?.let { Color(android.graphics.Color.parseColor(it.color)) } ?: Red,
            trackColor = Red.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.master_password_warning),
            color = Red,
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(8.dp))
                .background(LightRed)
                .border(1.dp, Red, RoundedCornerShape(8.dp))
                .padding(12.dp)
        )
        Spacer(modifier = Modifier.height(26.dp))
        PrimaryButton(
            text = stringResource(R.string.continue_button),
            onClick = { onContinue(master, confirm) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    SecureVaultTheme(darkTheme = false) {
        RegisterScreen(strength = PasswordStrength.STRONG, onPasswordChange = {}, onContinue = { _, _ -> })
    }
}
