# FastABLE_Android

To run code on your mobile device:  
1. Clone repository to your local _AndroidStudioProjects_ folder.
2. Launch Android Studio, choose _Open an existing Android Studio project_ and select cloned project.  
3. Select proper build variant (_Build_ > _Select Build Variant.._) depending on an architecture of your device (arm-v7, x86, etc.).  
4. Place training and test data in a _FastABLE_ folder on device's external storage. Test images should be stored in a _FastABLE/test_ directory, training images should be stored in a _FastABLE/train/xxx_ directories, where _xxx_ are separate folders for each training segment.  
5. Place _config.txt_ file in _FastABLE_ directory.  


### NOTE:
Since NDK support in Android Studio is still experimental, if you get errors like:
```
 Error retrieving parent for item: No resource found that matches the given name after...
```
then change the following fragment in file _build.gradle (Module: app)_:
```
...
        ndk {
            compileSdkVersion 19
            moduleName "fastable-android-jni"
...
```
under _model, android, ndk_ to:
```
...
        ndk {
            compileSdkVersion 23
            moduleName "fastable-android-jni"
...
```
run _Build_ > _Make Project_, change _compileSdkVersion_ back to 19 and make project again.

