# BeerStack
info about the app blablabla

## Functionalities
API request 1/2...
Collection Database...

## How To Use
ZUIPPPEN

# Required Assignments Explained
- Intercept & Modify Request (Secure)
- IDOR (Insecure)
- Frida Exploit
- Malware

# Intercept & Modify Request (Secure)

## Setup

### Network

#### Edit Wifi

In your Android device, go to Settings > Network & internet

Click on the modify button

From the Advanced options menu, select Proxy > Manual.

![image.png](readme-resources/image.png)

---

### Burp Certificate

get cacert.der from [http://brup](http://brup) in the burpsuite browser or google it and download from the site

[cacert.der](readme-resources/cacert.der)

#### Set Certificate in Windows

Press Win+R > type `certmgr.msc` > Enter to open Certificate Manager.

![image.png](readme-resources/image1.png)

![image.png](readme-resources/image2.png)

#### Set Certificate in Emulator

if command not added to system variables: `cd …\OpenSSL-Win64\bin>` and use `./` before openssl

move te .der to the same folder as openssl if neccesary

`openssl x509 -inform DER -in cacert.der -out cacert.pem`

`openssl x509 -inform PEM -subject_hash_old -in cacert.pem`

![image.png](readme-resources/image3.png)

`Rename-Item cacert.pem 9a5ba575.0` (<HASH_AT_TOP>.0)

`cd \android_sdk\platform-tools`

move the cacert.pem to the same folder as adb if neccesary

normally would but it in /system/etc/security/cacertsbut  by default the /system directory is not
writable.

`mkdir -p /data/adb/modules/playstore/system/etc/security/cacerts/`

`adb push 9a5ba575.0 /data/adb/modules/playstore/system/etc/security/cacerts/9a5ba575.0`

---

### AlwaysTrustUserCerts Magisk Module

#### If no magisk Installed

Download the latest [**Magisk APK**](https://github.com/topjohnwu/Magisk/releases).

`adb install Magisk-29.apk`

Work arround via Magisk, also bypass ssl pinning:

[AlwaysTrustUserCerts_v1.3.zip](readme-resources/AlwaysTrustUserCerts_v1.3.zip)

`./adb push AlwaysTrustUserCerts_v1.3.zip /sdcard/Download/`

- Open the **Magisk app** inside your emulator.
- Go to the **Modules** tab.
- Tap **Install from Storage** or the **“+”** icon.
- Browse to **/sdcard/Download/AlwaysTrustUserCerts-v1.3.zip** and select it.
- Wait for installation to complete
- Reboot

#### **Install Burp CA as User Certificate**

`./adb push cacert.der /sdcard/Download/`

- Open **settings** on the device
- More Security & Privacy > **Encryption & credentials**
- Tap **Install a certificate**.
- Choose **CA certificate** (not VPN, Wi-Fi, or user).
- Select **“Files”** and browse to **/sdcard/Download/cacert.der**
- If prompted with a warning, confirm or tap **“Install anyway”**.

---

## Intercept & Modify 1st Request (Secure)

There are 2 functionalities preventing the user from doing this intercept & modify. For the demo its disabled temporary to show it works. With the functionalities enabled it would block this ‘exploit’.

### Blocked Root

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

### Secure Sockets Layer

Secures the request so its cant be intercepted without the certificate, TLS handshake
See res/xml/network_security_config.xml
and this line in AndroidManifest.xml: android:networkSecurityConfig="@xml/network_security_config"

if burpsuite intercepting on the network get this error:

![image.png](readme-resources/image8.png)

#### Get The Correct Certificate SHA-256 Hash

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

### The Intercept & Modify Exploit

When app is started or something changes in the search bar, the API is (re)fetched. When this happens and both SSL/Root block is disabled/bypassed you can intercept the request with burpsuite

![image.png](readme-resources/image4.png)

when you change the path of the api in the inspector (bottom left corner)

![image.png](readme-resources/image5.png)

You’ll be able to see another endpoint of the API (Filtered with ‘g’ in this case)

![image.png](readme-resources/image6.png)

![image.png](readme-resources/image7.png)

So the main API request to [https://api.sampleapis.com/beers/ale](https://api.sampleapis.com/beers/ale) is intercepted and modified to another endpoint: [https://api.sampleapis.com/beers/stouts](https://api.sampleapis.com/beers/stouts)

## IDOR 2nd Request (Insecure)

Need the collection page/database to be finished:

you will intercept the insecure request to add a beer to the collection and edit it into a colleciton of another user/delete…

Or just modify the ID of the beer to make another beer go into the collection