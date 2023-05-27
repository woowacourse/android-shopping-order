package woowacourse.shopping.data.localDataSource

import woowacourse.shopping.model.Product

interface ProductLocalDataSource {
    fun getAll(callback: (List<Product>?) -> Unit)
    fun getNext(count: Int, callback: (List<Product>?) -> Unit)
    fun insert(product: Product, callback: (Int) -> Unit)
    fun findById(id: Int, callback: (Product?) -> Unit)
    fun insertAll(it: List<Product>?)
    fun isCached(): Boolean
    fun isEndOfCache(): Boolean
    fun clear()
}
