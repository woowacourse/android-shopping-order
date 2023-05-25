package woowacourse.shopping.data.service

import woowacourse.shopping.data.model.Product

interface ProductService {
    fun getAllProduct(): List<Product>
    fun findProductById(id: Int): Product?
    fun addProduct(product: Product)
    fun adjustProduct(product: Product)
    fun deleteProduct(product: Product)
}
