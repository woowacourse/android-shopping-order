package woowacourse.shopping.data.product.recentlyViewed

import woowacourse.shopping.data.product.ProductEntity

interface ProductDataSource {
    fun getProductEntity(id: Long): ProductEntity?
    fun getProductEntities(unit: Int, lastId: Long): List<ProductEntity>
    fun addProductEntity(
        name: String,
        price: Int,
        itemImage: String,
    ): Long

    fun isLastProductEntity(id: Long): Boolean
}
