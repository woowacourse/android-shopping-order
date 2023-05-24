package woowacourse.shopping.service

import java.lang.Thread.sleep
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

        return parseCartProductsResponse(executeRequest(request))
    }

    private fun parseCartProductsResponse(responseBody: String): List<CartProduct> {
        return responseBody.let {
            val cartJsonArray = JSONArray(it)
            val carts = mutableListOf<CartProduct>()

            for (i in 0 until cartJsonArray.length()) {
                val cartJsonObject = cartJsonArray.getJSONObject(i)
                val product = cartJsonObject.getJSONObject("product")
                carts += CartProduct(
                    id = cartJsonObject.getInt("id"),
                    count = cartJsonObject.getInt("quantity"),
                    productId = product.getInt("id"),
                    name = product.getString("name"),
                    price = product.getInt("price"),
                    imageUrl = product.getString("imageUrl"),
                    checked = true // TODO: 수정 !
                )
            }
            carts
        }
    }

    fun postItem(itemId: Int): String {
        val request = Request.Builder()
            .url("$baseUrl/cart-items")
            .header("Authorization", "Basic $credentials")
            .header("Content-Type", "application/json")
            .post(JSONObject().put("productId", itemId).toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        return executeRequest(request)
    }

    fun patchItemQuantity(itemId: Int, quantity: Int): String {
        val request = Request.Builder()
            .url("$baseUrl/cart-items/$itemId")
            .header("Authorization", "Basic $credentials")
            .header("Content-Type", "application/json")
            .patch(JSONObject().put("quantity", quantity).toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        return executeRequest(request)
    }

    fun deleteItem(itemId: Int): String {
        val request = Request.Builder()
            .url("$baseUrl/cart-items/$itemId")
            .header("Authorization", "Basic $credentials")
            .delete()
            .build()

        return executeRequest(request)
    }

    private fun executeRequest(request: Request): String {
        var responseBody: String? = null
        client.newCall(request).enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    println("Request failed: ${e.message}")
                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    responseBody = response.body?.string()
                }
            }
        )
        while (responseBody == null) { sleep(10) }
        return responseBody!!
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    }
}
