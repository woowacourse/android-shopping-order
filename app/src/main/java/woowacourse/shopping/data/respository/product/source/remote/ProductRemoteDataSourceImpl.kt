package woowacourse.shopping.data.respository.product.source.remote

import retrofit2.Retrofit
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.respository.product.source.remote.service.ProductService

class ProductRemoteDataSourceImpl(
    retrofit: Retrofit,
) : ProductRemoteDataSource {

    private val productService = retrofit.create(ProductService::class.java)

    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    ) {
        productService.requestProducts()
            .enqueue(object : retrofit2.Callback<List<ProductEntity>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ProductEntity>>,
                    response: retrofit2.Response<List<ProductEntity>>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return onFailure()
                        val products = body.map { productModel ->
                            CartRemoteEntity(
                                DUMMY_CART_ID,
                                DEFAULT_QUANTITY,
                                productModel,
                            )
                        }
                        onSuccess(products)
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductEntity>>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestData(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (product: CartRemoteEntity) -> Unit,
    ) {
        productService.requestProductById(productId)
            .enqueue(object : retrofit2.Callback<ProductEntity> {
                override fun onResponse(
                    call: retrofit2.Call<ProductEntity>,
                    response: retrofit2.Response<ProductEntity>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return onFailure()
                        val product =
                            CartRemoteEntity(DUMMY_CART_ID, DEFAULT_QUANTITY, body)
                        onSuccess(product)
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductEntity>, t: Throwable) {
                    onFailure()
                }
            })
    }

    companion object {
        private const val DUMMY_CART_ID = 99999L
        private const val DEFAULT_QUANTITY = 1
    }
}
