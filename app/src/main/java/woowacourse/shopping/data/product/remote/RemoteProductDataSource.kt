package woowacourse.shopping.data.product.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.dto.response.ProductResponse
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.data.remote.ApiClient
import woowacourse.shopping.domain.Product

class RemoteProductDataSource(
    private val productApiService: ProductApiService =
        ApiClient.getApiClient().create(
            ProductApiService::class.java,
        ),
) : ProductDataSource {
    override fun getProductById(
        productId: Long,
        callBack: (result: NetworkResult<Product>) -> Unit,
    ) {
        productApiService.requestProductDetail(productId = productId.toInt()).enqueue(
            object : Callback<ProductDto> {
                override fun onResponse(
                    call: Call<ProductDto>,
                    response: Response<ProductDto>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callBack(NetworkResult.Success(it.toProduct()))
                        }
                    } else {
                        callBack(NetworkResult.Error)
                    }
                }

                override fun onFailure(
                    call: Call<ProductDto>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }

    override fun getProducts(
        startPage: Int,
        pageSize: Int,
        callBack: (result: NetworkResult<List<Product>>) -> Unit,
    ) {
        productApiService.requestProducts(page = startPage, size = pageSize).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val products = response.body()?.content?.map { it.toProduct() } ?: emptyList()
                        callBack(NetworkResult.Success(products))
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }

    override fun getProductsByCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
        callBack: (result: NetworkResult<List<Product>>) -> Unit,
    ) {
        productApiService.requestProductsWithCategory(category = category, page = startPage, size = pageSize).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val products = response.body()?.content?.map { it.toProduct() } ?: emptyList()
                        callBack(NetworkResult.Success(products))
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }
}
