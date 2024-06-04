package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit>

    fun findProductHistory(productId: Long): Result<Product>

    fun getRecommendedProducts(size: Int): Result<List<Product>>

    fun getProductHistory(size: Int): Result<List<Product>>

    fun deleteProductHistory(productId: Long): Result<Unit>

    fun deleteAllProductHistory(): Result<Unit>

    companion object {
        private var instance: ProductHistoryRepository? = null

        fun setInstance(productHistoryRepository: ProductHistoryRepository) {
            instance = productHistoryRepository
        }

        fun getInstance(): ProductHistoryRepository = requireNotNull(instance)
    }
}
