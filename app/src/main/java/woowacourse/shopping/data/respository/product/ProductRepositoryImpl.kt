package woowacourse.shopping.data.respository.product

import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSource
import woowacouse.shopping.data.repository.product.ProductRepository
import woowacouse.shopping.model.product.Product

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun loadDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<Product>) -> Unit,
    ) {
        productRemoteDataSource.requestDatas(onFailure, onSuccess)
    }

    override fun loadDataById(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: Product) -> Unit,
    ) {
        productRemoteDataSource.requestData(productId, onFailure, onSuccess)
    }
}
