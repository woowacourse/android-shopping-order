package woowacourse.shopping.data.respository.product.source.remote

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.model.Server
import woowacouse.shopping.model.product.Product
import java.io.IOException

class ProductRemoteDataSourceImpl(
    private val server: Server,
) : ProductRemoteDataSource {

    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<Product>) -> Unit,
    ) {
        val client = OkHttpClient()
        val path = PRODUCT
        val request = Request.Builder().url(server.url + path).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Request Failed", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return onFailure()
                    onSuccess(parseProductList(body))
                    return
                }
                onFailure()
            }
        })
    }

    override fun requestData(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: Product) -> Unit,
    ) {
        val client = OkHttpClient()
        val path = "$PRODUCT/$productId"
        val request = Request.Builder().url(server.url + path).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Request Failed", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return onFailure()
                    onSuccess(parseProduct(JSONObject(body)))
                    return
                }
                onFailure()
            }
        })
    }

    private fun parseProductList(response: String): List<Product> {
        val products = mutableListOf<Product>()
        val jsonArray = JSONArray(response)

        for (index in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(index) ?: continue
            products.add(parseProduct(json))
        }

        return products
    }

    private fun parseProduct(json: JSONObject): Product {
        val id = json.getLong("id")
        val name = json.getString("name")
        val price = json.getInt("price")
        val image = json.getString("imageUrl")

        return Product(id, name, price, image)
    }

    companion object {
        private const val PRODUCT = "/products"
    }
}
