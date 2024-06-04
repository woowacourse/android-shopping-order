package woowacourse.shopping.data.product

import woowacourse.shopping.remote.product.ProductDto

interface ProductDataSource {
    fun findByPaged(page: Int): List<ProductDto>

    fun findById(id: Long): ProductDto

    fun isFinalPage(page: Int): Boolean

    fun findByCategory(productId: Long): List<ProductDto>

    fun shutDown(): Boolean
}
