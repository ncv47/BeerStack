/*package com.example.beerstack.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

val certificatePinner: CertificatePinner = CertificatePinner.Builder()
    .add("api.sampleapis.com", "sha256/HsbawayQYhB8+cXA6fHLgTgcXsw9vVb8eRIJ2LVfY7E=")
    .build()

val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    //.certificatePinner(certificatePinner) //remove commentary to use the certificatepinner with SSL
    //This is the 2nd method to SSL, primary method in res/xml/network_security_config.xml via AndroidManifest.xml
    .certificatePinner(CertificatePinner.DEFAULT) //If you dont want to use SSL Pinning (in this case this is skipped since we already have antoher working SSL method)
    .build()*/