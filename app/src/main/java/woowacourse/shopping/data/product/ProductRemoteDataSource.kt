package woowacourse.shopping.data.product

import woowacourse.shopping.Product

interface ProductRemoteDataSource {
    fun findProductById(id: Int): Product
    fun getAllProducts(): List<Product>
}
