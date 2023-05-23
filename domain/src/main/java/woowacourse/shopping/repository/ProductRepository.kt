package woowacourse.shopping.repository

import woowacourse.shopping.Product

interface ProductRepository {
    fun findProductById(id: Int): Product?
    fun getProductsWithRange(start: Int, size: Int): List<Product>
}
