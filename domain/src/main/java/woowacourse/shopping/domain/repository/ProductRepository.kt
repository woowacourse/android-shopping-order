package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun getAllProducts(): List<Product>
    fun findProductById(id: Int): Product?
    fun insertProduct(product: Product)
    fun updateProduct(product: Product)
    fun deleteProduct(product: Product)
}
