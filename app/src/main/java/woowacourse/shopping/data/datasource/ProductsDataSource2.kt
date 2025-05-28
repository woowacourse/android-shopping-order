package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.network.response.products.ProductsResponse
import woowacourse.shopping.data.network.service.ProductService

class ProductsDataSource2(private val service: ProductService) {
    // fun getProduct(productId: Long): ProductEntity = service.getProduct(productId)

//    fun getProducts(productIds: List<Long>): List<ProductEntity> {
//        //return service.getProducts(productIds)
//    }

    fun singlePage(
        fromIndex: Int,
        toIndex: Int,
        callback: (Result<ProductsResponse?>) -> Unit,
    ) {
        service.requestProducts(fromIndex, toIndex).enqueue(
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
}
