package com.billcorea.sudabot0222.retrofit

import android.provider.Settings.System.getString
import com.billcorea.sudabot0222.BuildConfig
import com.billcorea.sudabot0222.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type

interface RetrofitApi {

    @Headers("Content-Type: application/json", "Authorization: KakaoAK ${BuildConfig.KAKAO_REST_KEY}")
    @POST("v1/inference/kogpt/generation")
    fun doKoGPT(
        @Body requestBean: RequestBean
    ): Call<ResponseBean>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://api.kakaobrain.com/"
        private val client = OkHttpClient.Builder().build()
        val gson : Gson =   GsonBuilder().setLenient().create();
        val nullOnEmptyConverterFactory = object : Converter.Factory() {
            fun converterFactory() = this
            override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object :
                Converter<ResponseBody, Any?> {
                val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
                override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
                    try{
                        nextResponseBodyConverter.convert(value)
                    }catch (e:Exception){
                        e.printStackTrace()
                        null
                    }
                } else{
                    null
                }
            }
        }

        fun create(): RetrofitApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(nullOnEmptyConverterFactory)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
                .create(RetrofitApi::class.java)
        }

    }
}