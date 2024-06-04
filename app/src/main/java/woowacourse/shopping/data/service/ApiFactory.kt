package woowacourse.shopping.data.service

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.request.RequestPostOrderDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto

object ApiFactory {
    private val client =
        OkHttpClient.Builder().addInterceptor(DefaultInterceptor("gaeun5744", "password")).build()

    private val tokenClient =
        OkHttpClient.Builder().addInterceptor(TokeInterceptor()).build()

    private val retrofit: Retrofit by lazy {
        retrofitBuilder(client)
    }

    private val tokenRetrofit: Retrofit by lazy {
        retrofitBuilder(tokenClient)
    }

    private val productService = retrofit.create(ProductService::class.java)

    private val cartItemService = tokenRetrofit.create(CartItemService::class.java)

    private val orderService = tokenRetrofit.create(OrderService::class.java)

    private fun retrofitBuilder(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()

    fun getProductsByOffset(
        page: Int,
        size: Int,
    ): ResponseProductsGetDto? = productService.getProductsByOffset(page = page, size = size).execute().body()

    fun getProductsById(id: Long): ResponseProductIdGetDto? = productService.getProductsById(id = id).execute().body()

    fun getCartItems(
        page: Int,
        size: Int,
    ): ResponseCartItemsGetDto? = cartItemService.getCartItems(page = page, size = size).execute().body()

    fun postCartItems(request: RequestCartItemPostDto) = cartItemService.postCartItem(request = request).execute().body()

    fun deleteCartItems(id: Long) = cartItemService.deleteCartItem(id = id).execute().body()

    fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ) = cartItemService.patchCartItem(id = id, request = request).execute().body()

    fun getCartItemCounts() = cartItemService.getCartItemCounts().execute().body()

    fun postOrder(request: RequestPostOrderDto) = orderService.postOrders(request = request).execute().body()
}
