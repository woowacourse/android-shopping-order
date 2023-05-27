package woowacourse.shopping.data.repository.local

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.dataSource.service.CartProductRemoteService

class CartRepositoryImpl(
    private val cartProductRemoteService: CartProductRemoteService,
) : CartRepository {
    override fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductRemoteService.requestCarts(
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun addCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductRemoteService.requestAddCartProduct(
            productId,
            onSuccess,
            onFailure,
        )
    }

    override fun changeCartProductCount(
        cartId: Long,
        count: Int,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductRemoteService.requestChangeCartProductCount(cartId, count, onSuccess, onFailure)
    }

    override fun deleteCartProduct(
        cartId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductRemoteService.requestDeleteCartProduct(cartId, onSuccess, onFailure)
    }
}
