package woowacourse.shopping.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Retrofit
import woowacourse.shopping.data.product.service.ProductService

object API {
    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaTypeOrNull()!!))
            .build()

    val productService: ProductService = retrofit.create(ProductService::class.java)
}
