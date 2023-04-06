package ru.psuti.apache1337.homeowners.data

import okhttp3.Cookie
import okhttp3.CookieJar

import okhttp3.HttpUrl

class AppCookieJar : CookieJar{
    private var cookies: List<Cookie>? = null

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies ?: ArrayList<Cookie>()
    }
}