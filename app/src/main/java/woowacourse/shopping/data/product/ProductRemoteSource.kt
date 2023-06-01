package woowacourse.shopping.data.product

import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.network.RetrofitErrorHandlerProvider
import woowacourse.shopping.network.retrofit.ProductRetrofitService

class ProductRemoteSource(private val productService: ProductRetrofitService) : ProductDataSource {
    override fun findAll(onFinish: (Result<List<ProductEntity>>) -> Unit) {
        productService.selectProducts()
            .enqueue(RetrofitErrorHandlerProvider.callbackWithBody(200, onFinish))
    }

    override fun findRanged(
        limit: Int,
        offset: Int,
        onFinish: (Result<List<ProductEntity>>) -> Unit
    ) {
        productService.selectProducts().enqueue(
            RetrofitErrorHandlerProvider.callbackWithCustomBody(
                200, onFinish
            ) { products ->
                products.slice(offset until products.size).take(limit)
            }
        )
    }

    override fun countAll(onFinish: (Result<Int>) -> Unit) {
        findAll { result ->
            result.onSuccess {
                onFinish(Result.success(it.size))
            }.onFailure {
                onFinish(Result.failure(it))
            }
        }
    }

    override fun findById(id: Long, onFinish: (Result<ProductEntity>) -> Unit) {
        productService.selectProduct(id)
            .enqueue(RetrofitErrorHandlerProvider.callbackWithBody(200, onFinish))
    }
}
