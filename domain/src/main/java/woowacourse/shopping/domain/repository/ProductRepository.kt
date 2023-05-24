package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.page.Page

interface ProductRepository {
    fun getProductByPage(page: Page): List<Product>
    fun findProductById(id: Int): Product?
    fun getAllProducts(): List<Product>
    fun insertProduct(product: Product)
    fun updateProduct(product: Product)
    fun deleteProduct(product: Product)
}
