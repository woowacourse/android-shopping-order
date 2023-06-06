package woowacourse.shopping.data.cart

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.repository.CartRepository

class CartRepositoryImpl constructor(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override fun addCartItem(productId: Int, onSuccess: (Int?) -> Unit) {
        cartRemoteDataSource.addCartItem(
            productId,
            onSuccess = {
                onSuccess(it)
            },
            onFailure = {
            }
        )
    }

    override fun deleteCartItem(cartId: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        cartRemoteDataSource.deleteCartItem(
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
        cartRemoteDataSource.updateCartItemQuantity(cartId, count, onSuccess = {
            onSuccess()
        }, onFailure = {
            onFailure()
        })
    }

    override fun getAllCartItems(
        onSuccess: (List<CartProductInfo>) -> Unit,
        onFailure: () -> Unit
    ) {
        cartRemoteDataSource.getAllCartProductsInfo(onSuccess = {
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
