package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.CartProduct

class CartLocalDataSourceImpl : CartLocalDataSource {
    private val cartProductMap = mutableMapOf<Long, CartProduct>()

    override fun addCartProduct(cartProduct: CartProduct): Result<Unit> =
        runCatchingDebugLog {
            cartProductMap[cartProduct.product.id] = cartProduct
        }

    override fun addAllCartProducts(cartProducts: List<CartProduct>): Result<Unit> =
        runCatchingDebugLog {
            cartProductMap.putAll(cartProducts.associateBy { it.product.id })
        }

    override fun removeCartProductByCartId(cartId: Long): Result<Unit> =
        runCatchingDebugLog {
            cartProductMap.values.removeIf { it.cartId == cartId }
        }

    override fun removeCartProductsByCartIds(cartIds: List<Long>): Result<Unit> =
        runCatchingDebugLog {
            cartProductMap.values.removeIf { cartIds.contains(it.cartId) }
        }

    override fun updateQuantity(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatchingDebugLog {
            if (quantity <= DEFAULT_QUANTITY) {
                cartProductMap.remove(productId)
                return@runCatchingDebugLog
            }

            val foundCartProduct = cartProductMap[productId] ?: return@runCatchingDebugLog
            cartProductMap[productId] = foundCartProduct.copy(quantity = quantity)
        }

    override fun getQuantity(productId: Long): Result<Int> =
        runCatchingDebugLog {
            cartProductMap[productId]?.quantity ?: DEFAULT_QUANTITY
        }

    override fun getCartProduct(productId: Long): Result<CartProduct?> =
        runCatchingDebugLog {
            cartProductMap[productId]
        }

    override fun getCartProducts(): Result<List<CartProduct>> =
        runCatchingDebugLog {
            cartProductMap.values.toList()
        }

    companion object {
        private const val DEFAULT_QUANTITY = 0
    }
}
