package woowacourse.shopping.data.datasource.history

import woowacourse.shopping.data.dao.HistoryDao
import woowacourse.shopping.data.model.entity.HistoryProductEntity

class HistoryLocalDataSourceImpl(
    private val dao: HistoryDao,
) : HistoryLocalDataSource {
    override suspend fun getHistoryProducts(): List<HistoryProductEntity> = dao.getHistoryProducts()

    override suspend fun insertHistoryWithLimit(
        productId: Long,
        name: String,
        imageUrl: String,
        category: String,
        limit: Int,
    ) {
        dao.insertHistoryWithLimit(
            history =
                HistoryProductEntity(
                    productId = productId,
                    name = name,
                    imageUrl = imageUrl,
                    category = category,
                ),
            limit = limit,
        )
    }

    override suspend fun getRecentHistoryProduct(): HistoryProductEntity? = dao.getRecentHistoryProduct()
}
