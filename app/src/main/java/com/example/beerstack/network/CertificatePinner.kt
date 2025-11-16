package com.example.beerstack.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

val certificatePinner = CertificatePinner.Builder()
    .add(
        "sampleapis.com",
        "sha256/o2xLPMnEz7bPqBbMMLHftnkEDXcyLG2XnIamRz/aV3o="
    )
    .add(
        "sampleapis.com",
        "sha256/bzpISqORtwFMcNr2gKLqsOhuPqshIHjqx5Hk6g0Jg+E="
    )
    .add(
        "sampleapis.com",
        "sha256/hNSH3uUxjVFwgXPu7ogHgslkDHyIbZ4z8p2mxz1HNro="
    )
    .build()

val okHttpClient = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    //.certificatePinner(CertificatePinner.DEFAULT) //If you dont want to use SSL Pinning
    .build()