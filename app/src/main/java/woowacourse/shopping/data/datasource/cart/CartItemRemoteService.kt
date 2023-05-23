package woowacourse.shopping.data.datasource.cart

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.datasource.RemoteHost
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Product
import java.io.IOException
import java.time.LocalDateTime

class CartItemRemoteService(private val host: RemoteHost) : CartItemDataSource {
    private val client = OkHttpClient()
    override fun save(cartItem: CartItem, onFinish: (CartItem) -> Unit) {
        val path = "/cart-items"
        val jsonObject = JSONObject().apply {
            put("productId", cartItem.product.id)
        }
        val body = jsonObject.toString().toRequestBody()
        val request = Request.Builder().url(host.url + path).post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic ${host.token}").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.code)
                cartItem.id = response.header("Location")?.removePrefix("/cart-items/")?.toLong()
                onFinish(cartItem)
            }
        })
    }

    override fun findAll(onFinish: (List<CartItem>) -> Unit) {
        val path = "/cart-items"
        val request =
            Request.Builder().url(host.url + path).addHeader("Authorization", "Basic ${host.token}")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val jsonArray = JSONArray(body)
                val cartItems = (0 until jsonArray.length()).map {
                    val jsonObject = jsonArray.getJSONObject(it)
                    parseToCartItem(jsonObject)
                }
                onFinish(cartItems)
            }
        })
    }

    private fun parseToCartItem(jsonObject: JSONObject): CartItem {
        val id = jsonObject.getInt("id")
        val quantity = jsonObject.getInt("quantity")
        val jsonObject1 = jsonObject.getJSONObject("product")
        val product = parseToProduct(jsonObject1)
        return CartItem(product, LocalDateTime.now(), quantity).apply { this.id = id.toLong() }
    }

    private fun parseToProduct(jsonObject: JSONObject): Product {
        val id = jsonObject.getLong("id")
        val name = jsonObject.getString("name")
        val price = jsonObject.getInt("price")
        val imageUrl = jsonObject.getString("imageUrl")
        return Product(id, imageUrl, name, price)
    }

    override fun updateCountById(id: Long, count: Int) {
        println(111)
        val path = "/cart-items/$id"
        val jsonObject = JSONObject().apply {
            put("quantity", count)
        }
        val body = jsonObject.toString().toRequestBody()
        val request = Request.Builder().url(host.url + path).patch(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic ${host.token}").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.code)
            }
        })
    }

    override fun deleteById(id: Long) {
        val path = "/cart-items/$id"
        val request = Request.Builder().url(host.url + path).delete()
            .addHeader("Authorization", "Basic ${host.token}").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }
}
