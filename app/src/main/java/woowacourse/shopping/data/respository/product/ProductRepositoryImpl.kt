package woowacourse.shopping.data.respository.product

import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSource
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSourceImpl

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource = ProductRemoteDataSourceImpl(),
) : ProductRepository {
    override fun loadDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<ProductEntity>) -> Unit,
    ) {
        productRemoteDataSource.requestDatas(onFailure, onSuccess)
    }

    override fun loadDataById(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: ProductEntity) -> Unit,
    ) {
        productRemoteDataSource.requestData(productId, onFailure, onSuccess)
    }
}
