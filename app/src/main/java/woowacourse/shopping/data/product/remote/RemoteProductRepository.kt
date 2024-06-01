package woowacourse.shopping.data.product.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.RetrofitClient.retrofitApi
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.DataCallback
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

object RemoteProductRepository : ProductRepository {
    private const val MAX_PRODUCT_COUNT = 9999999
    private const val RECOMMEND_PRODUCTS_COUNT = 10

    override fun find(
        id: Int,
        dataCallback: DataCallback<Product>,
    ) {
        retrofitApi.requestProduct(id = id).enqueue(
            object : Callback<Content> {
                override fun onResponse(
                    call: Call<Content>,
                    response: Response<Content>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        dataCallback.onSuccess(body.toProduct())
                    }
                }

                override fun onFailure(
                    content: Call<Content>,
                    t: Throwable,
                ) {
                    dataCallback.onFailure(t)
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
        dataCallback: DataCallback<List<Product>>,
    ) {
        retrofitApi.requestProducts(page = page, size = pageSize).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        dataCallback.onSuccess(body.toProductList())
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    dataCallback.onFailure(t)
                }
            },
        )
    }

    override fun isPageLast(
        page: Int,
        pageSize: Int,
        dataCallback: DataCallback<Boolean>,
    ) {
        retrofitApi.requestProducts(page = page, size = pageSize).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        dataCallback.onSuccess(body.last)
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    dataCallback.onFailure(t)
                }
            },
        )
    }

    override fun findRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
        dataCallback: DataCallback<List<Product>>,
    ) {
        findCategoryProducts(
            category,
            object : DataCallback<List<Product>> {
                override fun onSuccess(result: List<Product>) {
                    val recommendCategoryProducts =
                        result
                            .filter { product -> cartItems.none { it.productId == product.id } }
                            .take(RECOMMEND_PRODUCTS_COUNT)
                    dataCallback.onSuccess(recommendCategoryProducts)
                }

                override fun onFailure(t: Throwable) {
                    dataCallback.onFailure(t)
                }
            },
        )
    }

    private fun findCategoryProducts(
        category: String,
        dataCallback: DataCallback<List<Product>>,
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
                            dataCallback.onSuccess(body.toProductList())
                        }
                    }

                    override fun onFailure(
                        call: Call<ProductResponse>,
                        t: Throwable,
                    ) {
                        dataCallback.onFailure(t)
                    }
                },
            )
    }
}
