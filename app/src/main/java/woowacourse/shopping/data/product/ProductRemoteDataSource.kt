package woowacourse.shopping.data.product

interface ProductRemoteDataSource {
    val products: List<ProductDataModel>
    fun findProductById(id: Int): ProductDataModel
}
