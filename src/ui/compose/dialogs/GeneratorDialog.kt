package ui.compose.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.enekocm.securevault.R
import domain.model.PasswordStrength
import ui.compose.theme.LightRed
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme
import ui.compose.theme.White

// 1:1 of password_generator.xml
@Composable
fun GeneratePasswordDialogContent(
    generated: String,
    strength: PasswordStrength,
    isLoading: Boolean,
    onRegenerate: () -> Unit,
    onUse: (String) -> Unit,
    onClose: () -> Unit,
    onGeneratePassword: (length: Int, lower: Boolean, upper: Boolean, numbers: Boolean, symbols: Boolean) -> Unit,
    onGeneratePassphrase: (words: Int) -> Unit
) {
    var isPassphrase by remember { mutableStateOf(false) }
    var length by remember { mutableFloatStateOf(16f) }
    var words by remember { mutableFloatStateOf(5f) }
    var lower by remember { mutableStateOf(true) }
    var upper by remember { mutableStateOf(true) }
    var numbers by remember { mutableStateOf(true) }
    var symbols by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    stringResource(R.string.password_generator),
                    color = Red, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 9.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoading) {
                            LinearProgressIndicator(color = Red, trackColor = LightRed, modifier = Modifier.weight(1f))
                        } else {
                            Text(
                                text = generated.ifEmpty { stringResource(R.string.generated_password_will_appear_here) },
                                color = Red, fontSize = 16.sp, fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        IconButton(onClick = onRegenerate, modifier = Modifier.padding(start = 8.dp)) {
                            Icon(painterResource(R.drawable.ic_generate), stringResource(R.string.regenerate_password), tint = Red)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Text(stringResource(R.string.password_strength), color = Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strength.label, color = Red, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { strength.progress / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(android.graphics.Color.parseColor(strength.color)),
                    trackColor = Red.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        RadioButton(
                            selected = !isPassphrase,
                            onClick = { isPassphrase = false; onGeneratePassword(length.toInt(), lower, upper, numbers, symbols) },
                            colors = RadioButtonDefaults.colors(selectedColor = Red)
                        )
                        Text(stringResource(R.string.password), color = Red)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        RadioButton(
                            selected = isPassphrase,
                            onClick = { isPassphrase = true; onGeneratePassphrase(words.toInt()) },
                            colors = RadioButtonDefaults.colors(selectedColor = Red)
                        )
                        Text(stringResource(R.string.passphrase), color = Red)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (!isPassphrase) {
                    Text("${stringResource(R.string.length)} ${length.toInt()}", color = Red)
                    Slider(
                        value = length, onValueChange = { length = it; onGeneratePassword(it.toInt(), lower, upper, numbers, symbols) },
                        valueRange = 8f..128f,
                        colors = SliderDefaults.colors(thumbColor = Red, activeTrackColor = Red)
                    )
                    VaultSwitch(stringResource(R.string.lowercase_a_z), lower) { lower = it; onGeneratePassword(length.toInt(), lower, upper, numbers, symbols) }
                    VaultSwitch(stringResource(R.string.uppercase_a_z), upper) { upper = it; onGeneratePassword(length.toInt(), lower, upper, numbers, symbols) }
                    VaultSwitch(stringResource(R.string.numbers_0_9), numbers) { numbers = it; onGeneratePassword(length.toInt(), lower, upper, numbers, symbols) }
                    VaultSwitch(stringResource(R.string.symbols), symbols) { symbols = it; onGeneratePassword(length.toInt(), lower, upper, numbers, symbols) }
                } else {
                    Text("${stringResource(R.string.number_of_words)} ${words.toInt()}", color = Red)
                    Slider(
                        value = words, onValueChange = { words = it; onGeneratePassphrase(it.toInt()) },
                        valueRange = 3f..15f,
                        colors = SliderDefaults.colors(thumbColor = Red, activeTrackColor = Red)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onUse(generated) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Red)
                ) { Text(stringResource(R.string.use_this_password)) }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Red)
                ) { Text(stringResource(R.string.close), color = Red) }
            }
        }
    }
}

@Composable
fun VaultSwitch(text: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = Red, modifier = Modifier.weight(1f))
        Switch(
            checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Red, checkedTrackColor = Red.copy(alpha = 0.5f))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GeneratorPreview() {
    SecureVaultTheme(darkTheme = false) {
        GeneratePasswordDialogContent(
            generated = "Xk9#mQ2!", strength = PasswordStrength.STRONG, isLoading = false,
            onRegenerate = {}, onUse = {}, onClose = {},
            onGeneratePassword = { _, _, _, _, _ -> }, onGeneratePassphrase = {}
        )
    }
}
