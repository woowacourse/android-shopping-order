package woowacourse.shopping.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.service.ProductService

object ApiClient {
    val productService =
        Retrofit
            .Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductService::class.java)
}


