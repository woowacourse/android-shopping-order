package woowacourse.shopping.fixture.fake

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result
import java.time.LocalDateTime

class FakeRecentRepository(initRecentItems: List<RecentProduct> = recentProducts) :
    RecentProductRepository {
    private val recentStubs: MutableList<RecentProduct> = initRecentItems.toMutableList()

    override suspend fun insert(productId: Long): Result<Long, DataError> =
        Result.Success(productId)

    override suspend fun getMostRecentProduct(): Result<RecentProduct, DataError> {
        val recent =
            recentStubs.maxByOrNull { it.recentTime } ?: return Result.Error(DataError.NotFound)
        return Result.Success(recent)
    }

    override suspend fun getAllRecentProducts(): Result<List<RecentProduct>, DataError> =
        Result.Success(recentProducts)

    override suspend fun deleteAll(): Result<Unit, DataError> = Result.Success(Unit)

    companion object {
        val recentProducts =
            (0..3).toList().map {
                RecentProduct(
                    it.toLong(),
                    FakeProductRepository.productStubs.elementAt(it).id,
                    LocalDateTime.now(),
                )
            }
    }
}
