package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.model.CartItem

interface ShoppingProductsRepository {
    fun pagedProducts(page: Int): List<Product>

    fun allProductsUntilPage(page: Int): List<Product>

    fun loadProduct(id: Long): Product

    fun isFinalPage(page: Int): Boolean
}
