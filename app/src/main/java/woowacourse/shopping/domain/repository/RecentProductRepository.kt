package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface RecentProductRepository {
    fun getRecentProducts(
        limit: Int,
        onResult: (Result<List<Product>>) -> Unit,
    )

    fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        onResult: (Result<Unit>) -> Unit,
    )
}
