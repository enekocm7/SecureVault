package di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import data.csv.formatter.CsvFormatter
import data.csv.storage.CsvStorage
import data.json.crypto.FileEncryptor
import data.json.storage.PasswordStorage
import data.repository.FirebasePasswordRepository
import data.repository.LocalPasswords
import data.repository.MasterPasswordRepositoryImpl
import data.repository.PasswordRepositoryImpl
import data.repository.factory.PasswordRepositoryFactory
import data.storage.AppKeyStorage
import data.storage.BackupStorage
import data.storage.FirebaseStorage
import domain.backup.BackupManager
import domain.repository.MasterPasswordRepository
import domain.usecases.EstimatePassword
import domain.usecases.auth.AuthenticateBiometrics
import domain.usecases.auth.GenerateAppKey
import domain.usecases.auth.GenerateBiometricKey
import domain.usecases.auth.GetDecryptCryptoObject
import domain.usecases.auth.GetEncryptCryptoObject
import domain.usecases.auth.IsAppKeyConfigured
import domain.usecases.auth.IsBiometricConfigured
import domain.usecases.auth.UnlockKeyWithBiometrics
import domain.usecases.auth.UnlockKeyWithPassword
import domain.usecases.csv.ReadCsv
import domain.usecases.csv.WriteCsv
import domain.usecases.firestore.GetModel
import domain.usecases.firestore.ModelToPreferences
import domain.usecases.firestore.UnlockKeyWithFirebase
import domain.usecases.generator.GeneratePassphrase
import domain.usecases.generator.GeneratePassword
import domain.usecases.password.AddPassword
import domain.usecases.password.DeleteAllPasswords
import domain.usecases.password.DeletePassword
import domain.usecases.password.GetAllPasswords
import domain.usecases.password.GetPasswordsByName
import domain.usecases.password.GetPasswordsFromEncrypted
import domain.usecases.password.InsertAllPasswords
import domain.usecases.sv.ReadSv
import domain.usecases.sv.WriteSv
import javax.inject.Provider
import me.gosimple.nbvcxz.Nbvcxz

/**
 * Manual service locator replacing Hilt (which needs a Gradle plugin that
 * Kargo's generated Gradle project does not apply).
 *
 * Scoping mirrors the old Hilt graph: stateless helpers are singletons,
 * stateful password caches are rebuilt per repository (as Hilt's unscoped
 * bindings did), and repositories are built lazily inside providers so the
 * file/network I/O in their init blocks still runs on background threads.
 *
 * Firebase is nullable: without the google-services Gradle plugin there is
 * no FirebaseApp, so cloud features degrade to offline instead of crashing.
 * Anything below that touches Firebase only runs when [firebaseAuth] is
 * non-null (same condition Hilt builds implicitly relied on).
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    // --- primitives ---
    val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val nbvcxz: Nbvcxz = Nbvcxz()

    // Null without google-services config (Kargo applies no google-services
    // Gradle plugin, so there is no FirebaseApp to get instances from).
    val firebaseAuth: FirebaseAuth? = try {
        Firebase.auth
    } catch (_: Exception) {
        null
    }
    private val firestore: FirebaseFirestore? = try {
        Firebase.firestore
    } catch (_: Exception) {
        null
    }

    // --- stateless helpers (singletons) ---
    val appKeyStorage: AppKeyStorage = AppKeyStorage(appContext)
    val backupStorage: BackupStorage = BackupStorage(appContext)
    val csvFormatter: CsvFormatter = CsvFormatter()
    val fileEncryptor: FileEncryptor = FileEncryptor(moshi)
    val firebaseStorage: FirebaseStorage? = firestore?.let { FirebaseStorage(it) }
    val masterPasswordRepository: MasterPasswordRepository =
        MasterPasswordRepositoryImpl(appKeyStorage)

    // --- repositories (built per use, like Hilt's unscoped bindings) ---
    val passwordRepositoryFactory: PasswordRepositoryFactory by lazy {
        PasswordRepositoryFactory(
            firebasePasswordRepositoryProvider = Provider {
                // Only called when firebaseAuth?.currentUser != null, which
                // implies FirebaseApp initialized, so both are non-null here.
                FirebasePasswordRepository(
                    firebaseStorage!!,
                    LocalPasswords(),
                    appKeyStorage,
                    fileEncryptor,
                    firebaseAuth!!
                )
            },
            localPasswordRepositoryProvider = Provider {
                PasswordRepositoryImpl(
                    PasswordStorage(appContext),
                    fileEncryptor,
                    LocalPasswords()
                )
            },
            firebaseAuth = firebaseAuth
        )
    }

    // --- csv / sv / storage use-cases ---
    val csvStorage: CsvStorage = CsvStorage(appContext, csvFormatter)
    val writeCsv: WriteCsv = WriteCsv(csvStorage)
    val readCsv: ReadCsv = ReadCsv(csvStorage)
    val writeSv: WriteSv = WriteSv(fileEncryptor, appContext)
    val readSv: ReadSv = ReadSv(fileEncryptor, appContext)

    // --- password use-cases ---
    val addPassword: AddPassword = AddPassword(passwordRepositoryFactory)
    val deletePassword: DeletePassword = DeletePassword(passwordRepositoryFactory)
    val deleteAllPasswords: DeleteAllPasswords = DeleteAllPasswords(passwordRepositoryFactory)
    val getAllPasswords: GetAllPasswords = GetAllPasswords(passwordRepositoryFactory)
    val getPasswordsByName: GetPasswordsByName = GetPasswordsByName(passwordRepositoryFactory)
    val getPasswordsFromEncrypted: GetPasswordsFromEncrypted =
        GetPasswordsFromEncrypted(fileEncryptor)
    val insertAllPasswords: InsertAllPasswords = InsertAllPasswords(passwordRepositoryFactory)

    // --- auth use-cases ---
    val isAppKeyConfigured: IsAppKeyConfigured = IsAppKeyConfigured(masterPasswordRepository)
    val isBiometricConfigured: IsBiometricConfigured =
        IsBiometricConfigured(masterPasswordRepository)
    val generateAppKey: GenerateAppKey = GenerateAppKey(masterPasswordRepository)
    val generateBiometricKey: GenerateBiometricKey =
        GenerateBiometricKey(masterPasswordRepository)
    val unlockKeyWithPassword: UnlockKeyWithPassword =
        UnlockKeyWithPassword(masterPasswordRepository)
    val unlockKeyWithBiometrics: UnlockKeyWithBiometrics =
        UnlockKeyWithBiometrics(masterPasswordRepository)
    val unlockKeyWithFirebase: UnlockKeyWithFirebase =
        UnlockKeyWithFirebase(masterPasswordRepository)
    val authenticateBiometrics: AuthenticateBiometrics = AuthenticateBiometrics()
    val getEncryptCryptoObject: GetEncryptCryptoObject = GetEncryptCryptoObject()
    val getDecryptCryptoObject: GetDecryptCryptoObject =
        GetDecryptCryptoObject(masterPasswordRepository)
    val estimatePassword: EstimatePassword = EstimatePassword(nbvcxz)

    // --- generator use-cases ---
    val generatePassword: GeneratePassword = GeneratePassword()
    val generatePassphrase: GeneratePassphrase = GeneratePassphrase()

    // --- firestore use-cases (null-safe: only used in Firebase paths) ---
    val getModel: GetModel = GetModel(firebaseStorage, firebaseAuth)
    val modelToPreferences: ModelToPreferences = ModelToPreferences(firebaseStorage)

    // --- backup ---
    val backupManager: BackupManager by lazy {
        BackupManager(
            appContext,
            backupStorage,
            fileEncryptor,
            readSv,
            passwordRepositoryFactory,
            dispatchers
        )
    }
}
