package woowacourse.shopping.data.product.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.RetrofitClient.retrofitApi
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

object RemoteProductRepository : ProductRepository {
    private const val MAX_PRODUCT_COUNT = 9999999
    private const val RECOMMEND_PRODUCTS_COUNT = 10

    override fun find(
        id: Int,
        callback: (Result<Product>) -> Unit,
    ) {
        retrofitApi.requestProduct(id = id).enqueue(
            object : Callback<Content> {
                override fun onResponse(
                    call: Call<Content>,
                    response: Response<Content>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        val result = body.toProduct()
                        callback(Result.success(result))
                    }
                }

                override fun onFailure(
                    content: Call<Content>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun syncFind(id: Int): Product {
        var product: Product? = null
        thread {
            val response = retrofitApi.requestProduct(id = id).execute()
            val body = response.body()
            product = body?.toProduct()
        }.join()

        return product!!
    }

    override fun findPage(
        page: Int,
        pageSize: Int,
        callback: (Result<List<Product>>) -> Unit,
    ) {
        retrofitApi.requestProducts(page = page, size = pageSize).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        val result = body.toProductList()
                        callback(Result.success(result))
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun isLastPage(
        page: Int,
        pageSize: Int,
        callback: (Result<Boolean>) -> Unit,
    ) {
        retrofitApi.requestProducts(page = page, size = pageSize).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        val result = body.last
                        callback(Result.success(result))
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun findRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
        callback: (Result<List<Product>>) -> Unit,
    ) {
        findCategoryProducts(category) {
            it.onSuccess { categoryProducts ->
                val recommendCategoryProducts =
                    categoryProducts
                        .filter { product -> cartItems.none { it.productId == product.id } }
                        .take(RECOMMEND_PRODUCTS_COUNT)

                callback(Result.success(recommendCategoryProducts))
            }.onFailure {
                callback(Result.failure(it))
            }
        }
    }

    private fun findCategoryProducts(
        category: String,
        callback: (Result<List<Product>>) -> Unit,
    ) {
        retrofitApi.requestProducts(category = category, page = 0, size = MAX_PRODUCT_COUNT)
            .enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body() ?: return
                            val result = body.toProductList()
                            callback(Result.success(result))
                        }
                    }

                    override fun onFailure(
                        call: Call<ProductResponse>,
                        t: Throwable,
                    ) {
                        callback(Result.failure(t))
                    }
                },
            )
    }
}
