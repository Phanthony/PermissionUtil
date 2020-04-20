package com.github.kayvannj.permission_utils.kotlinupdate

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.requestPermission(vararg permissionNames: String): PermissionUtil.PermissionRequestObject{
    val request = PermissionUtil.PermissionObject(this,null).request(*permissionNames)
    return request
}

fun AppCompatActivity.hasPermission(permissionName: String): Boolean{
    val request = PermissionUtil.PermissionObject(this).has(permissionName)
    return request
}

fun Fragment.requestPermission(vararg permissionNames: String): PermissionUtil.PermissionRequestObject{
    val request = PermissionUtil.PermissionObject(null,this).request(*permissionNames)
    return request
}

fun Fragment.hasPermission(permissionName: String): Boolean{
    val request = PermissionUtil.PermissionObject(null,this).has(permissionName)
    return request
}
