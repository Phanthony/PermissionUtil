package com.github.kayvannj.permission_utils.kotlinupdate

/**
 * Created by kayvan on 10/27/15.
 * Import to Kotlin by Phanthony on 4/13/20
 */
class SinglePermission(private var mPermissionName: String, private var mReason: String? = null) {

    private var mRationalNeeded = false

    fun isRationalNeeded(): Boolean = mRationalNeeded

    fun setRationalNeeded(rationalNeeded: Boolean){
        mRationalNeeded = rationalNeeded
    }

    fun getReason(): String = mReason?: ""

    fun setReason(reason: String){
        mReason = reason
    }

    fun getPermissionName(): String = mPermissionName

    fun setPermissionName(permissionName: String){
        mPermissionName = permissionName
    }
}