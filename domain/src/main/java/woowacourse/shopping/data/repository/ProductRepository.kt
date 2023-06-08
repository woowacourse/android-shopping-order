package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Product

interface ProductRepository {
    fun getAll(): Result<List<Product>>
    fun getNext(count: Int): Result<List<Product>>
    fun findById(id: Int): Result<Product>
}
