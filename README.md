# iphysio-mobile-PoseEstimation

 Application de physiothérapie sur Android.
 
 Application web obligatoire pour utiliser l'application: https://github.com/sebseb24/iphysio-web
 
 
 
 Requis
-
Android Studio (testée avec Android Studio 4.1.1)

CMake (installé à partir de SDK Tools dans Android Studio)

Appareil Android (Android 8.0 minimum)

Émulateurs
-
La partie de traitement d'images requiert un CPU ARM. Il n'est donc pas possible d'utiliser un émulateur x86 pour utiliser l'application.
Par contre, il est possible d'utiliser un émulateur x86 pour tester le restant de l'application.
Pour ce faire, commentez ceci dans le build.gradle de l'application
```
ndk {
    //Comment out this line to test the UI with a x86 Emulator
    abiFilters 'armeabi-v7a', 'arm64-v8a'
}
```

Utilisation
-
L'utilisation de l'application implique qu'un utilisateur "Client" a été créé à partir de l'application Web.

Modification de la base de données MongoDB Realm
-
Il est possible d'utiliser votre propre base de données MongoDB Realm.
Pour ce faire, modifiez ceci avec vortre propre App id dans le build.gradle de l'application
```
//Realm Id
def appId = "iphysio-app-wjkhj"
```

Modification de la base de données Firebase
-
Firebase est utilisé pour la partie messagerie de l'application.
Pour utiliser votre propre base de données, générez le fichier google-services.json et remplacez celui dans app/src/
