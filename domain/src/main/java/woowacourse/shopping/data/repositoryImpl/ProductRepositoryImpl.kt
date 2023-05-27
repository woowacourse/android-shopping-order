package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.remoteDataSource.ProductRemoteDataSource
import woowacourse.shopping.data.remoteDataSourceImpl.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.model.Product

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
