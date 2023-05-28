package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Product

interface ProductRepository {
    fun getAll(callback: (Result<List<Product>>) -> Unit)
    fun getNext(count: Int, callback: (Result<List<Product>>) -> Unit)
    fun insert(product: Product, callback: (Result<Int>) -> Unit)
    fun findById(id: Int, callback: (Result<Product>) -> Unit)
    fun clearCache()
}
