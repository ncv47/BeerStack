# Mobile Security project -  documentation

## Group Members
1. Noah Chang Vandewalle
2. Lancelot Boden
3. Kenzo Haeck
4. Noah Defruyt

## Project summary
Use this app to keep track of what beers you have already drunk in your life with a personal rating given to it

### How To Use  
ZUIPPPEN

## Requirements
### ℹ️ Legend
- :heavy_check_mark: = Implemented
- :x: = Not implemented
- :hourglass: = Work in progress
 
| Status             | Description                         | Details                     |     |
| ------------------ | ----------------------------------- | --------------------------- | --- |
|                    | **Application**                     |                             |     |
| :heavy_check_mark: | 2 UI screens                        | Just need to make it pretty |     |
| :heavy_check_mark: | Secure API request                  |                             |     |
| :hourglass:        | API request with IDOR               |                             |     |
| :heavy_check_mark: | Connection to room database         |                             |     |
| :heavy_check_mark: | Secure storage                      |                             |     |
|                    |                                     |                             |     |
|                    | **Security**                        |                             |     |
| :heavy_check_mark: | Unsafe storage                      |                             |     |
| :hourglass:        | Malware                             |                             |     |
| :heavy_check_mark: | Frida functionality                 |                             |     |
| :heavy_check_mark: | Detect root and block functionality |                             |     |

# Overview app
Describe the implementation of the following topics.

### ![](readme-resources/Screenshot.png) Screenshots
Give screenshots for every screen in the application. Give each screen an unique name.

## ![](readme-resources/API.png) Secure API request/Intercept & Modify Request (Secure)

### Setup

#### Network

##### Edit Wifi

In your Android device, go to Settings > Network & internet

Click on the modify button

From the Advanced options menu, select Proxy > Manual.

![image.png](readme-resources/image.png)

---

#### Burp Certificate

get cacert.der from [http://burp](http://brup) in the burpsuite browser or google it and download from the site

[cacert.der](readme-resources/cacert.der)

##### Set Certificate in Windows

Press Win+R > type `certmgr.msc` > Enter to open Certificate Manager.

![image.png](readme-resources/image1.png)

![image.png](readme-resources/image2.png)

##### Set Certificate in Emulator

if command not added to system variables: `cd …\OpenSSL-Win64\bin>` and use `./` before openssl

move the .der to the same folder as openssl if necessary

`openssl x509 -inform DER -in cacert.der -out cacert.pem`

`openssl x509 -inform PEM -subject_hash_old -in cacert.pem`

![image.png](readme-resources/image3.png)

`Rename-Item cacert.pem 9a5ba575.0` (<HASH_AT_TOP>.0)

`cd \android_sdk\platform-tools`

move the cacert.pem to the same folder as adb if necessary

normally would but it in /system/etc/security/cacertsbut  by default the /system directory is not
writeable.

`mkdir -p /data/adb/modules/playstore/system/etc/security/cacerts/`

`adb push 9a5ba575.0 /data/adb/modules/playstore/system/etc/security/cacerts/9a5ba575.0`

---

#### AlwaysTrustUserCerts Magisk Module

##### If no magisk Installed

Download the latest [**Magisk APK**](https://github.com/topjohnwu/Magisk/releases).

`adb install Magisk-29.apk`

Workarround via Magisk, also bypass ssl pinning:

[AlwaysTrustUserCerts_v1.3.zip](readme-resources/AlwaysTrustUserCerts_v1.3.zip)

`./adb push AlwaysTrustUserCerts_v1.3.zip /sdcard/Download/`

- Open the **Magisk app** inside your emulator.
- Go to the **Modules** tab.
- Tap **Install from Storage** or the **“+”** icon.
- Browse to **/sdcard/Download/AlwaysTrustUserCerts-v1.3.zip** and select it.
- Wait for installation to complete
- Reboot

##### **Install Burp CA as User Certificate**

`./adb push cacert.der /sdcard/Download/`

- Open **settings** on the device
- More Security & Privacy > **Encryption & credentials**
- Tap **Install a certificate**.
- Choose **CA certificate** (not VPN, Wi-Fi, or user).
- Select **“Files”** and browse to **/sdcard/Download/cacert.der**
- If prompted with a warning, confirm or tap **“Install anyway”**.

---

### Intercept & Modify 1st Request (Secure)

There are 2 functionalities preventing the user from doing this intercept & modify. For the demo its disabled temporary to show it works. With the functionalities enabled it would block this ‘exploit’.

#### Blocked Root

If the attacker uses magisk AlwaysTrustUserCerts this will be blocked because super user is needed
See Blocked root code in header "Root"

#### Secure Sockets Layer

Secures the request so its cant be intercepted without the certificate, TLS handshake
See res/xml/network_security_config.xml
and this line in AndroidManifest.xml: android:networkSecurityConfig="@xml/network_security_config"

if burpsuite intercepting on the network get this error:

![image.png](readme-resources/image8.png)

##### Get The Correct Certificate SHA-256 Hash

Go to the website, click on the lock > connection is secure > certificate is valid
Then in the new window details > export > .crt file

![image.png](readme-resources/image9.png)

Then for the hash do these commands with openssl:

```powershell
openssl x509 -in koenkoreman-be.pem -pubkey -noout | openssl pkey
-pubin -outform der | openssl dgst -sha256 -binary | openssl enc -
base64
```

Result is this:
> HsbawayQYhB8+cXA6fHLgTgcXsw9vVb8eRIJ2LVfY7E=

##### That's for the old API, for the new own API:

![image.png](readme-resources/image14.png)

#### The Intercept & Modify Exploit

When pressed the euro or dollar button next to the search bar, the API is (re)fetched and the prices change from currency. When this happens and both SSL/Root block is disabled/bypassed you can intercept the request with burpsuite

![image.png](readme-resources/image4.png)

when you change the path of the api in the inspector (bottom right corner) from euro to twd (taiwanese dollar ~x36)

![image.png](readme-resources/image5.png)

The value of each beer logically sky rockets

![image.png](readme-resources/image6.png)

![image.png](readme-resources/image7.png)

So the euro conversion rate api request [euro conversion rate](https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json) is intercepted and modified to the taiwanese dollar conversion rate: [taiwanese dollar conversion rate](https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/twd.json)

#### ![](readme-resources/API.png) API request with IDOR

For IDOR we will manipulate the app into thinking we are another user to read out our stack/collection

![image.png](readme-resources/image15.png)

When logged in as user 5 i have 1 beer in my collection

![image.png](readme-resources/image16.png)

But if we intercept this request we can change the user ID from 5

![image.png](readme-resources/image17.png)

To user ID 3

![image.png](readme-resources/image18.png)

ass a result we will see the collectin of user ID 3 instead of 5, even tho the app still thinks we are 5: "Logged in user ID: 5"

![image.png](readme-resources/image19.png)

database for reference

![image.png](readme-resources/image20.png)

## ![](readme-resources/Database.png) Room database
Type of data stored in the database used in screen x and displayed in screen y.

## ![](readme-resources/Database.png) Secure storage
Type of data stored used in screen x and displayed in screen y.

## ![](readme-resources/Database.png) Unsecure storage
Type of data stored used in screen x and displayed in screen y.

## ![](readme-resources/Notifications.png) Malware
Implementation of malware.

## ![](readme-resources/Frida.png) Frida
Detail implementation of Frida
### What to do
![image.png](readme-resources/image12.png)
We will be bypassing this function that checks if the phone is rooted or not so that we will still be able to run the app even though we have a rooted device.

### Starting point
![image.png](readme-resources/11.png)
I started of with the code from the slides and tested just this but with some tweaks so it fits our app and not the one from the slides

```Javascript
Java.perform(function () {

     var BaseActivity = Java.use("com.example.beerstack.BaseActivity");

     BaseActivity.isRooted.implementation = function () {

          console.log("[*] isRooted() got called!");

          this.isRooted();

     };

});
```

![image.png](readme-resources/image13.png)

This was the result of the first test where we see that we called the isRooted() functionality and were able to do something with it so for now just a console.log() and do what it was supposed to do with the this.isRooted()

### Final changes

```Javascript
Java.perform(function () {

     var BaseActivity = Java.use("com.example.beerstack.BaseActivity");

     BaseActivity.isRooted.implementation = function () {

         console.log("[*] isRooted() got called!");

        return false;

     };

});
```

We changed the this.isRooted(); to return false;
so now we don't just call the normal isRooted() function but we just always return a false so that it always looks like the device is not rooted.
![image.png](readme-resources/image12.png)
Now we see it got called and there are no more errors and the app just starts up even though we have a rooted device

## ![](readme-resources/Root.png) Root
Implementation of the detecting root and block functionality.

If the attacker uses magisk alwaystrustusercerts this will be blocked because super user is needed

this is done with the BaseActivity code:

```kotlin
//New activity so I can link all others to this instead of getting an entire root check for every activity
abstract class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isRooted()) {
            //Closes the app
            finish()
            return
        }
    }

    private fun isRooted(): Boolean {
        return checkSuPath() || checkWhichSu()
    }

    private fun checkSuPath(): Boolean {
        //Checks for su binaries
        val paths = arrayOf(
            "/sbin/su",
            "/system/app/Superuser.apk",
            "/system/bin/failsafe/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/system/sd/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su"
        )
        return paths.any { java.io.File(it).exists() }
    }

    private fun checkWhichSu(): Boolean {
        return try {
            Runtime.getRuntime()
                //Executes the command 'which su'
                .exec(arrayOf("which", "su"))
                //Read the output of the earlier executed command
                .inputStream
                .bufferedReader()
                .readText()
                //If it's not empty that means the device is likely rooted
                //because 'which su' doesn't return anything if there isn't a super user on the device
                .isNotEmpty()
        } catch (_: Exception) {
            //This catches any exceptions like if the command doesn't exist or shell execution is blocked
            false
        }
    }
}
```


## Link to Panopto video
https://

## Repositories
- Code
  - [Link to repository]
- APK
  - [Link to repository]