package woowacourse.shopping.data.respository.product.source.remote

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.BASE_URL_POI
import woowacourse.shopping.data.model.ProductEntity
import java.io.IOException

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<ProductEntity>) -> Unit,
    ) {
        Thread {
            val client = OkHttpClient()
            val host = BASE_URL_POI
            val path = PRODUCT
            val request = Request.Builder().url(host + path).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("krrong", e.toString())
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
        }.start()
    }

    override fun requestData(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: ProductEntity) -> Unit,
    ) {
        Thread {
            val client = OkHttpClient()
            val host = BASE_URL_POI
            val path = "$PRODUCT/$productId"
            val request = Request.Builder().url(host + path).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("krrong", e.toString())
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
        }.start()
    }

    private fun parseProductList(response: String): List<ProductEntity> {
        val products = mutableListOf<ProductEntity>()
        val jsonArray = JSONArray(response)

        for (index in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(index) ?: continue
            products.add(parseProduct(json))
        }

        return products
    }

    private fun parseProduct(json: JSONObject): ProductEntity {
        val id = json.getLong("id")
        val name = json.getString("name")
        val price = json.getInt("price")
        val image = json.getString("imageUrl")

        return ProductEntity(id, name, price, image)
    }

    companion object {
        private const val PRODUCT = "/products"
    }
}
