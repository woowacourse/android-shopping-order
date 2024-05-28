package woowacourse.shopping.data.shopping.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.remote.dto.response.ProductResponse
import woowacourse.shopping.remote.service.ProductService

class DefaultProductDataSource(
    private val productService: ProductService,
) : ProductDataSource {
    override fun products(
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        return runCatching {
            productService.fetchProducts(
                currentPage, size
            ).enqueue(object : Callback<ProductResponse> {
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
            }))
        }
    }

    override fun productById(id: Long): Result<Product> {
        return runCatching {
            productService.fetchDetailProduct(id).toProduct()
        }
    }

    override fun canLoadMore(
        page: Int,
        size: Int,
    ): Result<Boolean> {
        return runCatching {
//            productService.canLoadMore(page, size)
            true
        }
    }
}
