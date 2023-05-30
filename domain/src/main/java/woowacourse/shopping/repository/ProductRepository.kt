package woowacourse.shopping.repository

import woowacourse.shopping.domain.product.Product

interface ProductRepository {
    fun findAll(limit: Int, offset: Int, onFinish: (List<Product>) -> Unit)

    fun countAll(onFinish: (Int) -> Unit)

    fun findById(id: Long, onFinish: (Product?) -> Unit)
}
