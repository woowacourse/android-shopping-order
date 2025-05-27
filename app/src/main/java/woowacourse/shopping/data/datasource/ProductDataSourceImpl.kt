package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.data.model.response.ProductsResponse
import woowacourse.shopping.data.service.ProductService

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {

    override fun fetchProduct(
        id: Long,
        onResult: (ProductResponse) -> Unit,
    ) {
        productService.getProduct(id).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        if (body != null) {
                            onResult(body)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    override fun fetchProducts(
        page: Int,
        size: Int,
        onResult: (ProductsResponse) -> Unit,
    ) {
        productService.getProducts(page = page, size = size).enqueue(
            object : Callback<ProductsResponse> {
                override fun onResponse(
                    call: Call<ProductsResponse>,
                    response: Response<ProductsResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        if (body != null) {
                            onResult(body)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ProductsResponse>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }
}
