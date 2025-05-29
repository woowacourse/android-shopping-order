package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dao.HistoryDao
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.model.entity.HistoryProductEntity
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.repository.HistoryRepository
import kotlin.concurrent.thread

class HistoryRepository(
    private val dao: HistoryDao,
) : HistoryRepository {
    override fun fetchAllHistory(callback: (List<HistoryProduct>) -> Unit) {
        thread {
            callback(
                dao.getHistoryProducts().map { it.toDomain() },
            )
        }
    }

    override fun addHistoryWithLimit(
        productDetail: ProductDetail,
        limit: Int,
    ) {
        thread {
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
    }

    override fun fetchRecentHistory(callback: (HistoryProduct?) -> Unit) {
        thread {
            callback(
                dao.getRecentHistoryProduct()?.toDomain(),
            )
        }
    }
}
