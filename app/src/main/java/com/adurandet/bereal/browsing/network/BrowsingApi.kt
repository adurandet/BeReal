package com.adurandet.bereal.browsing.network

import com.adurandet.bereal.browsing.BrowsingContent
import com.adurandet.bereal.common.network.ApiInterface
import retrofit2.http.GET
import retrofit2.http.Path

interface BrowsingApi: ApiInterface {
    @GET("items/{id}")
    suspend fun getContent(@Path("id") id:String): List<BrowsingContent>
}