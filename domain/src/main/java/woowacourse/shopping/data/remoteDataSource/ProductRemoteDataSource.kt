package woowacourse.shopping.data.remoteDataSource

import woowacourse.shopping.model.Product

interface ProductRemoteDataSource {
    fun getAll(): Result<List<Product>>
    fun getNext(count: Int): Result<List<Product>>
    fun insert(product: Product): Result<Int>
    fun findById(id: Int): Result<Product>
}
