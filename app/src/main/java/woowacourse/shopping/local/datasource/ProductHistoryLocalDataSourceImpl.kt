package woowacourse.shopping.local.datasource

import woowacourse.shopping.data.datasource.local.ProductHistoryLocalDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.local.dao.ProductHistoryDao
import woowacourse.shopping.local.mapper.toDomain
import woowacourse.shopping.local.model.ProductHistoryEntity
import java.time.LocalDateTime

class ProductHistoryLocalDataSourceImpl(private val dao: ProductHistoryDao) :
    ProductHistoryLocalDataSource {
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

    override suspend fun getProductHistoryById(productId: Long): Result<Product> =
        runCatching {
            dao.findProductHistory(productId = productId).toDomain()
        }

    override suspend fun getProductHistoriesByCategory(category: String): Result<List<Product>> =
        runCatching {
            dao.getProductHistoriesByCategory(category = category).map { it.toDomain() }
        }

    override suspend fun getProductHistoriesBySize(size: Int): Result<List<Product>> =
        runCatching {
            dao.getProductHistoryPaged(size = size).map { it.toDomain() }
        }

    override suspend fun deleteProductHistoryById(productId: Long): Result<Unit> =
        runCatching {
            dao.deleteProductHistory(productId = -productId)
        }

    override suspend fun deleteAllProductHistories(): Result<Unit> =
        runCatching {
            dao.deleteAllProductHistory()
        }
}
