# PermissionUtil
[ ![Download](https://api.bintray.com/packages/kayvannj/maven/PermissionUtil/images/download.svg) ](https://bintray.com/kayvannj/maven/PermissionUtil/_latestVersion)
### What is this?
A simple wrapper around Android 6.0 runtime permission api
### Why?
Adding runtime permissions is not hard but having to seperate your code and move the methods around to capture callbacks is a little pain. This library provides a chained api to do all you need to do for supporting runtime permissions.

### How?
Anywhere in your ```AppCompatActivity``` or ```Fragment``` that you want to ask for user's permisssion you can define functions for all permissions granted or on any failure
```kotlin
// REQUEST_CODE_STORAGE is what ever int you want (should be distinct)
mRequestObject = requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_STORAGE,
                onAllGranted = {
                        //Happy Path
                },
                onAnyDenied = {
                        //Sad Path
                }
)
```
OR you can define 1 function that handles all results
```kotlin
mRequestObject = requestPermission(WRITE_EXTERNAL_STORAGE, WRITE_CONTACTS, requestCode = REQUEST_CODE_BOTH,
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
```
And add this to ```onRequestPermissionsResult()```
```java
mRequestObject.onRequestPermissionsResult(requestCode, permissions, grantResults);
```
^ NOTE: make sure to check for `null`

Add the requested permission to your ```AndroidManifest.xml``` as well
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

Done.

### Dependency?
```groovy
compile 'com.android.support:appcompat-v7:25.3.1'
```
### Download
```groovy
compile 'com.github.kayvannj:PermissionUtils:1.0.9'
```


License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


