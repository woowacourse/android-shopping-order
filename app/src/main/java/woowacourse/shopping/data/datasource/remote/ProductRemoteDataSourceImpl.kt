package woowacourse.shopping.data.datasource.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.handleFailure
import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.dto.product.ProductsResponse
import woowacourse.shopping.data.dto.product.toDomain
import woowacourse.shopping.data.remote.ProductService
import woowacourse.shopping.domain.model.Product

class ProductRemoteDataSourceImpl(
    private val productService: ProductService,
) : ProductRemoteDataSource {
    override fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        productService.requestProducts(page, pageSize, category).enqueue(
            object : Callback<ProductsResponse> {
                override fun onResponse(
                    call: Call<ProductsResponse>,
                    response: Response<ProductsResponse>,
                ) {
                    if (response.isSuccessful) {
                        val products = response.body()?.content?.map { it.toDomain() }
                        if (products.isNullOrEmpty()) {
                            onResult(Result.failure(NoSuchElementException("상품 데이터가 없습니다.")))
                        } else {
                            onResult(Result.success(products))
                        }
                        return
                    }
                    handleFailure(onResult)
                }

                override fun onFailure(
                    call: Call<ProductsResponse>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    override fun fetchProductById(
        id: Long,
        onResult: (Result<Product>) -> Unit,
    ) {
        productService.requestProductById(id).enqueue(
            object : Callback<ProductContent> {
                override fun onResponse(
                    call: Call<ProductContent>,
                    response: Response<ProductContent>,
                ) {
                    if (response.isSuccessful) {
                        val product = response.body()?.toDomain()
                        if (product != null) {
                            onResult(Result.success(product))
                        } else {
                            onResult(Result.failure(NoSuchElementException("상품 데이터가 없습니다.")))
                        }
                        return
                    }
                    handleFailure(onResult)
                }

                override fun onFailure(
                    call: Call<ProductContent>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }
}
