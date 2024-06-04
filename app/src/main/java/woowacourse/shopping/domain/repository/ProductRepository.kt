package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun find(id: Int): Product

    fun findRange(
        page: Int,
        pageSize: Int,
    ): List<Product>
}
