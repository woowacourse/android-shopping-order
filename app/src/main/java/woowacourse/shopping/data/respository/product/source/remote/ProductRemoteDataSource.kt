package woowacourse.shopping.data.respository.product.source.remote

import woowacourse.shopping.data.model.CartRemoteEntity

interface ProductRemoteDataSource {
    fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    )
    fun requestData(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: CartRemoteEntity) -> Unit,
    )
}
