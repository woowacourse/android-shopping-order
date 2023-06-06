package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.data.entity.mapper.ProductMapper.toDomain
import woowacourse.shopping.data.server.ProductRemoteDataSource
import woowacourse.shopping.domain.Product

class DefaultProductRemoteDataSource(private val service: ProductService) : ProductRemoteDataSource {
    override fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: (String) -> Unit) {
        service.requestProducts().enqueue(object : Callback<List<ProductEntity>> {
            override fun onResponse(
                call: Call<List<ProductEntity>>,
                response: Response<List<ProductEntity>>
            ) {
                if(response.isSuccessful) {
                    onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_PRODUCTS_FAILED })
                }
            }

            override fun onFailure(call: Call<List<ProductEntity>>, t: Throwable) {
                onFailure(MESSAGE_GET_PRODUCTS_FAILED)
            }
        })
    }

    override fun getProduct(id: Int, onSuccess: (Product) -> Unit, onFailure: (String) -> Unit) {
        service.requestProduct(id).enqueue(object : Callback<ProductEntity> {
            override fun onResponse(call: Call<ProductEntity>, response: Response<ProductEntity>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.toDomain())
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_GET_PRODUCT_FAILED })
                }
            }

            override fun onFailure(call: Call<ProductEntity>, t: Throwable) {
                onFailure(MESSAGE_GET_PRODUCT_FAILED)
            }
        })
    }

    companion object {
        private const val MESSAGE_GET_PRODUCTS_FAILED = "상품을 불러오는데 실패했습니다."
        private const val MESSAGE_GET_PRODUCT_FAILED = "상품 정보를 불러오는데 실패했습니다."
    }
}