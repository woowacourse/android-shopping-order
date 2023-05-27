package woowacourse.shopping.repositoryImpl

import woowacourse.shopping.model.Product
import woowacourse.shopping.remoteDataSource.ProductRemoteDataSource
import woowacourse.shopping.remoteDataSourceImpl.ProductRemoteDataSourceImpl
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl : ProductRepository {
    private val remoteDataSource: ProductRemoteDataSource = ProductRemoteDataSourceImpl()

    override fun getAll(callback: (List<Product>?) -> Unit) {
        remoteDataSource.getAll(callback)
    }

    override fun getNext(count: Int, callback: (List<Product>?) -> Unit) {
        remoteDataSource.getNext(count, callback)
    }

    override fun insert(product: Product, callback: (Int) -> Unit) {
        remoteDataSource.insert(product, callback)
    }

    override fun findById(id: Int, callback: (Product?) -> Unit) {
        remoteDataSource.findById(id, callback)
    }
}
