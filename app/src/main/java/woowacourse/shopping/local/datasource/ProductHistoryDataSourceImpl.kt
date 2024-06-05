package woowacourse.shopping.local.datasource

import woowacourse.shopping.data.datasource.local.ProductHistoryDataSource
import woowacourse.shopping.data.model.local.ProductHistoryDto
import woowacourse.shopping.local.dao.ProductHistoryDao
import woowacourse.shopping.local.mapper.toData
import woowacourse.shopping.local.model.ProductHistoryEntity
import java.time.LocalDateTime

class ProductHistoryDataSourceImpl(private val dao: ProductHistoryDao) : ProductHistoryDataSource {
    override suspend fun insertProductHistory(
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

    override suspend fun findProductHistory(productId: Long): Result<ProductHistoryDto> =
        runCatching {
            dao.findProductHistory(productId = productId).toData()
        }

    override suspend fun getProductHistoriesByCategory(category: String): Result<List<ProductHistoryDto>> =
        runCatching {
            dao.getProductHistoriesByCategory(category = category).map { it.toData() }
        }

    override suspend fun getProductHistory(size: Int): Result<List<ProductHistoryDto>> =
        runCatching {
            dao.getProductHistoryPaged(size = size).map { it.toData() }
        }

    override suspend fun deleteProductHistory(productId: Long): Result<Unit> =
        runCatching {
            dao.deleteProductHistory(productId = -productId)
        }

    override suspend fun deleteAllProductHistory(): Result<Unit> =
        runCatching {
            dao.deleteAllProductHistory()
        }
}
