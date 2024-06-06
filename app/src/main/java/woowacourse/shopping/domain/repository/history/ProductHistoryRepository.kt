package woowacourse.shopping.domain.repository.history

import woowacourse.shopping.domain.model.Product

interface ProductHistoryRepository {
    fun saveProductHistory(productId: Long)

    fun loadProductsHistory(): List<Product>

    fun loadProductHistory(productId: Long): Product

    fun loadLatestProduct(): Product

    fun deleteProductsHistory()
}
