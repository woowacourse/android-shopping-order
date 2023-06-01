package woowacourse.shopping.repository

import woowacourse.shopping.domain.product.Product

interface ProductRepository {
    fun findAll(limit: Int, offset: Int, onFinish: (Result<List<Product>>) -> Unit)

    fun countAll(onFinish: (Result<Int>) -> Unit)

    fun findById(id: Long, onFinish: (Result<Product>) -> Unit)
}
