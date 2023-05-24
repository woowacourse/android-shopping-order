package woowacourse.shopping.repositoryImpl

import java.lang.Thread.sleep
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.model.CartProduct

class RemoteCartDataSource(baseUrl: String) {
    private val baseUrl = baseUrl.removeSuffix("/")
    private val client = OkHttpClient()

    private var credentials = ""

    fun getAll(): List<CartProduct> {
        val request = Request.Builder()
            .url("$baseUrl/cart-items")
            .header("Authorization", "Basic $credentials")
            .build()
        return parseCartProductsResponse(executeRequest(request).body?.string())
    }

    private fun parseCartProductsResponse(executeRequest: String?): List<CartProduct> {
        println(executeRequest)
        JSONArray(executeRequest).map {
            val item = it as JSONObject
            val product = item.getJSONObject("product")

            CartProduct(
                id = item.getInt("id"),
                count = item.getInt("quantity"),
                name = product.getString("name"),
                price = product.getInt("price"),
                imageUrl = product.getString("imageUrl"),
                checked = true // TODO: 수정 !
            )
        }.let { return it }
    }

    fun postItem(itemId: Int): Response {
        val request = Request.Builder()
            .url("$baseUrl/cart-items/$itemId")
            .header("Authorization", "Basic $credentials")
            .header("Content-Type", "application/json")
            .post(JSONObject().put("productId", itemId).toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        return executeRequest(request)
    }

    fun patchItemQuantity(itemId: Int, quantity: Int): Response {
        val request = Request.Builder()
            .url("$baseUrl/cart-items/$itemId")
            .header("Authorization", "Basic $credentials")
            .header("Content-Type", "application/json")
            .patch(JSONObject().put("quantity", quantity).toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        return executeRequest(request)
    }

    fun deleteItem(itemId: Int): Response {
        val request = Request.Builder()
            .url("$baseUrl/cart-items/$itemId")
            .header("Authorization", "Basic $credentials")
            .delete()
            .build()

        return executeRequest(request)
    }

    private fun executeRequest(request: Request): Response {
        var responseAll: Response? = null
        client.newCall(request).enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    println("Request failed: ${e.message}")
                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    responseAll = response
                }
            }
        )
        while (responseAll == null) {
            sleep(10)
        }
        return responseAll as Response
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    }
}
