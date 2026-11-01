package domain.backup

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import data.crypto.AppKeyProvider
import data.json.crypto.FileEncryptor
import data.json.model.Password
import data.mapper.PasswordMapper
import data.repository.factory.PasswordRepositoryFactory
import data.storage.BackupStorage
import di.DispatcherProvider
import domain.usecases.sv.ReadSv
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backupStorage: BackupStorage,
    private val encryptor: FileEncryptor,
    private val readSv: ReadSv,
    private val passwordRepositoryFactory: PasswordRepositoryFactory,
    private val dispatchers: DispatcherProvider
) {
    companion object {
        private const val BACKUP_FILE_PREFIX = "securevault_backup_"
        private const val BACKUP_FILE_EXTENSION = ".sv"
        private const val MIME_TYPE = "application/octet-stream"
    }

    fun isBackupEnabled(): Boolean {
        return backupStorage.isBackupEnabled()
    }

    fun enableBackup() {
        backupStorage.enableBackup()
    }

    fun disableBackup() {
        backupStorage.disableBackup()
    }

    fun setBackupLocation(uri: Uri) {
        backupStorage.saveLocation(uri)
    }

    fun getBackupLocation(): Uri? {
        return backupStorage.getLocation()
    }

    suspend fun createBackupIfEnabled(passwords: List<Password>): Boolean {
        if (!backupStorage.isBackupEnabled()) {
            return false
        }

        val backupLocation = backupStorage.getLocation() ?: return false

        return createBackup(backupLocation, passwords)
    }

    suspend fun loadBackup(uri: Uri) {
        val passwords =
            readSv(uri, String(AppKeyProvider.getAppKey())).map { PasswordMapper.mapToEntity(it) }
        passwordRepositoryFactory.getPasswordRepository().insertAllPasswords(passwords)
    }

    private suspend fun createBackup(folderUri: Uri, passwords: List<Password>): Boolean =
        withContext(dispatchers.io) {
            try {
                val timestamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "$BACKUP_FILE_PREFIX$timestamp$BACKUP_FILE_EXTENSION"

                val docUri = DocumentsContract.buildDocumentUriUsingTree(
                    folderUri,
                    DocumentsContract.getTreeDocumentId(folderUri)
                )

                val fileUri = DocumentsContract.createDocument(
                    context.contentResolver,
                    docUri,
                    MIME_TYPE,
                    fileName
                ) ?: return@withContext false

                val appKey = String(AppKeyProvider.getAppKey())
                val encryptedData = encryptor.encryptPasswords(passwords, appKey)

                context.contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                    outputStream.write(encryptedData.toByteArray())
                    outputStream.flush()
                }

                return@withContext true
            } catch (_: Exception) {
                return@withContext false
            }
        }
}
