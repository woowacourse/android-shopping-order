package woowacourse.shopping.data.product

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import woowacourse.shopping.App
import woowacourse.shopping.Product
import woowacourse.shopping.data.mapper.toProduct

object ProductService : ProductRemoteDataSource {
    override fun findProductById(id: Int): Product {
        var product: Product?
        var number = 0
        while (number < 400) {
            product = getAllProducts().find { it.id == id }
            if (product != null) return product
            number += 20
        }
        return Product.defaultProduct
    }

    override fun getAllProducts(): List<Product> {
        var newProducts: List<Product> = emptyList()
        val thread = Thread {
            val client = OkHttpClient()
            val host = App.serverUrl
            val path = "products"
            val request = Request.Builder().url(host + path).build()
            Log.d("wooseok", host + path)
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@Thread
            Log.d("wooseok", body)
            newProducts = parseResponse(body)
        }
        thread.start()
        thread.join()

        return newProducts
    }

    private fun parseResponse(responseBody: String?): List<Product> {
        return responseBody?.let {
            val productsJsonArray = JSONArray(it)
            val products = mutableListOf<Product>()
            for (item in 0 until productsJsonArray.length()) {
                val jsonObject = productsJsonArray.getJSONObject(item)
                products.add(jsonObject.toProduct())
            }
            products
        } ?: emptyList()
    }
}
