package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun fetchProduct(
        id: Int,
        onResult: (Result<Product>) -> Unit,
    ) {
        val call = productRemoteDataSource.fetchProduct(id)
        call.enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { productResponse ->
                            onResult(Result.success(productResponse.toDomain()))
                        } ?: onResult(Result.failure(Exception()))
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    override fun fetchProducts(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<Product>>) -> Unit,
    ) {
        val call = productRemoteDataSource.fetchProducts(null, page, size)
        call.enqueue(
            object : Callback<PageableResponse<ProductResponse>> {
                override fun onResponse(
                    call: Call<PageableResponse<ProductResponse>>,
                    response: Response<PageableResponse<ProductResponse>>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { productResponse ->
                            val products = productResponse.content.map { it.toDomain() }
                            val hasMore = !productResponse.last
                            onResult(Result.success(PageableItem(products, hasMore)))
                        } ?: onResult(Result.failure(Exception()))
                    }
                }

                override fun onFailure(
                    call: Call<PageableResponse<ProductResponse>>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }
}
