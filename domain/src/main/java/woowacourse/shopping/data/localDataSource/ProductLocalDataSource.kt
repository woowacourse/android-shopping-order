package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.Product

interface ProductLocalDataSource {
    fun getAll(): Result<List<Product>>
    fun getNext(count: Int): Result<List<Product>>
    fun insert(product: Product): Result<Int>
    fun findById(id: Int): Result<Product>
    fun insertAll(it: List<Product>)
    fun isCached(): Boolean
    fun isEndOfCache(): Boolean
    fun clear()
}
