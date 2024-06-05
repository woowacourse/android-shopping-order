package woowacourse.shopping.domain.repository.product

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun loadAllProducts(page: Int): List<Product>

    fun loadProduct(id: Long): Product

    fun isFinalPage(page: Int): Boolean

    fun shoppingCartProductQuantity(): Int
}
