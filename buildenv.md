
```` txt

###########################
# 下载安装sdk34和build tool 34.0.0
###########################
cd /root/Android/Sdk/cmdline-tools/latest/bin
./sdkmanager --install platforms\;android-34
./sdkmanager --install build-tools\;34.0.0
./sdkmanager --install sources\;android-34

###########################
# 加载环境变量
###########################
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/root/Android/Sdk

###########################
# build项目
###########################
cd xxx
./gradlew assembleRelease

###########################
# apk签名
###########################
keytool -genkey -v -keystore caixxxin.keystore -alias caixxxin -storepass 12345678 -keypass 12345678 -keyalg RSA -keysize 2048 -validity 36500 -dname "CN=caixxxin, OU=Github, O=caixxxin, L=Github, S=Github, C=China"

/root/Android/Sdk/build-tools/34.0.0/apksigner sign --ks caixxxin.keystore --ks-pass pass:"12345678" --in app/build/outputs/apk/release/app-release-unsigned.apk --out sign.apk

````