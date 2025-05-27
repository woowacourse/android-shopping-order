package woowacourse.shopping.data

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import woowacourse.shopping.data.product.dto.CartRequest
import woowacourse.shopping.data.product.dto.ProductResponse
import java.util.Base64

class ProductsHttpClient(
    private val baseUrl: String = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com",
) {
    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            ).build()

    fun getProductById(id: Long): ProductResponse {
        val response = httpGet("/products/$id")
        val jsonString = response.body?.string() ?: ""
        return Json.decodeFromString(jsonString)
    }

    fun getShoppingCart(
        page: Int,
        size: Int,
    ): Response = httpGet("$PATH_CART_ITEMS?page=$page&size=$size&sort=string")

    fun postShoppingCartItem(
        id: Long,
        quantity: Int,
    ): Response {
        val cartRequest = CartRequest(id, quantity)
        val jsonString: String = Json.encodeToString(cartRequest)

        return httpPost(
            PATH_CART_ITEMS,
            jsonString.toRequestBody(MEDIA_TYPE_JSON.toMediaTypeOrNull()),
            true,
        )
    }

    fun getProducts(
        page: Int,
        size: Int,
    ): Response = httpGet("/products?page=$page&size=$size")

    fun getCartItems(): Response = run(PATH_CART_ITEMS)

    private fun run(path: String): Response {
        val request: Request =
            Request
                .Builder()
                .url(baseUrl + path)
                .build()

        return client.newCall(request).execute()
    }

    private fun httpGet(
        path: String,
        needAuthorization: Boolean = false,
    ): Response {
        val valueToEncode = "jerry8282:password"
        val request: Request =
            Request
                .Builder()
                .url(baseUrl + path)
                .apply {
                    if (needAuthorization) {
                        authorizationHeader(valueToEncode)
                    }
                }.get()
                .build()

        return client.newCall(request).execute()
    }

    private fun Request.Builder.authorizationHeader(valueToEncode: String) {
        header(
            "Authorization",
            Base64.getEncoder().encodeToString(valueToEncode.toByteArray()),
        )
    }

    private fun httpPost(
        path: String,
        body: RequestBody,
        needAuthorization: Boolean = false,
    ): Response {
        val valueToEncode = "jerry8282:password".toByteArray()
        Log.d("asdf", "valueToEncode  $valueToEncode")
        val request: Request =
            Request
                .Builder()
                .url(baseUrl + path)
                .apply {
                    if (needAuthorization) {
                        header(
                            "Authorization",
                            "Basic " + Base64.getEncoder().encodeToString(valueToEncode),
                        )
                    }
                }.post(body)
                .build()

        Log.d("asdf", "request  ${request.headers}")
        return client.newCall(request).execute()
    }

    companion object {
        private const val PATH_CART_ITEMS = "/cart-items"
        private const val MEDIA_TYPE_JSON = "application/json"
    }
}
