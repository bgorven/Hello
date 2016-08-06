OSX & Linux libs: [![Build Status](https://travis-ci.org/bgorven/Hello.svg?branch=develop)](https://travis-ci.org/bgorven/Hello)

Windows libs: [![Windows Build](https://buddygorven.visualstudio.com/_apis/public/build/definitions/35dfd6bd-527b-44e5-bb54-1215b7cc5fbf/2/badge)](https://buddygorven.visualstudio.com/Hello)

Final product: [![Build Status](https://travis-ci.org/bgorven/Hello.svg?branch=master)](https://travis-ci.org/bgorven/Hello)

Creating a new project with this template:

[ ] change nativeClass property of JNILibrarySpec component
[ ] optionally, rename JNILibrarySpec component (e.g for `nativeClass "com.example.FooBar"`, `'native'(JNILibrarySpec)` -> `fooBar(JNILibrarySpec)`)
[ ] if you renamed the component, move the native sources (e.g `mv src/native/ src/fooBar`)
[ ] if you have more native classes, create additional components and source dirs.
[ ] run `./gradlew nativeJavah` (or fooBarJavah, etc) to generate headers from your java class
[ ] gradle build puts it all together (also runs all javah tasks, to ensure headers are up to date)
[ ] for multiplatform ci, you can use travis for linux and osx, and visualstudio.com for windows
[ ] create an s3 bucket to hold native libs
[ ] add 'List' permissions for Everyone
[ ] update bucket policy to allow global public downloads http://stackoverflow.com/a/19177423
[ ] update aws_bucket var in build.gradle
[ ] generate [AWS access keys](https://console.aws.amazon.com/iam/home?#security_credential)
[ ] update .travis.yml env.global keys
[ ] echo 'AWS_SECRET_KEY={your secret key}' | travis encrypt --add
[ ] create project on visualstudio online (gradle build with `upload` task and `-Pvs2015` option), building off develop branch
[ ] add env vars to visualstudio project on Variables tab
[ ] optionally, enable badge on General tab, and update readme

The idea is that ci will run on your develop branch, and upload compiled native libraries to s3 under the commit hash.
Then when you merge to master, as long as it's a fast-forward merge, it will have the same commit hash, thus builds on that hash will download the correct binaries.

So the process is:

- commit changes to develop
- push to github
- confirm that all ci builds succeed
- git checkout master && git merge develop --ff-only && git push
- libs will automatically be downloaded and packed into the main jar, wherever master gets built
