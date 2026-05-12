package woowacourse.shopping.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: RetrofitInterface = retrofit.create(RetrofitInterface::class.java)
}