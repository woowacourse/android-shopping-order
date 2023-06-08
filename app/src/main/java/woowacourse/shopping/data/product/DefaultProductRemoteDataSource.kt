package woowacourse.shopping.data.product

import android.os.Handler
import android.os.Looper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.product.response.GetProductResponse
import woowacourse.shopping.data.dto.mapper.ProductMapper.toDomain
import woowacourse.shopping.data.server.ProductRemoteDataSource
import woowacourse.shopping.domain.Product

class DefaultProductRemoteDataSource(private val service: ProductService) : ProductRemoteDataSource {
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: (String) -> Unit) {
        service.requestProducts().enqueue(object : Callback<List<GetProductResponse>> {
            override fun onResponse(
                call: Call<List<GetProductResponse>>,
                response: Response<List<GetProductResponse>>
            ) {
                if(response.isSuccessful) {
                    postToMainHandler { onSuccess(response.body()?.map { it.toDomain() } ?: emptyList()) }
                }
                else {
                    postToMainHandler { onFailure(response.message().ifBlank { MESSAGE_GET_PRODUCTS_FAILED }) }
                }
            }

            override fun onFailure(call: Call<List<GetProductResponse>>, t: Throwable) {
                postToMainHandler { onFailure(MESSAGE_GET_PRODUCTS_FAILED) }
            }
        })
    }

    override fun getProduct(id: Int, onSuccess: (Product) -> Unit, onFailure: (String) -> Unit) {
        service.requestProduct(id).enqueue(object : Callback<GetProductResponse> {
            override fun onResponse(call: Call<GetProductResponse>, response: Response<GetProductResponse>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    postToMainHandler { onSuccess(responseBody.toDomain()) }
                }
                else {
                    postToMainHandler { onFailure(response.message().ifBlank { MESSAGE_GET_PRODUCT_FAILED }) }
                }
            }

            override fun onFailure(call: Call<GetProductResponse>, t: Throwable) {
                postToMainHandler { onFailure(MESSAGE_GET_PRODUCT_FAILED) }
            }
        })
    }

    private fun postToMainHandler(block: () -> Unit) {
        mainHandler.post {
            block()
        }
    }

    companion object {
        private const val MESSAGE_GET_PRODUCTS_FAILED = "상품을 불러오는데 실패했습니다."
        private const val MESSAGE_GET_PRODUCT_FAILED = "상품 정보를 불러오는데 실패했습니다."
    }
}