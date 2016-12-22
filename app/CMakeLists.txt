# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
             fastable-android-jni

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/jni/lib/ldb/ldb.cpp
             src/main/jni/src/dbscan.cpp
             src/main/jni/src/OpenABLE.cpp
             src/main/jni/fastable-android-jni.cpp)

# Specifies a path to native header files.
include_directories(src/main/jni/lib/ldb/
                    src/main/jni/src/)


find_library( # Defines the name of the path variable that stores the
              # location of the NDK library.
              log-lib

              # Specifies the name of the NDK library that
              # CMake needs to locate.
              log )



add_library(lib_opencv SHARED IMPORTED)

set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

include_directories(OpenCV-android-sdk/sdk/native/jni/include)



target_link_libraries( fastable-android-jni
                       ${log-lib}
                       lib_opencv)

