package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    suspend fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit>

    suspend fun findProductHistory(productId: Long): Result<Product>

    suspend fun getRecommendedProducts(size: Int): Result<List<Product>>

    suspend fun getProductHistory(size: Int): Result<List<Product>>

    suspend fun deleteProductHistory(productId: Long): Result<Unit>

    suspend fun deleteAllProductHistory(): Result<Unit>

    companion object {
        private var instance: ProductHistoryRepository? = null

        fun setInstance(productHistoryRepository: ProductHistoryRepository) {
            instance = productHistoryRepository
        }

        fun getInstance(): ProductHistoryRepository = requireNotNull(instance)
    }
}
