aerogear-android
================

This project is the core Aerogear library for Android.  It is build using Maven as an apk lib.

## Get Started With AeroGear on Android 

### Introduction

AeroGear's Android libraries are built as apklib projects using Maven and the [android-maven-plugin](http://code.google.com/p/maven-android-plugin/). The project follows the standard Android project layout so sources will be in /src instead of /src/main/java and can be imported directly into Eclipse as an Android project.

After the library has been built, we will clone and run the [TODO sample application](https://github.com/aerogear/aerogear-android-todo) to confirm it is working.

If you are unfamiliar with Maven, developerWorks has a detailed [introduction](http://www.ibm.com/developerworks/java/tutorials/j-mavenv2/).  This guide will cargo cult everything you need to build and test the AeroGear library and real familiarity isn't necessary.

### Android AeroGear Library

#### Prerequisites

* Java 6.0 (Java SDK 1.6)+
* Maven 3+
* Git
* Maven 3+
* Android SDK
* Need to have an AVD image running 2.3.3 (API level 10)
* You may use any IDE that supports Maven, but this guide currently focuses on the command line.

#### Setup Maven Android SDK Deployer

We need to have the [Android Maven SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer) installed and configured.  These are only quickstart instructions, the Android Maven SDK Deployer GitHub has a much more in depth install guide.

You must have the version of the platform installed through the Android SDK as you are using for your profile.  In this example, make sure Android 4.1 is installed.

    git clone https://github.com/mosabua/maven-android-sdk-deployer.git
    cd maven-android-sdk-deployer
    export ANDROID_HOME=YOUR_ANDROID_SDK_DIRECTORY
    mvn install -P 4.1


Now Maven will be able to include Android 4.1 as a dependency.  There are also profiles for other version of Android.


#### Installing aerogear-android

From the command line run the following.  This will clone the aerogear-android git repository (thus creating a aerogear-android directory) and then build the library and install it in your local maven repository.


    git clone https://github.com/aerogear/aerogear-android.git
    cd aerogear-android/
    mvn install


If all goes well you should have the project built and installed.  You can confirm the build was successful by looking into the target subdirectory of aerogear-android.  The library should be there in a jar file. If you have trouble see the troubleshooting section.
