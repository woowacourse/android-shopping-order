package woowacourse.shopping.data.repository.local

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.service.cart.CartRemoteService

class CartRepositoryImpl(
    private val cartRemoteService: CartRemoteService,
) : CartRepository {
    override fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartRemoteService.requestCarts(
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun addCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartRemoteService.requestAddCartProduct(
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
        cartRemoteService.requestChangeCartProductCount(cartId, count, onSuccess, onFailure)
    }

    override fun deleteCartProduct(
        cartId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartRemoteService.requestDeleteCartProduct(cartId, onSuccess, onFailure)
    }

    override fun getSize(onSuccess: (cartCount: Int) -> Unit, onFailure: () -> Unit) {
        onSuccess(3)
    }
}
