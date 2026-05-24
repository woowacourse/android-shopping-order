package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.domain.model.product.Product

interface RecentlyViewedProductRepository {
    fun getAll(): Flow<List<Long>?>

    suspend fun updateList(product: Product)

    fun getLatestItem(): Flow<Long?>
}
