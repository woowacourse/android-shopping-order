package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.Product

interface ProductLocalDataSource {
    fun getAll(callback: (Result<List<Product>>) -> Unit)
    fun getNext(count: Int, callback: (Result<List<Product>>) -> Unit)
    fun insert(product: Product, callback: (Int) -> Unit)
    fun findById(id: Int, callback: (Result<Product>) -> Unit)
    fun insertAll(it: List<Product>)
    fun isCached(): Boolean
    fun isEndOfCache(): Boolean
    fun clear()
}
