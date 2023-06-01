package woowacourse.shopping.repository

import woowacourse.shopping.domain.product.Product

interface ProductRepository {
    fun findAll(limit: Int, offset: Int): Result<List<Product>>

    fun countAll(): Result<Int>

    fun findById(id: Long): Result<Product>
}
