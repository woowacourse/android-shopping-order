package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Product

interface ProductRepository {
    fun getAll(): Result<List<Product>>
    fun getNext(count: Int): Result<List<Product>>
    fun insert(product: Product): Result<Int>
    fun findById(id: Int): Result<Product>
    fun clearCache()
}
