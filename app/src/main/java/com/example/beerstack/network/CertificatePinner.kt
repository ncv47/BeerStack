package com.example.beerstack.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

val certificatePinner = CertificatePinner.Builder()
    .add("api.sampleapis.com", "sha256/HsbawayQYhB8+cX46fHlLgTgcKsw9Vyb0BeRjJ2LVfY7E=")
    .build()

val okHttpClient = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()