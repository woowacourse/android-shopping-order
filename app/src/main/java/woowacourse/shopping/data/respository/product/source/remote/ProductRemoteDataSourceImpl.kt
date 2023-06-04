package woowacourse.shopping.data.respository.product.source.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.view.util.RetrofitService

class ProductRemoteDataSourceImpl(
    server: Server,
) : ProductRemoteDataSource {

    private val productService =
        Retrofit.Builder()
            .baseUrl(server.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    ) {
        productService.requestProducts()
            .enqueue(object : retrofit2.Callback<List<ProductModel>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ProductModel>>,
                    response: retrofit2.Response<List<ProductModel>>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return onFailure()
                        val products = body.map { productModel ->
                            CartRemoteEntity(
                                DUMMY_CART_ID,
                                DEFAULT_QUANTITY,
                                productModel.toEntity(),
                            )
                        }
                        onSuccess(products)
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductModel>>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestData(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: CartRemoteEntity) -> Unit,
    ) {
        productService.requestProductById(productId)
            .enqueue(object : retrofit2.Callback<ProductModel> {
                override fun onResponse(
                    call: retrofit2.Call<ProductModel>,
                    response: retrofit2.Response<ProductModel>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return onFailure()
                        val product =
                            CartRemoteEntity(DUMMY_CART_ID, DEFAULT_QUANTITY, body.toEntity())
                        onSuccess(product)
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductModel>, t: Throwable) {
                    onFailure()
                }
            })
    }

    companion object {
        private const val DUMMY_CART_ID = 99999L
        private const val DEFAULT_QUANTITY = 1
    }
}
