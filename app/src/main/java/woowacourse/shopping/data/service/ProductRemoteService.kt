package woowacourse.shopping.data.service

import com.example.domain.model.Price
import com.example.domain.model.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.user.ServerInfo
import java.io.IOException

class ProductRemoteService {
    private val baseUrl: String
        get() = ServerInfo.url

    fun request(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        val request = Request.Builder()
            .url("${baseUrl}products")
            .build()

        OkHttpClient().newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code >= 400) return onFailure()
                    val responseBody = response.body?.string()
                    response.close()

                    val result = responseBody?.let {
                        parseJsonToProductList(it)
                    } ?: emptyList()

                    onSuccess(result)
                }
            },
        )
    }

    fun requestProduct(
        productId: Long,
        onSuccess: (Product) -> Unit,
        onFailure: () -> Unit,
    ) {
        val request = Request.Builder().url("${baseUrl}products/$productId").build()

        OkHttpClient().newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code >= 400) return onFailure()

                    val productObject =
                        response.body?.string()?.let { JSONObject(it) } ?: return onFailure()
                    response.close()
                    val result = parseJsonToProduct(productObject)

                    onSuccess(result)
                }
            },
        )
    }

    private fun parseJsonToProduct(jsonProduct: JSONObject): Product {
        val id = jsonProduct.getInt("id")
        val name = jsonProduct.getString("name")
        val price = jsonProduct.getInt("price")
        val imageUrl = jsonProduct.getString("imageUrl")

        return Product(id.toLong(), name, imageUrl, Price(price))
    }

    private fun parseJsonToProductList(responseString: String): List<Product> {
        val productList = mutableListOf<Product>()

        val jsonArray = JSONArray(responseString)
        for (i in 0 until jsonArray.length()) {
            val jsonProduct = jsonArray.getJSONObject(i)
            val id = jsonProduct.getInt("id")
            val name = jsonProduct.getString("name")
            val imageUrl = jsonProduct.getString("imageUrl")
            val jsonPrice = jsonProduct.getInt("price")

            val price = Price(jsonPrice)
            val product = Product(id.toLong(), name, imageUrl, price)
            productList.add(product)
        }

        return productList
    }
}
