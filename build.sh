export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/root/Android/Sdk

rm -f sign.apk

chmod +x gradlew
./gradlew assembleRelease

/root/Android/Sdk/build-tools/34.0.0/apksigner sign --ks caixxxin.keystore --ks-pass pass:"12345678" --in tv/build/outputs/apk/release/mytv-android-tv-2.2.1-all-sdk21.apk --out sign.apk

rm -f sign.apk.idsig