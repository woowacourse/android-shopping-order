package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Cart
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

    fun getProductHistoriesByCategory(size: Int): Result<List<Cart>>

    fun getProductHistory(size: Int): Result<List<Product>>

    fun deleteProductHistory(productId: Long): Result<Unit>

    fun deleteAllProductHistory(): Result<Unit>
}
