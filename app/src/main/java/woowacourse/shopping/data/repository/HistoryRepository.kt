package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dao.HistoryDao
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.model.entity.HistoryProductEntity
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.repository.HistoryRepository

class HistoryRepository(
    private val dao: HistoryDao,
) : HistoryRepository {
    override suspend fun fetchAllHistory(): Result<List<HistoryProduct>> =
        runCatching {
            dao.getHistoryProducts().map { it.toDomain() }
        }

    override suspend fun addHistoryWithLimit(
        productDetail: ProductDetail,
        limit: Int,
    ): Result<Unit> =
        runCatching {
            dao.insertHistoryWithLimit(
                history =
                    HistoryProductEntity(
                        productId = productDetail.id,
                        name = productDetail.name,
                        imageUrl = productDetail.imageUrl,
                        category = productDetail.category,
                    ),
                limit = limit,
            )
        }

    override suspend fun fetchRecentHistory(): Result<HistoryProduct?> =
        runCatching {
            dao.getRecentHistoryProduct()?.toDomain()
        }
}
