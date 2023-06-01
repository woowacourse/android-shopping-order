package woowacourse.shopping.data.respository.product.source.remote

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.model.Server
import java.io.IOException

class ProductRemoteDataSourceImpl(
    private val server: Server,
) : ProductRemoteDataSource {

    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    ) {
        Thread {
            val client = OkHttpClient()
            val path = PRODUCT
            val request = Request.Builder().url(server.url + path).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("krrong", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: return onFailure()
                        onSuccess(parseCartEntities(body))
                        return
                    }
                    onFailure()
                }
            })
        }.start()
    }

    override fun requestData(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: CartRemoteEntity) -> Unit,
    ) {
        Thread {
            val client = OkHttpClient()
            val path = "$PRODUCT/$productId"
            val request = Request.Builder().url(server.url + path).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("krrong", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: return onFailure()
                        onSuccess(parseCartEntity(JSONObject(body)))
                        return
                    }
                    onFailure()
                }
            })
        }.start()
    }

    private fun parseCartEntities(response: String): List<CartRemoteEntity> {
        val products = mutableListOf<CartRemoteEntity>()
        val jsonArray = JSONArray(response)

        for (index in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(index) ?: continue
            products.add(parseCartEntity(json))
        }

        return products
    }

    private fun parseCartEntity(json: JSONObject): CartRemoteEntity {
        val id = json.getLong("id")
        val name = json.getString("name")
        val price = json.getInt("price")
        val image = json.getString("imageUrl")

        val productEntity = ProductEntity(id, name, price, image)

        return CartRemoteEntity(DUMMY_CART_ID, DEFAULT_QUANTITY, productEntity)
    }

    companion object {
        private const val PRODUCT = "/products"
        private const val DUMMY_CART_ID = 99999L
        private const val DEFAULT_QUANTITY = 1
    }
}
