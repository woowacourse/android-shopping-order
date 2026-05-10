package woowacourse.shopping.backend

import woowacourse.shopping.model.ShoppingItem

interface ProductBackendDataSource {
    suspend fun fetchProducts(): List<ShoppingItem>
}
