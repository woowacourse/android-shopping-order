package woowacourse.shopping.data.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProductService {
    private val logging = HttpLoggingInterceptor().apply {
        level = Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val INSTANCE: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
