package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.history.HistoryLocalDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val dataSource: HistoryLocalDataSource,
) : HistoryRepository {
    override suspend fun fetchAllHistory(): Result<List<HistoryProduct>> =
        runCatching {
            dataSource.getHistoryProducts().map { it.toDomain() }
        }

    override suspend fun addHistoryWithLimit(
        productDetail: ProductDetail,
        limit: Int,
    ): Result<Unit> =
        runCatching {
            dataSource.insertHistoryWithLimit(
                productId = productDetail.id,
                name = productDetail.name,
                imageUrl = productDetail.imageUrl,
                category = productDetail.category,
                limit = limit,
            )
        }

    override suspend fun fetchRecentHistory(): Result<HistoryProduct?> =
        runCatching {
            dataSource.getRecentHistoryProduct()?.toDomain()
        }
}
