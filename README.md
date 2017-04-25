# FastABLE

FastABLE is an accelerated and modified version of OpenABLE that suits the needs of indoor mobile localization. OpenABLE that is an open toolbox that contributes different solutions and functionalities to the research community in the topic of life-long visual localization for autonomous vehicles. The original OpenABLE: https://github.com/roberto-arroyo/OpenABLE

The code is made available under the BSD license. In case of using the software in your own work, we kindly ask you to consider citing the following papers:

FastABLE:
* [1] M. Nowicki, J. Wietrzykowski and P. Skrzypczyński, ``Real-Time Visual Place Recognition for Personal Localization on a Mobile Device'', Wireless Personal Communication, 2017 (waiting for acceptance after minor revision)

Comparison between OpenABLE and FAB-MAP for indoor localization:
* [2] M. Nowicki, J. Wietrzykowski and P. Skrzypczyński, ``Experimental Evaluation of Visual Place Recognition Algorithms for Personal Indoor Localization'', in International Conference on Indoor Positioning and Indoor Navigation (IPIN 2016), p. 1-8, Alcala de Henares, Spain, 2016.

The FastABLE is heavily based on OpenABLE and therefore we ask you to also cite the original OpenABLE work:

* [3] R. Arroyo, L. M. Bergasa and E. Romera, "OpenABLE: An Open-source Toolbox for Application in Life-Long Visual Localization of Autonomous Vehicles", submitted to Intelligent Transportation Systems Conference (ITSC), Rio de Janeiro (Brazil), November 2016 (in review process).

* [4] R. Arroyo, P. F. Alcantarilla, L. M. Bergasa and E. Romera, "Towards Life-Long Visual Localization using an Efficient Matching of Binary Sequences from Images", in IEEE International Conference on Robotics and Automation (ICRA), pp. 6328-6335, Seattle, Washington (United States), May 2015.

* [5] R. Arroyo, P. F. Alcantarilla, L. M. Bergasa, J. J. Yebes and S. Bronte, "Fast and Effective Visual Place Recognition using Binary Codes and Disparity Information", in IEEE/RSJ International Conference on Intelligent Robots and Systems (IROS), pp. 3089-3094, Chicago, Illinois (United States), September 2014.

* [6] R. Arroyo, P. F. Alcantarilla, L. M. Bergasa, J. J. Yebes and S. Gámez, "Bidirectional Loop Closure Detection on Panoramas for Visual Navigation", in IEEE Intelligent Vehicles Symposium (IV), pp. 1378-1383, Dearborn, Michigan (United States), June 2014.

The PC version of the FastABLE is available at https://github.com/LRMPUT/FastABLE

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

