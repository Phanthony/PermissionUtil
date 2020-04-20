package com.github.kayvannj.permission_utils.kotlinupdate

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log


/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Created by kayvan on 10/26/15.
 * Import to Kotlin by Phanthony on 4/13/20
 */

class PermissionUtil {

    class PermissionObject(val mActivity: AppCompatActivity? = null, val mFragment: Fragment? = null) {

        fun has(permissionName: String): Boolean {
            val permissionCheck = if (mActivity == null) {
                ContextCompat.checkSelfPermission(mFragment!!.requireContext(), permissionName)
            } else {
                ContextCompat.checkSelfPermission(mActivity, permissionName)
            }
            return permissionCheck == PackageManager.PERMISSION_GRANTED
        }

        fun request(vararg permissionNames: String): PermissionRequestObject {
            return if (mActivity != null) {
                PermissionRequestObject(mActivity = mActivity, mFragment = null, mPermissionNames = arrayOf(*permissionNames))
            } else {
                PermissionRequestObject(mActivity = null, mFragment = mFragment, mPermissionNames = arrayOf(*permissionNames))
            }
        }
    }

    class PermissionRequestObject(val mActivity: AppCompatActivity? = null, val mFragment: Fragment? = null, var mPermissionNames: Array<String>) {

        val TAG = PermissionObject::class.simpleName
        var mRequestCode: Int = Int.MIN_VALUE
        var mPermissionsWeDontHave = arrayListOf<SinglePermission>()
        var mGrantFunc: (() -> Unit)? = null
        var mDenyFunc: (() -> Unit)? = null
        var mRationalFunc: ((permissionName: String) -> Unit)? = null
        var mResultFunc: ((requestCode: Int, permissions: Array<String>, grantResults: Array<Int>) -> Unit)? = null


        fun ask(reqCode: Int): PermissionRequestObject {
            mRequestCode = reqCode
            val length = mPermissionNames.size
            for (permission in mPermissionNames) {
                mPermissionsWeDontHave.add(SinglePermission(permission))
            }

            if (needToAsk()) {
                Log.i(TAG, "Asking for permission")
                if (mActivity != null) {
                    ActivityCompat.requestPermissions(mActivity, mPermissionNames, reqCode)
                } else {
                    mFragment!!.requestPermissions(mPermissionNames, reqCode)
                }
            } else {
                Log.i(TAG, "No need to ask for permission")
                if (mGrantFunc != null) {
                    mGrantFunc!!()
                }
            }
            return this
        }

        private fun needToAsk(): Boolean {
            val permissionsStillNeeded = arrayListOf<SinglePermission>()
            for (i in mPermissionsWeDontHave.indices) {
                val perm = mPermissionsWeDontHave[i]
                val checkRes = if (mActivity != null) {
                    ContextCompat.checkSelfPermission(mActivity, perm.getPermissionName())
                } else {
                    ContextCompat.checkSelfPermission(mFragment!!.requireContext(), perm.getPermissionName())
                }
                if (checkRes != PackageManager.PERMISSION_GRANTED) {
                    permissionsStillNeeded.add(perm)
                    val shouldShowRequestPermissionRationale = if (mActivity != null) {
                        ActivityCompat.shouldShowRequestPermissionRationale(mActivity, perm.getPermissionName())
                    } else {
                        mFragment!!.shouldShowRequestPermissionRationale(perm.getPermissionName())
                    }
                    if (shouldShowRequestPermissionRationale) {
                        perm.setRationalNeeded(true)
                    }
                }
            }
            mPermissionsWeDontHave = permissionsStillNeeded
            mPermissionNames = Array(permissionsStillNeeded.size) { permissionsStillNeeded[it].getPermissionName() }
            return mPermissionsWeDontHave.size != 0
        }

        /**
         * Called for the first denied permission if there is need to show the rational
         */
        fun onRational(rationalFunc: (permissionName: String) -> Unit): PermissionRequestObject {
            mRationalFunc = rationalFunc
            return this
        }

        /**
         * Called if all the permissions were granted
         */
        fun onAllGranted(grantFunc: () -> Unit): PermissionRequestObject {
            mGrantFunc = grantFunc
            return this
        }

        /**
         * Called if there is at least one denied permission
         */
        fun onAnyDenied(denyFunc: () -> Unit): PermissionRequestObject {
            mDenyFunc = denyFunc
            return this
        }

        /**
         * Called with the original operands from {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])
         * onRequestPermissionsResult} for any result
         */
        fun onResult(resultFunc: (requestCode: Int, permissions: Array<String>, grantResults: Array<Int>) -> Unit): PermissionRequestObject {
            mResultFunc = resultFunc
            return this
        }

        /**
         * This Method should be called from {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])
         * onRequestPermissionsResult} with all the same incoming operands
         * <pre>
         * {@code
         *
         * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
         *      if (mStoragePermissionRequest != null)
         *          mStoragePermissionRequest.onRequestPermissionsResult(requestCode, permissions,grantResults);
         * }
         * }
         * </pre>
         */
        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: Array<Int>) {
            if (mRequestCode == requestCode) {
                if (mResultFunc != null) {
                    Log.i(TAG, "Calling Results Func");
                    mResultFunc!!(requestCode, permissions, grantResults)
                    return
                }
            }

            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (mPermissionsWeDontHave[i].isRationalNeeded() && mRationalFunc != null) {
                        Log.i(TAG, "Calling Rational Func")
                        mRationalFunc!!(mPermissionsWeDontHave[i].getPermissionName())
                    } else if (mDenyFunc != null) {
                        Log.i(TAG, "Calling Deny Func")
                        mDenyFunc!!()
                    } else {
                        Log.e(TAG, "NULL DENY FUNCTIONS")
                    }

                    // terminate if there is at least one deny
                    return;
                }
            }

            // there has not been any denies
            if (mGrantFunc != null) {
                Log.i(TAG, "Calling Grant Func");
                mGrantFunc!!()
            } else {
                Log.e(TAG, "NULL GRANT FUNCTIONS")
            }
        }
    }
}