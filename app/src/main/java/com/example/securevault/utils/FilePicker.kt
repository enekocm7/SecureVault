package com.example.securevault.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResult

enum class FilePickerType {
    FILE, FOLDER
}

class FilePicker(
    private val activity: AppCompatActivity? = null,
    private val fragment: Fragment? = null,
    private val onFileSelected: (Uri) -> Unit
) {

    private val launcher: ActivityResultLauncher<Intent>

    init {
        val resultContract = ActivityResultContracts.StartActivityForResult()
        val callback = { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri: Uri ->
                    if (result.data?.flags != null && (result.data!!.flags.toLong() and Intent.FLAG_GRANT_READ_URI_PERMISSION.toLong()) != 0L) {
                         val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                 Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        (activity?.contentResolver ?: fragment?.context?.contentResolver)?.takePersistableUriPermission(uri, takeFlags)
                    }
                    onFileSelected(uri)
                }
            }
        }

        launcher = when {
            activity != null -> activity.registerForActivityResult(resultContract, callback)
            fragment != null -> fragment.registerForActivityResult(resultContract, callback)
            else -> error("Either activity or fragment must be provided")
        }
    }

    fun launch(type: FilePickerType, mimeType: String? = null) {
        val intent = when (type) {
            FilePickerType.FILE -> Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                this.type = mimeType ?: "*/*"
            }
            FilePickerType.FOLDER -> Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
        }
        launcher.launch(intent)
    }
}
