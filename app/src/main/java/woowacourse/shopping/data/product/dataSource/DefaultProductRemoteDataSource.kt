package woowacourse.shopping.data.product.dataSource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.product.remote.dto.ProductResponseDto
import woowacourse.shopping.data.product.remote.dto.ProductsResponseDto
import woowacourse.shopping.data.product.remote.service.ProductService

class DefaultProductRemoteDataSource(
    private val productService: ProductService,
) : ProductRemoteDataSource {
    override fun getProducts(
        page: Int,
        size: Int,
        onCallback: (Result<ProductsResponseDto?>) -> Unit,
    ) {
        productService
            .getProducts(page = page, size = size)
            .enqueue(
                object : Callback<ProductsResponseDto> {
                    override fun onResponse(
                        call: Call<ProductsResponseDto>,
                        response: Response<ProductsResponseDto>,
                    ) {
                        onCallback(Result.success(response.body()))
                    }

                    override fun onFailure(
                        call: Call<ProductsResponseDto?>,
                        t: Throwable,
                    ) {
                        onCallback(Result.failure(t))
                    }
                },
            )
    }

    override fun getProductDetail(
        productId: Long,
        onCallback: (Result<ProductResponseDto?>) -> Unit,
    ) {
        productService.getProductDetail(productId).enqueue(
            object : Callback<ProductResponseDto> {
                override fun onResponse(
                    call: Call<ProductResponseDto>,
                    response: Response<ProductResponseDto>,
                ) {
                    onCallback(Result.success(response.body()))
                }

                override fun onFailure(
                    call: Call<ProductResponseDto>,
                    t: Throwable,
                ) {
                    onCallback(Result.failure(t))
                }
            },
        )
    }
}
