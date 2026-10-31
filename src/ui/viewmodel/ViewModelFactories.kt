package ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.enekocm.securevault.SecureVaultApp
import ui.viewmodel.dialogs.CreatePasswordViewModel
import ui.viewmodel.dialogs.ExportPasswordViewModel
import ui.viewmodel.dialogs.GeneratePasswordViewModel
import ui.viewmodel.dialogs.ImportPasswordViewModel
import ui.viewmodel.dialogs.PasswordDetailViewModel

private fun containerOf(app: Application) = (app as SecureVaultApp).container

private inline fun <reified T : ViewModel> factory(crossinline build: () -> T) =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <M : ViewModel> create(modelClass: Class<M>): M = build() as M
    }

// --- screens ---

fun mainFactory(app: Application) = factory {
    MainViewModel(containerOf(app).isAppKeyConfigured)
}

fun registerFactory(app: Application) = factory {
    val c = containerOf(app)
    RegisterViewModel(c.estimatePassword, c.generateAppKey, c.addPassword, c.deletePassword, c.isAppKeyConfigured)
}

fun loginFactory(app: Application) = factory {
    val c = containerOf(app)
    LoginViewModel(
        app,
        c.unlockKeyWithPassword,
        c.unlockKeyWithBiometrics,
        c.unlockKeyWithFirebase,
        c.isBiometricConfigured,
        c.authenticateBiometrics,
        c.getDecryptCryptoObject,
        c.modelToPreferences,
        c.getModel,
        c.getAllPasswords,
        c.dispatchers,
        c.firebaseAuth
    )
}

fun homeFactory(app: Application) = factory {
    val c = containerOf(app)
    HomeViewModel(c.getAllPasswords, c.getPasswordsByName, c.firebaseAuth, c.dispatchers)
}

fun biometricFactory(app: Application) = factory {
    val c = containerOf(app)
    BiometricViewModel(app, c.generateBiometricKey, c.authenticateBiometrics, c.getEncryptCryptoObject)
}

fun cloudFactory(app: Application) = factory {
    CloudViewModel(containerOf(app).firebaseAuth)
}

fun settingsFactory(app: Application) = factory {
    val c = containerOf(app)
    SettingsViewModel(c.isBiometricConfigured, c.deleteAllPasswords, c.backupManager, c.getAllPasswords, c.dispatchers)
}

// --- dialogs ---

fun createPasswordFactory(app: Application) = factory {
    CreatePasswordViewModel(containerOf(app).addPassword)
}

fun passwordDetailFactory(app: Application) = factory {
    val c = containerOf(app)
    PasswordDetailViewModel(c.addPassword, c.deletePassword)
}

fun generatePasswordFactory(app: Application) = factory {
    val c = containerOf(app)
    GeneratePasswordViewModel(c.generatePassword, c.generatePassphrase, c.estimatePassword, c.dispatchers)
}

fun importPasswordFactory(app: Application) = factory {
    val c = containerOf(app)
    ImportPasswordViewModel(c.getPasswordsFromEncrypted, c.readCsv, c.insertAllPasswords, c.dispatchers)
}

fun exportPasswordFactory(app: Application) = factory {
    val c = containerOf(app)
    ExportPasswordViewModel(app, c.writeCsv, c.writeSv, c.getAllPasswords, c.dispatchers)
}
