package woowacourse.shopping.cart

import woowacourse.shopping.common.model.CartProductModel
import woowacourse.shopping.common.model.mapper.CartProductMapper.toDomain
import woowacourse.shopping.common.model.mapper.CartProductMapper.toView
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartRepository

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    private var currentPage: Int = 0,
    private val sizePerPage: Int
) : CartContract.Presenter {
    private var cart: Cart = Cart(emptyList())
    private var cartTotalPrice: Int = 0
    private var cartTotalAmount: Int = 0

    init {
        view.updateNavigationVisibility(determineNavigationVisibility())
        getCartProducts(onSuccess = { updateCartPage(it) })
        setupTotalPrice()
        setupTotalAmount()
    }

    override fun removeCartProduct(cartProductModel: CartProductModel) {
        cartRepository.deleteCartProduct(cartProductModel.toDomain())
        view.updateNavigationVisibility(determineNavigationVisibility())
        getCartProducts(onSuccess = { updateCartPage(it) })
        view.setResultForChange()
    }

    override fun goToPreviousPage() {
        currentPage--
        getCartProducts(onSuccess = { updateCartPage(it) })

        if (currentPage == 0) view.updateNavigationVisibility(determineNavigationVisibility())
    }

    override fun goToNextPage() {
        currentPage++
        getCartProducts(onSuccess = { updateCartPage(it) })
    }

    override fun changeCartProductChecked(cartProductModel: CartProductModel) {
        val isChecked = !cartProductModel.isChecked
        val newCartProduct = cartProductModel.toDomain().changeChecked(isChecked)
        applyCartProductCheckedChange(cartProductModel.toDomain(), newCartProduct)
    }

    override fun updateAllChecked() {
        val isAllChecked = cartRepository.isAllCheckedInPage(currentPage, sizePerPage)
        view.updateAllChecked(isAllChecked)
    }

    override fun decreaseCartProductAmount(cartProductModel: CartProductModel) {
        if (cartProductModel.quantity > 1) {
            val prevCartProduct = cartProductModel.toDomain()
            val newCartProduct = prevCartProduct.decreaseAmount()
            updateCartProduct(prevCartProduct, newCartProduct)

            if (cartProductModel.isChecked) {
                subtractProductPriceToCartTotalPrice(cartProductModel)
                decreaseTotalAmount()
            }
        }
    }

    override fun increaseCartProductAmount(cartProductModel: CartProductModel) {
        val prevCartProduct = cartProductModel.toDomain()
        val newCartProduct = prevCartProduct.increaseAmount()
        updateCartProduct(prevCartProduct, newCartProduct)

        if (cartProductModel.isChecked) {
            addProductPriceToCartTotalPrice(cartProductModel)
            increaseTotalAmount()
        }
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
                applyCartProductCheckedChange(cartProduct, newCartProduct)
                cart.replaceCartProduct(newCartProduct)
            } else {
                cart.replaceCartProduct(cartProduct)
            }
        }
    }

    private fun updateCartPage(cartProducts: List<CartProduct>) {
        val startIndex = currentPage * sizePerPage
        val cartProductModels = cartProducts.subList(startIndex, startIndex + sizePerPage).map {
            val prevCartProduct = cart.findCartProduct(it)
            if (prevCartProduct == null) {
                cart.add(it)
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

    private fun setupTotalPrice() {
        cartTotalPrice = cartRepository.getTotalPrice()
        view.updateCartTotalPrice(cartTotalPrice)
    }

    private fun setupTotalAmount() {
        cartTotalAmount = cartRepository.getTotalAmount()
        view.updateCartTotalAmount(cartTotalAmount)
    }

    private fun isLastPageCart(cartSize: Int): Boolean {
        return cart.cartProducts.size >= cartSize
    }

    private fun determineNavigationVisibility(): Boolean {
        val cartCount = cartRepository.getAllCount()
        return cartCount > sizePerPage || currentPage != 0
    }

    private fun applyCartProductCheckedChange(prev: CartProduct, new: CartProduct) {
        view.updateCartProduct(prev.toView(), new.toView())

        applyProductTotalPriceToCartTotalPrice(new)
        applyProductAmountToCartTotalAmount(new)
    }

    private fun applyProductTotalPriceToCartTotalPrice(cartProduct: CartProduct) {
        val productTotalPrice = cartProduct.product.price * cartProduct.quantity
        if (cartProduct.isChecked) {
            cartTotalPrice += productTotalPrice
            view.updateCartTotalPrice(cartTotalPrice)
        } else {
            cartTotalPrice -= productTotalPrice
            view.updateCartTotalPrice(cartTotalPrice)
        }
    }

    private fun applyProductAmountToCartTotalAmount(cartProduct: CartProduct) {
        if (cartProduct.isChecked) {
            cartTotalAmount += cartProduct.quantity
            view.updateCartTotalAmount(cartTotalAmount)
        } else {
            cartTotalAmount -= cartProduct.quantity
            view.updateCartTotalAmount(cartTotalAmount)
        }
    }

    private fun updateCartProduct(prev: CartProduct, new: CartProduct) {
        cartRepository.modifyCartProduct(new)
        cartRepository.replaceCartProduct(prev, new)
        view.updateCartProduct(prev.toView(), new.toView())
    }

    private fun subtractProductPriceToCartTotalPrice(cartProductModel: CartProductModel) {
        cartTotalPrice -= cartProductModel.product.price
        view.updateCartTotalPrice(cartTotalPrice)
    }

    private fun decreaseTotalAmount() {
        cartTotalAmount -= 1
        view.updateCartTotalAmount(cartTotalAmount)
    }

    private fun addProductPriceToCartTotalPrice(cartProductModel: CartProductModel) {
        cartTotalPrice += cartProductModel.product.price
        view.updateCartTotalPrice(cartTotalPrice)
    }

    private fun increaseTotalAmount() {
        cartTotalAmount += 1
        view.updateCartTotalAmount(cartTotalAmount)
    }
}
