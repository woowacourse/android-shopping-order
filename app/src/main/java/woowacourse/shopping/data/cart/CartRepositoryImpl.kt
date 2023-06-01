package woowacourse.shopping.data.cart

import woowacourse.shopping.data.server.CartRemoteDataSource
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource
) : CartRepository {

    override fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit
    ) {
        cartRemoteDataSource.getCartProducts(
            onSuccess = { onSuccess(it) },
            onFailure = { onFailure() }
        )
    }

    override fun addCartProduct(product: Product, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        cartRemoteDataSource.addCartProduct(
            product.id,
            onSuccess = { onSuccess(it) },
            onFailure = { onFailure() }
        )
    }

    override fun deleteCartProduct(
        cartProduct: CartProduct,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
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
        onFailure: () -> Unit
    ) {
        findByProductId(
            productId = shoppingProduct.product.id,
            onSuccess = {
                if(it != null) {
                    deleteCartProduct(it, onSuccess, onFailure)
                }
                else {
                    onFailure()
                }
            },
            onFailure = onFailure
        )
    }

    override fun updateCartProductQuantity(cartProduct: CartProduct, onSuccess: () -> Unit, onFailure: () -> Unit) {
        cartRemoteDataSource.updateCartProductQuantity(
            id = cartProduct.id,
            quantity = cartProduct.quantity,
            onSuccess = { onSuccess() },
            onFailure = { onFailure() }
        )
    }

    override fun findByProductId(productId: Int, onSuccess: (CartProduct?) -> Unit, onFailure: () -> Unit) {
        getAll(
            onSuccess = { products ->
                val cartProduct = products.find { it.product.id == productId }
                onSuccess(cartProduct)
            },
            onFailure = { onFailure() }
        )
    }
}
