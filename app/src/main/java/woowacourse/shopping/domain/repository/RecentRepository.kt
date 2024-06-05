package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecentRepository {
    fun loadAll(): Result<List<Product>>

    fun loadMostRecent(): Result<Product?>

    fun add(recentProduct: Product): Result<Long>
}
