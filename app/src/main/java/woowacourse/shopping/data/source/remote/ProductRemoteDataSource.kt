package woowacourse.shopping.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.source.remote.api.ProductService
import woowacourse.shopping.data.source.remote.dto.product.ProductContent
import woowacourse.shopping.data.source.remote.dto.product.ProductResponse
import kotlin.jvm.java

class ProductRemoteDataSource(
    private val baseUrl: String = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com",
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    private val productService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ProductService::class.java)

    suspend fun fetchProducts(
        offset: Int,
        limit: Int,
    ): List<ProductContent> =
        withContext(Dispatchers.IO) {
            val response =
                productService.requestProducts(
                    page = offset / limit,
                    size = limit,
                )
            response.content
        }

    suspend fun fetchProductById(id: Long): ProductResponse =
        withContext(Dispatchers.IO) {
            productService.requestProduct(id = id)
        }
}
