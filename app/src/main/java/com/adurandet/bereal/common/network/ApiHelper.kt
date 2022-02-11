package com.adurandet.bereal.common.network

import com.adurandet.bereal.BuildConfig
import com.adurandet.bereal.browsing.network.BrowsingApi
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiHelper {

    // This is super dirty way to manage autthorization
    val authorizaztionHeader: Pair<String, String> = "Authorization" to "Basic bm9lbDpmb29iYXI="

    private fun getApiInterface(apiClass: Class<out ApiInterface>): ApiInterface {
        val headerInterceptor = Interceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()
            builder.header("Content-Type", "application/json")
            builder.header(authorizaztionHeader.first, authorizaztionHeader.second)
            chain.proceed(builder.build())
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()

        val gson = GsonBuilder().create()

        val retrofit: Retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(apiClass)
    }

    fun getBrowsingApiInterface() = getApiInterface(BrowsingApi::class.java) as BrowsingApi
}