package woowacourse.shopping.data.respository.cart.source.remote

import woowacourse.shopping.data.model.CartEntity2

interface CartRemoteDataSource {
    fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartEntity2>) -> Unit,
    )
}
