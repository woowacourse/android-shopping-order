package woowacourse.shopping.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.service.RetrofitProductService

object RetrofitUtil {

    private var instance: Retrofit? = null

    val retrofitProductService: RetrofitProductService by lazy {
        getRetrofit().create(RetrofitProductService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder() // 객체를 생성해 줍니다.
                .baseUrl("http://13.125.207.155:8080") // 통신할 서버 주소를 설정합니다.
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance!!
    }
}
