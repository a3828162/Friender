# Friender

## Introduction
這是一個結合了匿名聊天跟交友的聊天 App
![Imgur](https://github.com/a3828162/Friender/blob/main/picture/5.png)
![Imgur](https://github.com/a3828162/Friender/blob/main/picture/6.png)
![Imgur](https://github.com/a3828162/Friender/blob/main/picture/7.png)
![Imgur](https://github.com/a3828162/Friender/blob/main/picture/8.png)

## Apk link
<a href="https://drive.google.com/file/d/1soROmngn-LKTB6i8ZeDQUUv68UHyi931/view?usp=sharing">Friender</a>

## Features
* Create Account
  * Register Mail
  * Reset Password

* Chat
  * Meet Call, Phone Call
  * Random Pairing
  * Friend
  * Group
  * Flower
  * Notification
  * Send Photo
  * Unsend message
  * Group Note

* Profile
  * Profile Image
  * Friend Profile, Group Profile
  * Light, Dark Mode

## Dependancy
以下是我用到的 SDK
```
- implementation 'com.google.firebase:firebase-auth:21.0.3'
- implementation 'com.google.firebase:firebase-database:20.0.4'
- implementation 'de.hdodenhof:circleimageview:2.2.0'
- implementation 'com.github.bumptech.glide:glide:4.8.0'
- implementation 'com.google.firebase:firebase-storage:20.0.1'
- implementation 'com.google.android.gms:play-services-auth:19.2.0'
- implementation 'com.squareup.retrofit2:retrofit:2.3.0'
- implementation 'com.squareup.retrofit2:converter-scalars:2.3.0'
- implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
- implementation 'com.google.firebase:firebase-messaging:23.0.3'
- testImplementation 'junit:junit:4.+'
- androidTestImplementation 'androidx.test.ext:junit:1.1.3'
- androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
- implementation ('org.jitsi.react:jitsi-meet-sdk:3.+') { transitive = true }
```
* firebase 的 dependency 在連接到 firebase 時會有選項新增<br>
* 如果在 build 的過程中有錯可能是你 android studio build tool 版本問題或套件版本問題，可以去 stackoverflow 找答案，或是看官方 document
## Connect to Firebase

### Step1
到 <a href="https://firebase.google.com/?">https://firebase.google.com/</a> 點 Go to Console
![Imgur](https://github.com/a3828162/Friender/blob/main/picture/1.png)
### Step2
創建專案，按照步驟去做
![Imgur](https://github.com/a3828162/Friender/blob/main/picture/2.png)
### Step3
點 Tool -> Firebase
![Imgur](https://github.com/a3828162/Friender/blob/main/picture/3.png)
### Step4
點選你要的功能，我有用FCM、Auth、RealtimeDB
* 按 connect your app 會跳轉到 firebase project 頁面，選你剛剛創建的專案
* 按 add SDK 會幫你把用到的 SDK 版本加進 dependancy
![Imgur](https://github.com/a3828162/Friender/blob/main/picture/4.png)

## Remind
icon 是同組的組員畫的，因此若使用本 project 的 icon 造成法律糾紛請自行負責

## Contact
* Server 資料夾是我 reset flower 數量跟重配對的 app，因為我懶得去用定時執行 =)
* 因為要求迭代很快，所以我架構跟 code 沒有寫很好，前後可能會有寫法不一的情況
* 因為是課堂上的 Project，所以應該不會再做改版，也不會對 bug 做修正，若有人有問題或想法可以寄信到 <a href="mailto:hjkja0511@gmail.com?">hjkja0511@gmail.com</a>