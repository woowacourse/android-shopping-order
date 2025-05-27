package woowacourse.shopping.data.datasource.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.cart.toDomain
import woowacourse.shopping.data.dto.product.ProductsResponse
import woowacourse.shopping.data.remote.ProductService
import woowacourse.shopping.domain.model.Product

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {
    override fun fetchPagingProducts(
        page: Int,
        pageSize: Int,
        onResult: (List<Product>) -> Unit,
    ) {
        productService.requestProducts(page = page).enqueue(
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

    override fun fetchProductById(id: Long): Product = productService.fetchProductById(id)
}
