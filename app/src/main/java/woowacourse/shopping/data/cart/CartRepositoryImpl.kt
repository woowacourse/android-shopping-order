package woowacourse.shopping.data.cart

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.repository.CartRepository

class CartRepositoryImpl constructor(
    private val cartDataSource: CartDataSource,
) : CartRepository {
    override fun addCartItem(productId: Int, onSuccess: (Int?) -> Unit) {
        cartDataSource.addCartItem(
            productId,
            onSuccess = {
                onSuccess(it)
            },
            onFailure = {
            }
        )
    }

    override fun deleteCartItem(cartId: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        cartDataSource.deleteCartItem(
            cartId,
            onSuccess = {
                onSuccess()
            },
            onFailure = {
                onFailure()
            }
        )
    }

    override fun updateCartItemQuantity(
        cartId: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        cartDataSource.updateCartItemQuantity(cartId, count, onSuccess = {
            onSuccess()
        }, onFailure = {
            onFailure()
        })
    }

    override fun getAllCartItems(
        onSuccess: (List<CartProductInfo>) -> Unit,
        onFailure: () -> Unit
    ) {
        cartDataSource.getAllCartProductsInfo(onSuccess = {
            onSuccess(it)
        }, onFailure = {
            onFailure()
        })
    }

    override fun getCartItemByProductId(
        productId: Int,
        onSuccess: (CartProductInfo?) -> Unit
    ) {
        getAllCartItems(onSuccess = {
            val foundItem =
                CartProductInfoList(it).items.find { cartItem -> cartItem.product.id == productId }
            onSuccess(foundItem)
        }, onFailure = {
            onSuccess(null)
        })
    }
}
