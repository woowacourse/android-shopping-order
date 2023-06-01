package woowacourse.shopping.data.respository.product

import woowacourse.shopping.data.model.CartRemoteEntity

interface ProductRepository {
    fun loadDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    )

    fun loadDataById(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: CartRemoteEntity) -> Unit,
    )
}
