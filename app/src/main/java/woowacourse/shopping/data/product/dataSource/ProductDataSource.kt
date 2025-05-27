package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.local.entity.ProductEntity

interface ProductDataSource {
    fun load(
        lastProductId: Long?,
        size: Int,
    ): List<ProductEntity>
}
