package woowacourse.shopping.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import woowacourse.shopping.data.cart.dto.CartItemResponse
import woowacourse.shopping.data.cart.dto.CartQuantityResponse
import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.product.dto.CartRequest
import woowacourse.shopping.data.product.dto.ProductResponse
import woowacourse.shopping.data.product.dto.ProductsResponse
import java.util.Base64

class ProductsHttpClient(
    private val baseUrl: String = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com",
) {
    fun getCart(
        page: Int,
        size: Int,
    ): CartResponse {
        val response =
            http(
                HttpMethod.Get,
                "$PATH_CART_ITEMS?page=$page&size=$size",
                true,
            )
        val jsonString: String = response.body?.string() ?: ""
        return Json.decodeFromString(jsonString)
    }

    fun getAllCart(): CartResponse {
        val response =
            http(
                HttpMethod.Get,
                PATH_CART_ITEMS,
                true,
            )
        val jsonString: String = response.body?.string() ?: ""
        return Json.decodeFromString(jsonString)
    }

    fun postCartItem(
        productId: Long,
        quantity: Int,
    ): Response {
        val cartRequest = CartRequest(productId, quantity)
        val jsonString: String = Json.encodeToString(cartRequest)

        return http(
            HttpMethod.Post(jsonString.toRequestBody(MEDIA_TYPE_JSON.toMediaTypeOrNull())),
            PATH_CART_ITEMS,
            true,
        )
    }

    fun deleteShoppingCartItem(cartItemId: Long) =
        http(
            HttpMethod.Delete(),
            "/cart-items/$cartItemId",
            true,
        )

    fun patchCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    ) {
        val requestBody = CartItemResponse(newQuantity)
        val jsonString: String = Json.encodeToString(requestBody)

        http(
            HttpMethod.Patch(jsonString.toRequestBody(MEDIA_TYPE_JSON.toMediaTypeOrNull())),
            path = "$PATH_CART_ITEMS/$cartItemId",
            needAuthorization = true,
        )
    }

    fun getCartItemQuantity(): CartQuantityResponse {
        val response =
            http(
                HttpMethod.Get,
                "$PATH_CART_ITEMS/counts",
                true,
            )
        val jsonString = response.body?.string() ?: ""
        return Json.decodeFromString(jsonString)
    }

    fun getProducts(
        page: Int,
        size: Int,
    ): ProductsResponse {
        val response = http(HttpMethod.Get, "/products?page=$page&size=$size")
        val jsonString: String = response.body?.string() ?: ""
        return Json.decodeFromString(jsonString)
    }

    fun getProductById(productId: Long): ProductResponse {
        val response =
            http(
                HttpMethod.Get,
                "/products/$productId",
            )
        val jsonString = response.body?.string() ?: ""
        return Json.decodeFromString(jsonString)
    }

    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            ).build()

    private fun http(
        httpMethod: HttpMethod,
        path: String,
        needAuthorization: Boolean = false,
    ): Response {
        val request: Request =
            Request
                .Builder()
                .url(baseUrl + path)
                .addBasicAuthorizationHeader(needAuthorization)
                .method(httpMethod.name, httpMethod.body)
                .build()

        return client.newCall(request).execute()
    }

    private fun Request.Builder.addBasicAuthorizationHeader(needAuthorization: Boolean): Request.Builder {
        val valueToEncode = "${AuthStorage.id}:${AuthStorage.pw}".toByteArray()

        return apply {
            if (needAuthorization) {
                header(
                    "Authorization",
                    "Basic " + Base64.getEncoder().encodeToString(valueToEncode),
                )
            }
        }
    }

    companion object {
        private const val PATH_CART_ITEMS = "/cart-items"
        private const val MEDIA_TYPE_JSON = "application/json"
    }
}
