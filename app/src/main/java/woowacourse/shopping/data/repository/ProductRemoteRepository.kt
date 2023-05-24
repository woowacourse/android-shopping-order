package woowacourse.shopping.data.repository

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import java.io.IOException

class ProductRemoteRepository(baseUrl: String) : ProductRepository {
    private val client =
        OkHttpClient.Builder().build()
    private val request = Request.Builder().url("$baseUrl/products").build()

    override fun getAll(callBack: (List<Product>) -> Unit) {
        executeRequest(request, callBack)
    }

    override fun getProduct(id: Int, callBack: (Product?) -> Unit) {
        fun callBackWrapper(products: List<Product>) {
            callBack(products.find { it.id == id })
        }
        executeRequest(request, ::callBackWrapper)
    }

    override fun getProductsByRange(mark: Int, rangeSize: Int, callBack: (List<Product>) -> Unit) {
        fun callBackWrapper(products: List<Product>) {
            if (mark + rangeSize >= products.size) {
                callBack(products.subList(mark, products.size))
            } else {
                callBack(products.subList(mark, mark + rangeSize))
            }
        }
        executeRequest(request, ::callBackWrapper)
    }

    override fun isExistByMark(mark: Int, callBack: (Boolean) -> Unit) {
        fun callBackWrapper(products: List<Product>) {
            callBack(products.size > mark)
        }
        executeRequest(request, ::callBackWrapper)
    }

    override fun getProductsById(ids: List<Int>, callBack: (List<Product>) -> Unit) {
        fun callBackWrapper(products: List<Product>) {
            val products = ids.map { id -> products.first { product -> product.id == id } }
            callBack(products)
        }
        executeRequest(request, ::callBackWrapper)
    }

    private fun executeRequest(request: Request, callBack: (List<Product>) -> Unit) {
        var responseBody: String?
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    throw java.lang.RuntimeException("Request Failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    responseBody = response.body?.string()
                    callBack(parseResponse(responseBody))
                    response.close()
                }
            },
        )
    }

    private fun parseResponse(responseBody: String?): List<Product> {
        return responseBody?.let {
            val productsJSONArray = JSONArray(it)
            val products = mutableListOf<Product>()
            for (index in 0 until productsJSONArray.length()) {
                val productJSON = productsJSONArray.getJSONObject(index)
                val product = Product.fromJson(productJSON)
                products.add(product)
            }
            products
        } ?: emptyList()
    }
}
