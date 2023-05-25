package woowacourse.shopping.data.product

import com.example.domain.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ProductRemoteService {
    private val okHttpClient = OkHttpClient()

    fun requestAllProducts(
        url: String,
        port: String = "8080",
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {

        val baseUrl = "$url:$port"
        val requestUrl = "$baseUrl/products"
        val request = Request.Builder().url(requestUrl).build()

        okHttpClient.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (400 <= response.code) return onFailure()
                    val responseBody = response.body?.string()
                    response.close()

                    val result: List<Product> = responseBody?.let {
                        parseJsonToProductList(it)
                    } ?: emptyList()

                    onSuccess(result)
                }
            }
        )
    }

    fun requestProduct(
        url: String,
        port: String = "8080",
        id: Int,
        onSuccess: (product: Product?) -> Unit,
        onFailure: () -> Unit
    ) {

        val baseUrl = "$url:$port"
        val requestUrl = "$baseUrl/products/$id"
        val request = Request.Builder().url(requestUrl).build()

        okHttpClient.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (400 <= response.code) return onFailure()
                    val responseBody = response.body?.string()
                    response.close()

                    val result: Product = responseBody?.let {
                        parseJsonToProduct(it)
                    } ?: Product(-1, "", "dummy", 0)

                    onSuccess(result)
                }
            }
        )
    }

    private fun parseJsonToProductList(responseString: String): List<Product> {
        val products: MutableList<Product> = mutableListOf()

        val jsonArray = JSONArray(responseString)
        for (index in 0 until jsonArray.length()) {
            val jsonProduct = jsonArray.getJSONObject(index)
            val id = jsonProduct.getInt("id")
            val name = jsonProduct.getString("name")
            val imageUrl = jsonProduct.getString("imageUrl")
            val price = jsonProduct.getInt("price")

            val product = Product(id = id, name = name, imageUrl = imageUrl, price = price)
            products.add(product)
        }

        return products
    }

    private fun parseJsonToProduct(responseString: String): Product {
        val jsonObject = JSONObject(responseString)
        val id = jsonObject.getInt("id")
        val name = jsonObject.getString("name")
        val imageUrl = jsonObject.getString("imageUrl")
        val price = jsonObject.getInt("price")

        val product = Product(id = id, name = name, imageUrl = imageUrl, price = price)

        return product
    }
}
