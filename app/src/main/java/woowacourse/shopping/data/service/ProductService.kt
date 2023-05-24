package woowacourse.shopping.data.service

import woowacourse.shopping.data.model.Page
import woowacourse.shopping.data.model.Product

interface ProductService {
    fun getProductByPage(page: Page): List<Product>
    fun findProductById(id: Int): Product?
    fun getAllProduct(): List<Product>
    fun addProduct(product: Product)
    fun adjustProduct(product: Product)
    fun deleteProduct(product: Product)
}
