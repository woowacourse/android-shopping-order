package woowacourse.shopping.data.product

interface ProductDataSource2 {
    fun getProductEntity(id: Long): ProductEntity?
    fun getProductEntities(unit: Int, lastId: Long, callback: (List<ProductEntity>?) -> Unit)
    fun addProductEntity(
        name: String,
        price: Int,
        itemImage: String,
    ): Long

    fun isLastProductEntity(id: Long): Boolean
}
