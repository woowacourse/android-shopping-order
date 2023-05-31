package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecentViewedRepository {
    fun findAll(callback: (List<Product>) -> Unit)
    fun add(product: Product)
    fun remove(id: Int)
}
