package woowacourse.shopping.data.product

import woowacourse.shopping.model.Product

interface ProductRepository {
    fun getProducts(
        page: Int,
        size: Int,
        success: (List<Product>) -> Unit,
    )

    fun find(id: Long): Product
}
