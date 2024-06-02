package woowacourse.shopping.local.datasource

import woowacourse.shopping.data.datasource.local.ProductHistoryLocalDataSource
import woowacourse.shopping.data.model.local.ProductHistoryDto
import woowacourse.shopping.local.dao.ProductHistoryDao
import woowacourse.shopping.local.mapper.toData
import woowacourse.shopping.local.model.ProductHistoryEntity
import java.time.LocalDateTime

class ProductHistoryLocalDataSourceImpl(private val dao: ProductHistoryDao) : ProductHistoryLocalDataSource {
    override fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit> =
        runCatching {
            val productHistoryEntity =
                ProductHistoryEntity(
                    productId = productId,
                    name = name,
                    price = price,
                    imageUrl = imageUrl,
                    category = category,
                    createAt = LocalDateTime.now(),
                )

            dao.insertProductHistory(productHistoryEntity = productHistoryEntity)
        }

    override fun getProductHistoryById(productId: Long): Result<ProductHistoryDto> =
        runCatching {
            dao.findProductHistory(productId = productId).toData()
        }

    override fun getProductHistoriesByCategory(category: String): Result<List<ProductHistoryDto>> =
        runCatching {
            dao.getProductHistoriesByCategory(category = category).map { it.toData() }
        }

    override fun getProductHistoriesBySize(size: Int): Result<List<ProductHistoryDto>> =
        runCatching {
            dao.getProductHistoryPaged(size = size).map { it.toData() }
        }

    override fun deleteProductHistoryById(productId: Long): Result<Unit> =
        runCatching {
            dao.deleteProductHistory(productId = -productId)
        }

    override fun deleteAllProductHistories(): Result<Unit> =
        runCatching {
            dao.deleteAllProductHistory()
        }
}
