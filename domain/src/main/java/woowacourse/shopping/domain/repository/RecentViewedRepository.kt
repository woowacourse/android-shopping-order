package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecentViewedRepository {
    fun findAll(callBack: (List<Product>) -> Unit)
    fun add(id: Int)
    fun remove(id: Int)
}
