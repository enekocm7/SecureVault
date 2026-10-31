package ui.compose.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import domain.model.PasswordDto
import ui.compose.components.VaultTextField
import ui.compose.theme.LightRed
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme
import ui.compose.theme.White

// 1:1 of create_password_dialog.xml
@Composable
fun CreatePasswordDialogContent(
    onSave: (PasswordDto) -> Unit,
    onCancel: () -> Unit,
    onGenerate: () -> Unit,
    show: Boolean = true
) {
    if (!show) return
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    stringResource(R.string.create_password).trim(),
                    color = Red, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                VaultTextField(value = name, onValueChange = { name = it }, label = stringResource(R.string.name))
                Spacer(modifier = Modifier.height(8.dp))
                VaultTextField(value = url, onValueChange = { url = it }, label = stringResource(R.string.url))
                Spacer(modifier = Modifier.height(8.dp))
                VaultTextField(value = username, onValueChange = { username = it }, label = stringResource(R.string.username))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VaultTextField(
                        value = password, onValueChange = { password = it },
                        label = stringResource(R.string.password),
                        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { visible = !visible }) {
                                Icon(painterResource(R.drawable.baseline_visibility_24), null, tint = Red)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = onGenerate,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Red, contentColor = White),
                        modifier = Modifier.size(48.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(painterResource(R.drawable.ic_generate), stringResource(R.string.generate_password))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Red)
                    ) { Text(stringResource(R.string.cancel), color = Red) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(PasswordDto(name = name, url = url.lowercase(), username = username, value = password))
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) { Text(stringResource(R.string.save)) }
                }
            }
        }
    }
}

// 1:1 of password_detail.xml
@Composable
fun PasswordDetailDialogContent(
    initial: PasswordDto,
    onSave: (PasswordDto) -> Unit,
    onDelete: (PasswordDto) -> Unit,
    onClose: () -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var url by remember { mutableStateOf(initial.url) }
    var username by remember { mutableStateOf(initial.username) }
    var password by remember { mutableStateOf(initial.value) }
    var visible by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    stringResource(R.string.password_details),
                    color = Red, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                VaultTextField(value = name, onValueChange = { name = it }, label = stringResource(R.string.name))
                Spacer(modifier = Modifier.height(8.dp))
                VaultTextField(value = url, onValueChange = { url = it }, label = stringResource(R.string.url))
                Spacer(modifier = Modifier.height(8.dp))
                VaultTextField(value = username, onValueChange = { username = it }, label = stringResource(R.string.username))
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
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onSave(PasswordDto(name, url.lowercase(), username, password)) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Red)
                ) { Text(stringResource(R.string.save)) }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { onDelete(PasswordDto(name, url.lowercase(), username, password)) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Red)
                ) { Text(stringResource(R.string.delete), color = Red) }
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

@Preview(showBackground = true)
@Composable
fun CreateDialogPreview() {
    SecureVaultTheme(darkTheme = false) {
        CreatePasswordDialogContent(onSave = {}, onCancel = {}, onGenerate = {})
    }
}
