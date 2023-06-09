package woowacourse.shopping.data.cart

import woowacourse.shopping.data.server.CartRemoteDataSource
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val cartRemoteDataSource: CartRemoteDataSource
) : CartRepository {

    override fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        cartRemoteDataSource.getCartProducts(onSuccess, onFailure)
    }

    override fun addCartProduct(productId: Int, quantity: Int, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        cartRemoteDataSource.addCartProduct(
            productId,
            quantity,
            onSuccess,
            onFailure
        )
    }

    override fun deleteCartProduct(
        cartProduct: CartProduct,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        cartRemoteDataSource.deleteCartProduct(
            cartProductId = cartProduct.id,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    override fun deleteCartProduct(
        shoppingProduct: ShoppingProduct,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        findByProductId(
            productId = shoppingProduct.product.id,
            onSuccess = {
                if(it != null) {
                    deleteCartProduct(it, onSuccess, onFailure)
                }
                else {
                    onFailure(FAILURE_MESSAGE_NOT_EXIST)
                }
            },
            onFailure = onFailure
        )
    }

    override fun updateCartProductQuantity(cartProduct: CartProduct, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        cartRemoteDataSource.updateCartProductQuantity(
            id = cartProduct.id,
            quantity = cartProduct.quantity,
            onSuccess,
            onFailure
        )
    }

    override fun findByProductId(productId: Int, onSuccess: (CartProduct?) -> Unit, onFailure: (String) -> Unit) {
        getAll(
            onSuccess = { products ->
                val cartProduct = products.find { it.product.id == productId }
                onSuccess(cartProduct)
            },
            onFailure
        )
    }

    companion object {
        private const val FAILURE_MESSAGE_NOT_EXIST = "장바구니에 존재하지 않는 상품입니다."
    }
}
