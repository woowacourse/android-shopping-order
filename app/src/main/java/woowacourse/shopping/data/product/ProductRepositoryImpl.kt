package woowacourse.shopping.data.product

import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.remote.datasource.RemoteProductDataSource

class ProductRepositoryImpl(private val remoteProductDataSource: ProductDataSource = RemoteProductDataSource()) : ProductRepository {
    override fun loadWithCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
        callBack: (result: NetworkResult<List<Product>>) -> Unit,
    ) {
        remoteProductDataSource.getProductsByCategory(category, startPage, pageSize, callBack)
    }

    override fun load(
        startPage: Int,
        pageSize: Int,
        callBack: (result: NetworkResult<List<Product>>) -> Unit,
    ) {
        remoteProductDataSource.getProducts(startPage, pageSize, callBack)
    }

    override fun loadById(
        id: Long,
        callback: (result: NetworkResult<Product>) -> Unit,
    ) {
        remoteProductDataSource.getProductById(productId = id, callback)
    }
}
