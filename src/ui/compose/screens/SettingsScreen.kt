package ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.enekocm.securevault.R
import ui.compose.theme.LightRed
import ui.compose.theme.Gris
import ui.compose.theme.Red
import ui.compose.theme.SecureVaultTheme
import ui.compose.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userName: String,
    userEmail: String,
    photoUrl: String?,
    syncText: String,
    isSignedIn: Boolean,
    biometricChecked: Boolean,
    biometricEnabled: Boolean,
    backupEnabled: Boolean,
    backupLocationText: String?,
    showBackupDetails: Boolean,
    autofillChecked: Boolean,
    showBrowserSwitch: Boolean,
    browserChecked: Boolean,
    appVersion: String,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onChangeMaster: () -> Unit,
    onBiometricChange: (Boolean) -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onClear: () -> Unit,
    onBackupChange: (Boolean) -> Unit,
    onBackupLocation: () -> Unit,
    onCreateBackup: () -> Unit,
    onLoadBackup: () -> Unit,
    onAutofillChange: (Boolean) -> Unit,
    onBrowserChange: (Boolean) -> Unit,
    onPrivacy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Red)
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.title_activity_settings), color = White) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.back),
                        tint = White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Red)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightRed)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 50.dp)
        ) {
            SectionHeader(stringResource(R.string.account))
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (photoUrl != null) {
                            AsyncImage(
                                model = photoUrl, contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(64.dp).clip(androidx.compose.foundation.shape.CircleShape)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_account_circle),
                                contentDescription = null,
                                tint = Red,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(userName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(userEmail, fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_sync_disabled),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(syncText, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            if (isSignedIn) {
                WhiteButton(stringResource(R.string.sign_out), onSignOut)
            } else {
                RedButton(stringResource(R.string.sign_in_with_google), onSignIn)
            }

            SectionHeader(stringResource(R.string.pref_category_security), top = 24.dp)
            WhiteButton(stringResource(R.string.pref_title_change_master_password), onChangeMaster)
            SwitchRow(
                text = stringResource(R.string.pref_title_enable_biometrics),
                checked = biometricChecked,
                enabled = biometricEnabled,
                onChange = onBiometricChange
            )

            SectionHeader(stringResource(R.string.pref_category_data_management), top = 16.dp)
            WhiteButton(stringResource(R.string.import_passwords), onImport)
            WhiteButton(stringResource(R.string.export_passwords), onExport)
            WhiteButton(stringResource(R.string.pref_title_clear_passwords), onClear)
            SwitchRow(text = stringResource(R.string.backup), checked = backupEnabled, enabled = true, onChange = onBackupChange)
            if (showBackupDetails) {
                WhiteButton(stringResource(R.string.change_backup_location), onBackupLocation)
                WhiteButton(stringResource(R.string.create_backup), onCreateBackup)
                WhiteButton(stringResource(R.string.load_backup), onLoadBackup)
                backupLocationText?.let {
                    Text("${stringResource(R.string.file_location)} $it", color = Gris, modifier = Modifier.padding(top = 4.dp))
                }
            }

            SectionHeader(stringResource(R.string.pref_category_autofill_browser), top = 16.dp)
            SwitchRow(text = stringResource(R.string.pref_title_autofill_service), checked = autofillChecked, enabled = true, onChange = onAutofillChange)
            if (showBrowserSwitch) {
                SwitchRow(text = stringResource(R.string.pref_title_browser_connection), checked = browserChecked, enabled = true, onChange = onBrowserChange)
            }

            SectionHeader(stringResource(R.string.pref_category_application), top = 16.dp)
            WhiteButton(stringResource(R.string.pref_title_privacy_policy), onPrivacy)
            Text(
                text = "${stringResource(R.string.pref_title_app_version)}: $appVersion",
                color = Color.Gray, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(text: String, top: Dp = 8.dp) {
    Spacer(modifier = Modifier.height(top))
    Text(text, color = Red, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
}

@Composable
fun WhiteButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Red)
    ) { Text(text) }
}

@Composable
fun RedButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Red, contentColor = White)
    ) { Text(text) }
}

@Composable
fun SwitchRow(text: String, checked: Boolean, enabled: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = Color.Black, modifier = Modifier.weight(1f))
        Switch(
            checked = checked, enabled = enabled, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Red, checkedTrackColor = Red.copy(alpha = 0.5f),
                uncheckedThumbColor = White, uncheckedTrackColor = Color.Gray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SecureVaultTheme(darkTheme = false) {
        SettingsScreen(
            userName = "Not signed in", userEmail = "Sign in to sync", photoUrl = null,
            syncText = "Sync disabled", isSignedIn = false,
            biometricChecked = false, biometricEnabled = true,
            backupEnabled = false, backupLocationText = null, showBackupDetails = false,
            autofillChecked = false, showBrowserSwitch = false, browserChecked = false,
            appVersion = "2.0.1",
            onBack = {}, onSignIn = {}, onSignOut = {}, onChangeMaster = {},
            onBiometricChange = {}, onImport = {}, onExport = {}, onClear = {},
            onBackupChange = {}, onBackupLocation = {}, onCreateBackup = {}, onLoadBackup = {},
            onAutofillChange = {}, onBrowserChange = {}, onPrivacy = {}
        )
    }
}
