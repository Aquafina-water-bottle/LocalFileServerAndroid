# Local File Server for Android

An extremely simple HTTP server to view locally stored files.

* Only works under `/storage/emulated/0/Android/data/com.rayrtheii.localfileserver/files/`
* The primary use of this was to use [Mokuro](https://github.com/kha-white/mokuro) on locally stored files while having access to
    [AnkiConnectAndroid](https://github.com/KamWithK/AnkiconnectAndroid) without setting CORS to `*`.
* No encryption or security features. Since it's a local server, unless you're trying to open/read
    confidential data, it should be fine to use.
* Code based off of [AnkiConnectAndroid](https://github.com/KamWithK/AnkiconnectAndroid) (this is technically a fork)

## Usage
1. Move the desired files into the following folder:
    ```
    /storage/emulated/0/Android/data/com.rayrtheii.localfileserver/files/
    ```
2. Start the app
3. Visit http://localhost:8976/browse

## Also consider
* Using [mokuro.moe](mokuro.moe)
* [Simple HTTP Server](https://play.google.com/store/apps/details?id=com.phlox.simpleserver): A more featureful but closed source variant of this app
* Running an Apache server on Termux
