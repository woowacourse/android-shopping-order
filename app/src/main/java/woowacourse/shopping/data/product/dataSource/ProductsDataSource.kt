package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.entity.ProductEntity

interface ProductsDataSource {
    fun load(page: Int, size: Int): List<ProductEntity>

    fun getById(id: Long): ProductEntity?
}
