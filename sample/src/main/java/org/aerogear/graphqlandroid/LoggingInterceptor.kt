package org.aerogear.graphqlandroid

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class LoggingInterceptor : Interceptor {

    val TAG = javaClass.simpleName + "Sample"

    lateinit var headers: Headers

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        headers = request.headers()
        val t1 = System.nanoTime()
        Log.e(
            TAG + "1",
            " ${request.url()}  ---   ${bodyToString(request)}   ---  ${request.headers()}"
        )

        val response = chain.proceed(request)
        val t2 = System.nanoTime()
        Log.e(TAG + "2 ", " ${response.request().url()}  ${t2 - t1}  ${request.headers()}")
        return response
    }

    private fun bodyToString(request: Request): String {
        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()?.writeTo(buffer)
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }
    }
}