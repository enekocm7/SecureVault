package ui.compose.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.enekocm.securevault.R
import ui.compose.components.VaultTextField
import ui.compose.theme.LightRed
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme
import ui.compose.theme.White
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource

// 1:1 of dialog_import.xml
@Composable
fun ImportDialogContent(
    onBrowse: () -> Unit,
    onImport: (filePath: String, password: String, encrypted: Boolean) -> Unit,
    onCancel: () -> Unit,
    filePath: String,
    isBusy: Boolean
) {
    var encrypted by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    stringResource(R.string.import_file),
                    color = Red, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp)
                )
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OptionRow(stringResource(R.string.import_encrypted), encrypted) { encrypted = true }
                    Spacer(modifier = Modifier.height(8.dp))
                    OptionRow(stringResource(R.string.import_csv), !encrypted) { encrypted = false }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    VaultTextField(
                        value = filePath, onValueChange = {},
                        label = stringResource(R.string.select_file),
                        readOnly = true, modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onBrowse,
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) { Text(stringResource(R.string.browse)) }
                }
                if (encrypted) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.decryption_password), color = Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        VaultTextField(
                            value = password, onValueChange = { password = it },
                            label = stringResource(R.string.password),
                            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { visible = !visible }) {
                                    Icon(painterResource(R.drawable.baseline_visibility_24), null, tint = Red)
                                }
                            }
                        )
                    }
                }
                if (isBusy) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        color = Red, trackColor = LightRed,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                    Text(
                        stringResource(R.string.importing_passwords), color = Red,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedButton(
                        onClick = onCancel, modifier = Modifier.weight(1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Red)
                    ) { Text(stringResource(R.string.cancel), color = Red) }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { onImport(filePath, password, encrypted) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) { Text(stringResource(R.string.import_button)) }
                }
            }
        }
    }
}

// 1:1 of dialog_export.xml
@Composable
fun ExportDialogContent(
    onBrowse: () -> Unit,
    onExport: (path: String, password: String, confirm: String, encrypted: Boolean) -> Unit,
    onCancel: () -> Unit,
    path: String,
    isBusy: Boolean
) {
    var encrypted by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    stringResource(R.string.export_method),
                    color = Red, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp)
                )
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OptionRow(stringResource(R.string.export_encrypted), encrypted) { encrypted = true }
                    Spacer(modifier = Modifier.height(8.dp))
                    OptionRow(stringResource(R.string.export_csv), !encrypted) { encrypted = false }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    VaultTextField(
                        value = path, onValueChange = {},
                        label = stringResource(R.string.file_path),
                        readOnly = true, modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onBrowse,
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) { Text(stringResource(R.string.browse)) }
                }
                if (encrypted) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.encryption_password), color = Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        VaultTextField(value = password, onValueChange = { password = it }, label = stringResource(R.string.password), visualTransformation = PasswordVisualTransformation())
                        Spacer(modifier = Modifier.height(8.dp))
                        VaultTextField(value = confirm, onValueChange = { confirm = it }, label = stringResource(R.string.confirm_password), visualTransformation = PasswordVisualTransformation())
                    }
                }
                if (isBusy) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        color = Red, trackColor = LightRed,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                    Text(
                        stringResource(R.string.exporting_passwords), color = Red,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedButton(
                        onClick = onCancel, modifier = Modifier.weight(1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Red)
                    ) { Text(stringResource(R.string.cancel), color = Red) }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { onExport(path, password, confirm, encrypted) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) { Text(stringResource(R.string.export)) }
                }
            }
        }
    }
}

@Composable
fun OptionRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            RadioButton(
                selected = selected, onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Red)
            )
            Text(text, color = androidx.compose.ui.graphics.Color.Black, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImportPreview() {
    SecureVaultTheme(darkTheme = false) {
        ImportDialogContent(onBrowse = {}, onImport = { _, _, _ -> }, onCancel = {}, filePath = "", isBusy = false)
    }
}
