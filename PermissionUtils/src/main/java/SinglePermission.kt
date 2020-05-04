/**
 * Created by kayvan on 10/27/15.
 * Import to Kotlin by Phanthony on 4/13/20
 */
class SinglePermission(private var mPermissionName: String, private var mReason: String? = null) {

    var mRationalNeeded: Boolean = false
    var isRationalNeeded: Boolean
    get() {
        return mRationalNeeded
    }
    set(value) {
        mRationalNeeded = value
    }

    var permissionName: String
    get(){
        return mPermissionName
    }
    set(value) {
        mPermissionName = value
    }

    var reason: String
    get() {
        return mReason?: ""
    }
    set(value){
        mReason = value
    }
}