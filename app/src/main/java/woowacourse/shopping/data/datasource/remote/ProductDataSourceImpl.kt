package woowacourse.shopping.data.datasource.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.dto.product.ProductsResponse
import woowacourse.shopping.data.dto.product.toDomain
import woowacourse.shopping.data.remote.ProductService
import woowacourse.shopping.domain.model.Product

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {
    override fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
        onResult: (List<Product>) -> Unit,
    ) {
        productService.requestProducts(page, pageSize, category).enqueue(
            object : Callback<ProductsResponse> {
                override fun onResponse(
                    call: Call<ProductsResponse>,
                    response: Response<ProductsResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body =
                            response.body()?.content?.map { it.toDomain() } ?: emptyList()
                        onResult(body)
                    }
                }

                override fun onFailure(
                    call: Call<ProductsResponse>,
                    t: Throwable,
                ) {
                    onResult(emptyList())
                }
            },
        )
    }

    override fun fetchProductById(
        id: Long,
        onResult: (Product) -> Unit,
    ) {
        productService.requestProductById(id).enqueue(
            object : Callback<ProductContent> {
                override fun onResponse(
                    call: Call<ProductContent>,
                    response: Response<ProductContent>,
                ) {
                    if (response.isSuccessful) {
                        val product =
                            response.body()?.toDomain()
                                ?: throw NoSuchElementException("해당 id의 상품을 찾지 못했습니다.")
                        onResult(product)
                    }
                }

                override fun onFailure(
                    call: Call<ProductContent>,
                    t: Throwable,
                ) {
                }
            },
        )
    }
}
