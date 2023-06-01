package woowacourse.shopping.data.respository.product

import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSource

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun loadDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    ) {
        productRemoteDataSource.requestDatas(onFailure, onSuccess)
    }

    override fun loadDataById(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: CartRemoteEntity) -> Unit,
    ) {
        productRemoteDataSource.requestData(productId, onFailure, onSuccess)
    }
}
