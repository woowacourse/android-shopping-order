package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dataSource.ProductDataSource
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteDatabase: ProductDataSource,
) : ProductRepository {
    override fun getAll(callback: (List<Product>?) -> Unit) {
        remoteDatabase.getAll(callback)
    }

    override fun getNext(count: Int, callback: (List<Product>?) -> Unit) {
//        remoteDatabase.getNext(count, callback)
    }

    override fun findById(id: Int, callback: (Product?) -> Unit) {
        remoteDatabase.findById(id, callback)
    }
}
