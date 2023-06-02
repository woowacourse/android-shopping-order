package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.model.CartProductModel
import woowacourse.shopping.ui.model.mapper.CartProductMapper.toDomain
import woowacourse.shopping.ui.model.mapper.CartProductMapper.toView
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.math.min

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    private var currentPage: Int = 0,
    private val sizePerPage: Int
) : CartContract.Presenter {
    private lateinit var cart: Cart

    init {
        getCartProducts(onSuccess = {
            cart = Cart(it)
            updateCartPage()
            updateTotalPrice()
            updateTotalQuantity()
            updateNavigationVisibility()
        })
    }

    private fun getCartProducts(onSuccess: (List<CartProduct>) -> Unit) {
        cartRepository.getAll(
            onSuccess = { onSuccess(it) },
            onFailure = { view.notifyLoadFailed() }
        )
    }

    private fun updateCartPage() {
        val cartProducts = getCartProductsInPage()

        view.updateCart(
            cartProducts = cartProducts.map { it.toView() },
            currentPage = currentPage + 1,
            isLastPage = isLastPage()
        )
        view.updateAllChecked(cartProducts.all { it.isChecked })
    }

    private fun getCartProductsInPage(): List<CartProduct> {
        val fromIndex = currentPage * sizePerPage
        val toIndex = min(fromIndex + sizePerPage, cart.cartProducts.size)
        return cart.cartProducts.subList(fromIndex, toIndex)
    }

    private fun isLastPage(): Boolean {
        return (currentPage + 1) * sizePerPage >= cart.cartProducts.size
    }

    private fun updateTotalPrice() {
        view.updateCartTotalPrice(cart.selectedCart.totalPrice)
    }

    private fun updateTotalQuantity() {
        view.updateCartTotalQuantity(cart.selectedCart.totalQuantity)
    }

    private fun updateNavigationVisibility() {
        val visibility = currentPage != 0 || cart.cartProducts.size > sizePerPage
        view.updateNavigationVisibility(visibility)
    }

    override fun removeCartProduct(cartProductModel: CartProductModel) {
        val cartProduct = cartProductModel.toDomain()
        cartRepository.deleteCartProduct(
            cartProduct = cartProduct,
            onSuccess = {
                cart = cart.removeCartProduct(cartProduct)
                updateCartPage()
                updateNavigationVisibility()
                view.setResultForChange()
            },
            onFailure = {}
        )
    }

    override fun goToPreviousPage() {
        currentPage--
        updateCartPage()

        if (currentPage == 0) updateNavigationVisibility()
    }

    override fun goToNextPage() {
        currentPage++
        updateCartPage()
    }

    override fun reverseCartProductChecked(cartProductModel: CartProductModel) {
        val isChecked = !cartProductModel.isChecked
        val cartProduct = cartProductModel.toDomain().changeChecked(isChecked)
        updateCartProduct(cartProduct)

        updateTotalPrice()
        updateTotalQuantity()
    }

    private fun updateCartProduct(cartProduct: CartProduct) {
        cart = cart.replaceCartProduct(cartProduct)
        view.updateCartProduct(cartProduct.toView())
    }

    override fun updateAllChecked() {
        val cartProducts = getCartProductsInPage()
        val isAllChecked = cartProducts.all { it.isChecked }
        view.updateAllChecked(isAllChecked)
    }

    override fun decreaseCartProductQuantity(cartProductModel: CartProductModel) {
        if (cartProductModel.quantity > 1) {
            val cartProduct = cartProductModel.toDomain().decreaseAmount()
            updateCartProductQuantity(cartProduct)
        }
    }

    override fun increaseCartProductQuantity(cartProductModel: CartProductModel) {
        val cartProduct = cartProductModel.toDomain().increaseAmount()
        updateCartProductQuantity(cartProduct)
    }

    private fun updateCartProductQuantity(cartProduct: CartProduct) {
        cartRepository.updateCartProductQuantity(
            cartProduct = cartProduct,
            onSuccess = {
                updateCartProduct(cartProduct)
                view.setResultForChange()

                updateTotalPrice()
                updateTotalQuantity()
            },
            onFailure = {}
        )
    }

    override fun changeAllChecked(isChecked: Boolean) {
        val cartProducts = getCartProductsInPage()
        cartProducts.forEach {
            if(it.isChecked != isChecked) {
                updateCartProduct(it.changeChecked(isChecked))
            }
        }
        updateTotalPrice()
        updateTotalQuantity()
    }
//
//    private fun updateChecked(cartProducts: List<CartProduct>, isChecked: Boolean) {
//        val startIndex = currentPage * sizePerPage
//        cartProducts.subList(startIndex, startIndex + sizePerPage).forEach {
//            val cartProduct = cart.findCartProduct(it)!!
//            cart = if (cartProduct.isChecked != isChecked) {
//                val newCartProduct = cartProduct.changeChecked(isChecked)
//                updateCartProduct(newCartProduct)
//                cart.replaceCartProduct(newCartProduct)
//            } else {
//                cart.replaceCartProduct(cartProduct)
//            }
//        }
//    }
}
