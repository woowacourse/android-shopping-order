package woowacourse.shopping.service

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.model.CartProduct

class RemoteCartService(baseUrl: String) {
    private val baseUrl = baseUrl.removeSuffix("/")
    private val client = OkHttpClient()

    private var credentials = "YUBhLmNvbToxMjM0"

    fun getAll(): List<CartProduct> {
        val request = Request.Builder()
            .url("$baseUrl/cart-items")
            .header("Authorization", "Basic $credentials")
            .get()
            .build()

        var products: List<CartProduct>? = null
        executeRequest(
            request,
            onSuccess = {
                println("Cart get all success")
                val responseBody = it.body?.string()
                    ?: throw RuntimeException("Cart get all failed")
                products = parseCartProductsResponse(responseBody)
            },
            onFailure = { println("Cart get all failed") }
        )

        while (products == null) {
            Thread.sleep(10)
        }
        return products!!
    }

    fun postItem(itemId: Int) {
        val request = Request.Builder()
            .url("$baseUrl/cart-items")
            .header("Authorization", "Basic $credentials")
            .header("Content-Type", "application/json")
            .post(JSONObject().put("productId", itemId).toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        executeRequest(
            request,
            onSuccess = { println("Cart post success") },
            onFailure = { println("Cart post failed") }
        )
    }

    fun patchItemQuantity(itemId: Int, quantity: Int) {
        val request = Request.Builder()
            .url("$baseUrl/cart-items/$itemId")
            .header("Authorization", "Basic $credentials")
            .header("Content-Type", "application/json")
            .patch(JSONObject().put("quantity", quantity).toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        executeRequest(
            request,
            onSuccess = { println("Cart patch success") },
            onFailure = { println("Cart patch failed") }
        )
    }

    fun deleteItem(itemId: Int) {
        val request = Request.Builder()
            .url("$baseUrl/cart-items/$itemId")
            .header("Authorization", "Basic $credentials")
            .delete()
            .build()

        executeRequest(
            request,
            onSuccess = { println("Cart delete success") },
            onFailure = { println("Cart delete failed") }
        )
    }

    private fun executeRequest(
        request: Request,
        onSuccess: (Response) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        client.newCall(request).enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    onFailure(e)
                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    onSuccess(response)
                }
            }
        )
    }

    private fun parseCartProductsResponse(responseBody: String): List<CartProduct> {
        val cartJsonArray = JSONArray(responseBody)
        val carts = mutableListOf<CartProduct>()
        for (i in 0 until cartJsonArray.length()) {
            carts += CartProduct.fromJson(cartJsonArray.getJSONObject(i))
        }
        return carts
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    }
}
