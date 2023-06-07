package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.product.response.GetProductResponse
import woowacourse.shopping.data.dto.mapper.ProductMapper.toDomain
import woowacourse.shopping.data.server.ProductRemoteDataSource
import woowacourse.shopping.domain.Product

class DefaultProductRemoteDataSource(private val service: ProductService) : ProductRemoteDataSource {
    override fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: (String) -> Unit) {
        service.requestProducts().enqueue(object : Callback<List<GetProductResponse>> {
            override fun onResponse(
                call: Call<List<GetProductResponse>>,
                response: Response<List<GetProductResponse>>
            ) {
                if(response.isSuccessful) {
                    onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_PRODUCTS_FAILED })
                }
            }

            override fun onFailure(call: Call<List<GetProductResponse>>, t: Throwable) {
                onFailure(MESSAGE_GET_PRODUCTS_FAILED)
            }
        })
    }

    override fun getProduct(id: Int, onSuccess: (Product) -> Unit, onFailure: (String) -> Unit) {
        service.requestProduct(id).enqueue(object : Callback<GetProductResponse> {
            override fun onResponse(call: Call<GetProductResponse>, response: Response<GetProductResponse>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.toDomain())
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_PRODUCT_FAILED })
                }
            }

            override fun onFailure(call: Call<GetProductResponse>, t: Throwable) {
                onFailure(MESSAGE_GET_PRODUCT_FAILED)
            }
        })
    }

    companion object {
        private const val MESSAGE_GET_PRODUCTS_FAILED = "상품을 불러오는데 실패했습니다."
        private const val MESSAGE_GET_PRODUCT_FAILED = "상품 정보를 불러오는데 실패했습니다."
    }
}