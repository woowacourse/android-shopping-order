package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

interface RecentProductRepository {

    suspend fun insert(productId: Long): Result<Long, DataError>

    suspend fun getMostRecentProduct(): Result<RecentProduct, DataError>

    suspend fun getAllRecentProducts(): Result<List<RecentProduct>, DataError>

    suspend fun deleteAll(): Result<Unit, DataError>
}
