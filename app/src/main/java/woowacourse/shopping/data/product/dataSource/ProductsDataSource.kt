package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.PageableProducts
import woowacourse.shopping.data.product.entity.ProductEntity

interface ProductsDataSource {
    fun load(
        page: Int,
        size: Int,
    ): PageableProducts

    fun getProductById(id: Long): ProductEntity?
}
