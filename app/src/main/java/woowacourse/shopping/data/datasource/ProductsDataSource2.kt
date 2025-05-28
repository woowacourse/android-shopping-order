package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.network.response.products.ProductResponse
import woowacourse.shopping.data.network.response.products.ProductsResponse
import woowacourse.shopping.data.network.service.ProductService

class ProductsDataSource2(private val service: ProductService) {
    // fun getProduct(productId: Long): ProductEntity = service.getProduct(productId)

//    fun getProducts(productIds: List<Long>): List<ProductEntity> {
//        //return service.getProducts(productIds)
//    }

    fun singlePage(
        page: Int,
        size: Int,
        callback: (Result<ProductsResponse?>) -> Unit,
    ) {
        service.requestProducts(page, size).enqueue(
            object :
                Callback<ProductsResponse> {
                override fun onResponse(
                    call: Call<ProductsResponse>,
                    response: Response<ProductsResponse?>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        callback(Result.success(body))
                    } else {
                        callback(Result.failure(NullPointerException("응답이 비어 있습니다.")))
                    }
                }

                override fun onFailure(
                    call: Call<ProductsResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    fun getProduct(
        productId: Long,
        callback: (Result<ProductResponse?>) -> Unit,
    ) {
        service.getProduct(productId).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        callback(Result.success(body))
                    } else {
                        callback(Result.failure(HttpException(response)))
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }

            }
        )
    }
}
