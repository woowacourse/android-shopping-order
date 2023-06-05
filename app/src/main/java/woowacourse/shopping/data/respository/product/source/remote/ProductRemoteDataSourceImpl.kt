package woowacourse.shopping.data.respository.product.source.remote

import android.util.Log
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.respository.product.service.ProductService
import woowacouse.shopping.model.product.Product

class ProductRemoteDataSourceImpl(
    private val productService: ProductService,
) : ProductRemoteDataSource {

    override fun requestDatas(
        onFailure: (message: String) -> Unit,
        onSuccess: (products: List<Product>) -> Unit,
    ) {
        productService.requestDatas().enqueue(object : retrofit2.Callback<List<ProductEntity>> {
            override fun onResponse(
                call: retrofit2.Call<List<ProductEntity>>,
                response: retrofit2.Response<List<ProductEntity>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        onSuccess(products.map { it.toModel() })
                    } ?: response.errorBody()?.let { onFailure(it.string()) }
                } else {
                    response.errorBody()?.let { onFailure(it.string()) }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<ProductEntity>>, t: Throwable) {
                Log.e("Request Failed", t.toString())
                onFailure(ERROR_CONNECT)
            }
        })
    }

    override fun requestData(
        productId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (products: Product) -> Unit,
    ) {
        productService.requestData(productId).enqueue(object : retrofit2.Callback<ProductEntity> {
            override fun onResponse(
                call: retrofit2.Call<ProductEntity>,
                response: retrofit2.Response<ProductEntity>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { product ->
                        onSuccess(product.toModel())
                    } ?: response.errorBody()?.let { onFailure(it.string()) }
                } else {
                    response.errorBody()?.let { onFailure(it.string()) }
                }
            }

            override fun onFailure(call: retrofit2.Call<ProductEntity>, t: Throwable) {
                Log.e("Request Failed", t.toString())
                onFailure(ERROR_CONNECT)
            }
        })
    }

    companion object {
        private const val ERROR_CONNECT = "연결에 실패하였습니다. 다시 시도해주세요"
    }
}
