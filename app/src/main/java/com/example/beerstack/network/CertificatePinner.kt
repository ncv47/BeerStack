package com.example.beerstack.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

val certificatePinner = CertificatePinner.Builder()
    .add("api.sampleapis.com", "sha256/ZxEAGkQnK2SFieL8wKsYIuQE6ruj82isoDykb/EmcXY=")
    .build()

val okHttpClient = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()