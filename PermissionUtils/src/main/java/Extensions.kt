import android.app.Activity
import android.support.v4.app.Fragment

fun Activity.requestPermission(vararg permissionNames: String, requestCode: Int, onAllGranted: () -> Unit, onAnyDenied: () -> Unit): PermissionUtil.PermissionRequestObject {
    return PermissionUtil.PermissionObject(this, null).request(*permissionNames).onAllGranted(onAllGranted).onAnyDenied(onAnyDenied).ask(requestCode)
}

fun Activity.requestPermission(permissionNames: String, requestCode: Int, onAllGranted: () -> Unit, onAnyDenied: () -> Unit): PermissionUtil.PermissionRequestObject {
    return PermissionUtil.PermissionObject(this, null).request(permissionNames).onAllGranted(onAllGranted).onAnyDenied(onAnyDenied).ask(requestCode)
}

fun Activity.requestPermission(vararg permissionNames: String, requestCode: Int, onGivenResult: (requestCode: Int, permissions: Array<String>, grantResults: Array<Int>) -> Unit): PermissionUtil.PermissionRequestObject {
    return PermissionUtil.PermissionObject(this, null).request(*permissionNames).onResult(onGivenResult).ask(requestCode)
}

fun Activity.hasPermission(permissionName: String): Boolean{
    return PermissionUtil.PermissionObject(this).has(permissionName)
}

fun Fragment.requestPermission(vararg permissionNames: String, requestCode: Int, onAllGranted: (() -> Unit)? = null, onAnyDenied: (() -> Unit)? = null): PermissionUtil.PermissionRequestObject {
    return PermissionUtil.PermissionObject(null, this).request(*permissionNames).onAllGranted { onAllGranted?.invoke() }.onAnyDenied { onAnyDenied?.invoke() }.ask(requestCode)
}

fun Fragment.requestPermission(permissionNames: String, requestCode: Int, onAllGranted: (() -> Unit)? = null, onAnyDenied: (() -> Unit)? = null): PermissionUtil.PermissionRequestObject {
    return PermissionUtil.PermissionObject(null, this).request(permissionNames).onAllGranted { onAllGranted?.invoke() }.onAnyDenied { onAnyDenied?.invoke() }.ask(requestCode)
}

fun Fragment.requestPermission(vararg permissionNames: String, requestCode: Int, onGivenResult: ((requestCode: Int, permissions: Array<String>, grantResults: Array<Int>) -> Unit)? = null): PermissionUtil.PermissionRequestObject {
    return PermissionUtil.PermissionObject(null, this).request(*permissionNames).onResult{ reqCode, permissions, grantResults ->
        onGivenResult?.invoke(reqCode,permissions,grantResults)
    }.ask(requestCode)
}

fun Fragment.hasPermission(permissionName: String): Boolean{
    return PermissionUtil.PermissionObject(null, this).has(permissionName)
}
