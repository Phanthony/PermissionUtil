package com.kayvan.permissionutil

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import butterknife.ButterKnife
import PermissionUtil
import hasPermission
import requestPermission

class MainActivity : AppCompatActivity() {
    val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS
    private val REQUEST_CODE_CONTACTS = 1
    private val REQUEST_CODE_STORAGE = 2
    private val REQUEST_CODE_BOTH = 3
    lateinit var mStatus: TextView
    var mBothPermissionRequest: PermissionUtil.PermissionRequestObject? = null
    var mStoragePermissionRequest: PermissionUtil.PermissionRequestObject? = null
    var mContactsPermissionRequest: PermissionUtil.PermissionRequestObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mStatus = findViewById(R.id.status)
        findViewById<Button>(R.id.storage_check).setOnClickListener {
            onCheckStoragePermissionClick()
        }
        findViewById<Button>(R.id.contacts_check).setOnClickListener {
            onCheckContactsPermissionClick()
        }
        findViewById<Button>(R.id.contacts).setOnClickListener {
            onAskForContactsPermissionClick()
        }
        findViewById<Button>(R.id.storage).setOnClickListener {
            onAskForStoragePermissionClick()
        }
        findViewById<Button>(R.id.both).setOnClickListener {
            onAskBothPermissionsClick()
        }
        ButterKnife.bind(this)
    }

    fun onCheckStoragePermissionClick() {
        val hasStoragePermission = hasPermission(WRITE_EXTERNAL_STORAGE)
        updateStatus(if (hasStoragePermission) "Has Storage permission" else "Doesn't have Storage permission")
    }

    private fun updateStatus(s: String) {
        mStatus.setText(String.format("> %s\n", s) + mStatus.getText().toString())
    }

    fun onCheckContactsPermissionClick() {
        val hasContactsPermission = hasPermission(WRITE_CONTACTS)
        updateStatus(if (hasContactsPermission) "Has Contacts permission" else "Doesn't have Contacts permission")
    }

    fun onAskForStoragePermissionClick() {
       mStoragePermissionRequest = requestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CODE_STORAGE,
                onAllGranted = {
                    doOnPermissionGranted("Storage")
                },
                onAnyDenied = {
                    doOnPermissionDenied("Storage")
                })
    }

    private fun doOnPermissionDenied(permission: String) {
        updateStatus("$permission Permission Denied or is on \"Do Not Show Again\"")
    }

    private fun doOnPermissionGranted(permission: String) {
        updateStatus("$permission Permission Granted")
    }

    fun onAskBothPermissionsClick() {
        mBothPermissionRequest = requestPermission(WRITE_EXTERNAL_STORAGE, WRITE_CONTACTS, requestCode = REQUEST_CODE_BOTH,
                onGivenResult = { requestCode, permissions, grantResults ->
                    for (i in permissions.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            doOnPermissionGranted(permissions[i])
                        } else {
                            doOnPermissionDenied(permissions[i])
                        }
                    }
                }
        )
    }

    fun onAskForContactsPermissionClick() {
       mContactsPermissionRequest = requestPermission(WRITE_CONTACTS, REQUEST_CODE_CONTACTS,
                onAllGranted = {
                    doOnPermissionGranted("Contacts")
                },
                onAnyDenied = {
                    doOnPermissionDenied("Contacts")
                }
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mStoragePermissionRequest?.onRequestPermissionsResult(requestCode, permissions, grantResults.toTypedArray())
        mContactsPermissionRequest?.onRequestPermissionsResult(requestCode, permissions, grantResults.toTypedArray())
        mBothPermissionRequest?.onRequestPermissionsResult(requestCode, permissions, grantResults.toTypedArray())
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}