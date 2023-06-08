package woowacourse.shopping.data.datasource

import woowacourse.shopping.domain.model.Product

interface RecentViewedDataSource {
    fun findAll(callback: (List<Product>) -> Unit)
    fun add(product: Product)
    fun remove(id: Int)
}
