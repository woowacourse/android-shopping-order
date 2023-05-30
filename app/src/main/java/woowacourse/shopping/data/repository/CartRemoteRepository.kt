package woowacourse.shopping.data.repository

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import java.io.IOException

class CartRemoteRepository(private val baseUrl: String) : CartRepository {

    private val client =
        OkHttpClient.Builder().build()

    override fun findAll(callback: (List<CartProduct>) -> Unit) {
        val request = Request.Builder()
            .url("$baseUrl/cart-items")
            .addHeader("Authorization", "Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
            .build()

        executeGetRequest(request, callback)
    }

    override fun insert(productId: Int, callback: (cartItemId: Int) -> Unit) {
        val requestBody = JSONObject().put("productId", productId).toString()
            .toRequestBody(contentType = "application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/cart-items")
            .post(requestBody)
            .addHeader("Authorization", USER_EMAIL_INFO)
            .build()

        executeCreateRequest(request, callback)
    }

    override fun update(cartId: Int, count: Int, callback: (Boolean) -> Unit) {
        val requestBody = JSONObject().put("quantity", count).toString()
            .toRequestBody(contentType = "application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/cart-items/$cartId")
            .patch(requestBody)
            .addHeader("Authorization", USER_EMAIL_INFO)
            .build()

        executeUpdateRequest(request, callback)
    }

    override fun remove(cartId: Int, callback: (Boolean) -> Unit) {
        val request = Request.Builder()
            .url("$baseUrl/cart-items/$cartId")
            .delete()
            .addHeader("Authorization", USER_EMAIL_INFO)
            .build()

        executeUpdateRequest(request, callback)
    }

    override fun findRange(mark: Int, rangeSize: Int, callback: (List<CartProduct>) -> Unit) {
        val request = Request.Builder()
            .url("$baseUrl/cart-items")
            .addHeader("Authorization", USER_EMAIL_INFO)
            .build()

        fun callBackWrapper(cartProducts: List<CartProduct>) {
            if (mark + rangeSize >= cartProducts.size) {
                callback(cartProducts.subList(mark, cartProducts.size))
            } else {
                callback(cartProducts.subList(mark, mark + rangeSize))
            }
        }
        executeGetRequest(request, ::callBackWrapper)
    }

    override fun isExistByMark(mark: Int, callback: (Boolean) -> Unit) {
        val request = Request.Builder()
            .url("$baseUrl/cart-items")
            .addHeader("Authorization", USER_EMAIL_INFO)
            .build()

        fun callBackWrapper(cartProducts: List<CartProduct>) {
            callback(cartProducts.size > mark)
        }
        executeGetRequest(request, ::callBackWrapper)
    }

    private fun executeGetRequest(request: Request, callBack: (List<CartProduct>) -> Unit) {
        var responseBody: String?
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    throw java.lang.RuntimeException("Request Failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    responseBody = response.body?.string()
//                    callBack(parseResponse(responseBody))
                    response.close()
                }
            },
        )
    }

    private fun executeUpdateRequest(request: Request, callBack: (Boolean) -> Unit) {
//        var responseBody: String?
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    throw java.lang.RuntimeException("Request Failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callBack(response.isSuccessful)
                    response.close()
                }
            },
        )
    }

    private fun executeCreateRequest(
        request: Request,
        callBack: (returnedId: Int) -> Unit,
    ) {
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    throw java.lang.RuntimeException("Request Failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val cartItemId: Int =
                        response.header("Location", null)?.substringAfterLast("/")?.toInt() ?: 0
                    callBack(cartItemId)
                    response.close()
                }
            },
        )
    }

    companion object {
        private const val USER_EMAIL_INFO = "Basic YUBhLmNvbToxMjM0"
    }
}
