package woowacourse.shopping.cart

import woowacourse.shopping.common.model.CartProductModel
import woowacourse.shopping.common.model.mapper.CartProductMapper.toDomain
import woowacourse.shopping.common.model.mapper.CartProductMapper.toView
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
    private var cart: Cart = Cart(emptyList())

    init {
        updateNavigationVisibility()
        getCartProducts(onSuccess = {
            updateCartPage(it)
            updateTotalPrice()
            updateTotalQuantity()
        })
    }

    override fun removeCartProduct(cartProductModel: CartProductModel) {
        cartRepository.deleteCartProduct(cartProductModel.toDomain())
        updateNavigationVisibility()
        getCartProducts(onSuccess = { updateCartPage(it) })
        view.setResultForChange()
    }

    override fun goToPreviousPage() {
        currentPage--
        getCartProducts(onSuccess = { updateCartPage(it) })

        if (currentPage == 0) updateNavigationVisibility()
    }

    override fun goToNextPage() {
        currentPage++
        getCartProducts(onSuccess = { updateCartPage(it) })
    }

    override fun changeCartProductChecked(cartProductModel: CartProductModel) {
        val isChecked = !cartProductModel.isChecked
        val cartProduct = cartProductModel.toDomain().changeChecked(isChecked)
        applyCartProductCheckedChange(cartProduct)
    }

    override fun updateAllChecked() {
        val isAllChecked = cartRepository.isAllCheckedInPage(currentPage, sizePerPage)
        view.updateAllChecked(isAllChecked)
    }

    override fun decreaseCartProductAmount(cartProductModel: CartProductModel) {
        if (cartProductModel.quantity > 1) {
            val cartProduct = cartProductModel.toDomain().decreaseAmount()
            updateCartProduct(cartProduct)

            updateTotalPrice()
            updateTotalQuantity()
        }
    }

    override fun increaseCartProductAmount(cartProductModel: CartProductModel) {
        val cartProduct = cartProductModel.toDomain().increaseAmount()
        updateCartProduct(cartProduct)

        updateTotalPrice()
        updateTotalQuantity()
    }

    override fun updateCartProductCheckedInPage(isChecked: Boolean) {
        getCartProducts { updateChecked(it, isChecked) }
    }

    private fun getCartProducts(onSuccess: (List<CartProduct>) -> Unit) {
        cartRepository.getAll(
            onSuccess = { onSuccess(it) },
            onFailure = { view.notifyLoadFailed() }
        )
    }

    private fun updateChecked(cartProducts: List<CartProduct>, isChecked: Boolean) {
        val startIndex = currentPage * sizePerPage
        cartProducts.subList(startIndex, startIndex + sizePerPage).forEach {
            val cartProduct = cart.findCartProduct(it)!!
            cart = if (cartProduct.isChecked != isChecked) {
                val newCartProduct = cartProduct.changeChecked(isChecked)
                applyCartProductCheckedChange(newCartProduct)
                cart.replaceCartProduct(newCartProduct)
            } else {
                cart.replaceCartProduct(cartProduct)
            }
        }
    }

    private fun updateCartPage(cartProducts: List<CartProduct>) {
        val fromIndex = currentPage * sizePerPage
        val toIndex = min(fromIndex + sizePerPage, cartProducts.size)
        val cartProductModels = cartProducts.subList(fromIndex, toIndex).map {
            val prevCartProduct = cart.findCartProduct(it)
            if (prevCartProduct == null) {
                cart = cart.add(it)
                it.toView()
            } else {
                val newCartProduct = it.changeChecked(prevCartProduct.isChecked)
                cart = cart.replaceCartProduct(newCartProduct)
                newCartProduct.toView()
            }
        }

        view.updateCart(
            cartProducts = cartProductModels,
            currentPage = currentPage + 1,
            isLastPage = isLastPageCart(cartProducts.size)
        )
        updateAllChecked()
    }

    private fun updateTotalPrice() {
        view.updateCartTotalPrice(cart.totalPrice)
    }

    private fun updateTotalQuantity() {
        view.updateCartTotalAmount(cart.totalQuantity)
    }

    private fun isLastPageCart(cartSize: Int): Boolean {
        return (currentPage + 1) * sizePerPage >= cartSize
    }

    private fun updateNavigationVisibility() {
        cartRepository.getAllCount(
            onSuccess = {
                val visibility = it > sizePerPage || currentPage != 0
                view.updateNavigationVisibility(visibility)
            },
            onFailure = {}
        )
    }

    private fun applyCartProductCheckedChange(cartProduct: CartProduct) {
        view.updateCartProduct(cartProduct.toView())

        updateTotalPrice()
        updateTotalQuantity()
    }

    private fun updateCartProduct(cartProduct: CartProduct) {
        cartRepository.updateCartProductQuantity(
            cartProduct = cartProduct,
            onSuccess = {
                cart = cart.replaceCartProduct(cartProduct)
                view.updateCartProduct(cartProduct.toView())
                view.setResultForChange()
            },
            onFailure = {}
        )
    }
}
