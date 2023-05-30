package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.server.CartRemoteDataSource

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource
) : CartRepository {

    override fun addCartProduct(product: Product, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        cartRemoteDataSource.addCartProduct(
            product.id,
            onSuccess = { onSuccess(it) },
            onFailure = { onFailure() }
        )
    }

    override fun getAllCount(onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        cartRemoteDataSource.getCartProducts(
            onSuccess = {
                onSuccess(it.size)
            },
            onFailure = { onFailure() }
        )
    }

    override fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit
    ) {
        cartRemoteDataSource.getCartProducts(
            onSuccess = { onSuccess(it) },
            onFailure = { onFailure() }
        )
    }

    override fun deleteCartProduct(cartProduct: CartProduct) {
        // cartDao.deleteCartProduct(cartProduct)
        // cart = cart.removeCartProduct(cartProduct)
    }

    override fun updateCartProductQuantity(cartProduct: CartProduct, onSuccess: () -> Unit, onFailure: () -> Unit) {
        cartRemoteDataSource.updateCartProductQuantity(
            id = cartProduct.id,
            quantity = cartProduct.quantity,
            onSuccess = { onSuccess() },
            onFailure = { onFailure() }
        )
    }

    // override fun getTotalPrice(): Int {
    //     return cartDao.getTotalPrice()
    // }

    override fun isAllCheckedInPage(page: Int, sizePerPage: Int): Boolean {
        // val startIndex = page * sizePerPage
        // val cartInPage = cart.getSubCart(startIndex, startIndex + sizePerPage)
        // return cartInPage.cartProducts.all { it.isChecked }
        return true
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
