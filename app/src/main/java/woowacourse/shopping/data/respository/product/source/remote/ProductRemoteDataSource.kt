package woowacourse.shopping.data.respository.product.source.remote

import woowacouse.shopping.model.product.Product

interface ProductRemoteDataSource {
    fun requestDatas(
        onFailure: (message: String) -> Unit,
        onSuccess: (products: List<Product>) -> Unit,
    )

    fun requestData(
        productId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (products: Product) -> Unit,
    )
}
