package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.domain.model.Product

class DefaultProductHistoryRepository(
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val productDataSource: ProductDataSource,
) : ProductHistoryRepository {
    override fun saveProductHistory(productId: Long) {
        productHistoryDataSource.saveProductHistory(productId)
    }

    override fun loadAllProductHistory(): List<Product> {
        val productIds = productHistoryDataSource.loadAllProductHistory()
        return productIds.map {
            productDataSource.findById(it.id).toDomain(quantity = 0)
        }
    }

    override fun loadLatestProduct(): Product {
        val productId: Long = productHistoryDataSource.loadLatestProduct().id
        val productData = productDataSource.findById(productId)
        return Product(
            productData.id,
            productData.imgUrl,
            productData.name,
            productData.price,
            quantity = 0,
            category = productData.category,
        )
    }

    override suspend fun saveProductHistory2(productId: Long): Result<Unit> = productHistoryDataSource.saveProductHistory2(productId)

    override suspend fun loadLatestProduct2(): Result<Product> {
        val latestProductId =
            productHistoryDataSource.loadLatestProduct2().getOrNull()?.id
                ?: return Result.failure(Exception("No latest product found"))
        val productData =
            productDataSource.findById2(latestProductId).getOrNull() ?: return Result.failure(Exception("No product found"))
        return Result.success(productData.toDomain(quantity = 0))
    }

    override suspend fun loadRecentProducts(size: Int): Result<List<Product>> {
        val productIds =
            productHistoryDataSource.loadRecentProducts(size).getOrNull()
                ?: return Result.failure(Exception("No recent products found"))
        val products =
            productIds.map {
                productDataSource.findById2(it.id).getOrNull()?.toDomain(quantity = 0)
                    ?: return Result.failure(Exception("No product found"))
            }
        return Result.success(products)
    }

    companion object {
        private const val TAG = "DefaultProductHistoryRe"
    }
}
