package woowacourse.shopping.data.service.product

import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.service.cart.ProductId

interface ProductService {
    fun getAllProduct(): List<Product>
    fun findProductById(id: ProductId): Product?
    fun insertProduct(product: Product)
    fun updateProduct(product: Product)
    fun deleteProduct(product: Product)
}
