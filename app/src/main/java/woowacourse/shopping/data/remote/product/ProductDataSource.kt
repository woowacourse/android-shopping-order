package woowacourse.shopping.data.remote.product

interface ProductDataSource {
    fun getProductById(
        id: Int,
        onSuccess: (ProductDataModel) -> Unit,
        onFailure: () -> Unit
    )

    fun getAllProducts(onSuccess: (List<ProductDataModel>) -> Unit, onFailure: () -> Unit)
}
