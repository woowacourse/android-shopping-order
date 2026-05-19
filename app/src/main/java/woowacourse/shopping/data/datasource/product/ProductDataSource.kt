package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductPage

interface ProductDataSource {

    suspend fun findAllProduct(
        page: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): ProductPage

    suspend fun findById(id: String): Product
}
