package woowacourse.shopping.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.product.ProductResponse
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.product.catalog.ProductUiModel
import kotlin.concurrent.thread

class HttpCatalogProductRepositoryImpl(
    private val baseUrl: String,
    private val client: OkHttpClient = OkHttpClient(),
) : CatalogProductRepository {
    val retrofitService = RetrofitProductService.INSTANCE.create(ProductService::class.java)

    private var cachedProducts: List<ProductUiModel>? = null

    private fun getAllProducts(callback: (List<ProductUiModel>) -> Unit) {
        if (cachedProducts != null) {
            callback(cachedProducts!!)
            return
        }

        retrofitService.requestProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    println("body : $body")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                println("error : $t")
            }
        })

        thread {
            try {
                val request =
                    Request
                        .Builder()
                        .url("$baseUrl/products")
                        .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""

                val gson = Gson()
                val type = object : TypeToken<List<ProductUiModel>>() {}.type
                val products = gson.fromJson<List<ProductUiModel>>(body, type)

                cachedProducts = products
                callback(products)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(emptyList())
            }
        }
    }

    override fun getAllProductsSize(callback: (Int) -> Unit) {
        getAllProducts { products ->
            callback(products.size)
        }
    }

    override fun getProductsInRange(
        startIndex: Int,
        endIndex: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        getAllProducts { products ->
            callback(products.subList(startIndex, minOf(endIndex, products.size)))
        }
    }

    override fun getCartProductsByUids(
        uids: List<Int>,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        getAllProducts { products ->
            callback(products.filter { it.id in uids })
        }
    }

    override fun getProductsByPage(
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}
