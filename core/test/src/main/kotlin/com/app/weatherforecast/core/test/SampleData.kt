package com.app.weatherforecast.core.test

interface SampleData {
    val code: Int
    val file: String

    fun readText(): String = javaClass
        .classLoader!!
        .getResource(file)
        .readText()
}