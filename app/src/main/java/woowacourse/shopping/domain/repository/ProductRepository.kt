package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun findProductsByPage(): List<Product>

    fun findProductById(id: Int): Product?

    fun canLoadMore(): Boolean
}

interface ProductRepository2 {
    fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductResponse>

    fun getProductById(id: Int): Result<woowacourse.shopping.data.model.Product2>
}
