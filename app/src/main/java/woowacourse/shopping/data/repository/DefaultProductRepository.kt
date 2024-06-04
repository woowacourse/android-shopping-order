package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.model.response.product.ProductContent
import woowacourse.shopping.data.datasource.remote.model.response.product.ProductResponse
import woowacourse.shopping.data.datasource.remote.model.response.product.toProduct
import woowacourse.shopping.data.datasource.remote.model.response.product.toProductList
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient.retrofitApi
import woowacourse.shopping.domain.model.Product
import kotlin.concurrent.thread

class DefaultProductRepository {
    fun findProducts(
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

    fun getIsPageLast(
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

    fun find(
        id: Int,
        dataCallback: DataCallback<Product>,
    ) {
        retrofitApi.requestProduct(id = id).enqueue(
            object : Callback<ProductContent> {
                override fun onResponse(
                    call: Call<ProductContent>,
                    response: Response<ProductContent>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        dataCallback.onSuccess(body.toProduct())
                    }
                }

                override fun onFailure(
                    productContent: Call<ProductContent>,
                    t: Throwable,
                ) {
                    dataCallback.onFailure(t)
                }
            },
        )
    }

    fun syncFind(id: Int): Product {
        var product: Product? = null
        thread {
            val response = retrofitApi.requestProduct(id = id).execute()
            val body = response.body()
            product = body?.toProduct()
        }.join()

        return product!!
    }
}
