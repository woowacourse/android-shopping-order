package woowacourse.shopping.data.respository.product

import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSource
import woowacouse.shopping.data.repository.product.ProductRepository
import woowacouse.shopping.model.product.Product

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun loadDatas(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<Product>) -> Unit,
    ) {
        productRemoteDataSource.requestDatas(onFailure) { products ->
            onSuccess(products.map { it.toModel() })
        }
    }

    override fun loadDataById(
        productId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: Product) -> Unit,
    ) {
        productRemoteDataSource.requestData(productId, onFailure) { product ->
            onSuccess(product.toModel())
        }
    }
}
