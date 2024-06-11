package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.RecentlyDataSource
import woowacourse.shopping.domain.model.product.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.utils.exception.ErrorEvent

class RecentlyProductRepositoryImpl(
    private val recentlyDataSource: RecentlyDataSource,
) : RecentlyProductRepository {

    override suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Unit> {
        return recentlyDataSource.addRecentlyProduct(recentlyProduct)
    }

    override suspend fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        return recentlyDataSource.getMostRecentlyProduct()
    }

    override suspend fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        return recentlyDataSource.getRecentlyProducts()
            .recoverCatching {
                throw ErrorEvent.LoadDataEvent()
            }
    }

    override suspend fun deleteRecentlyProduct(id: Long): Result<Unit> {
        return recentlyDataSource.deleteRecentlyProduct(id)
    }
}
