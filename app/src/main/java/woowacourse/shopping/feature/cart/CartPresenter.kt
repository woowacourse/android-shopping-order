package woowacourse.shopping.feature.cart

import com.example.domain.Cart
import com.example.domain.CartProduct
import com.example.domain.repository.CartRepository
import woowacourse.shopping.model.CartProductState
import woowacourse.shopping.model.CartProductState.Companion.MAX_COUNT_VALUE
import woowacourse.shopping.model.CartProductState.Companion.MIN_COUNT_VALUE
import woowacourse.shopping.model.mapper.toUi

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    private val cart: Cart = Cart()
) : CartContract.Presenter {

    private val maxProductsPerPage: Int = 5
    private val minPageNumber: Int = 1
    // todo 추가 필요
//    private val maxPageNumber: Int
//        get() = getMaxPageNumber(cartRepository.getAll().size)

    private var pageNumber: Int = 1

    override fun initContents() {
    }

    override fun loadCart() {
        val startIndex = pageNumber * maxProductsPerPage - maxProductsPerPage
        val endIndex = pageNumber * maxProductsPerPage - 1

        cartRepository.getAll(onFailure = {}, onSuccess = {
            val items: List<CartProductState> = it.map(CartProduct::toUi)
            cart.addAll(it)
            pickAll()
            view.setCartPageNumber(pageNumber)
            view.setCartProducts(items)
            view.showCartProducts()
        })

//        val items: List<CartProductState> =
//            cartProducts.filterIndexed { index, _ -> index in startIndex..endIndex }
//
//        view.setCartPageNumber(pageNumber)
        view.hidePageSelectorView()
//        if (minPageNumber < maxPageNumber) view.showPageSelectorView()
    }

    override fun updatePickedCartProductCount() {
        val count = cart.getPickedCount()
        view.setCartProductCount(count)
    }

    override fun plusPageNumber() {
//        pageNumber = (++pageNumber).coerceAtMost(maxPageNumber)
//
//        view.setCartPageNumberMinusEnable(true)
//        if (pageNumber > maxPageNumber) return
//        if (pageNumber < maxPageNumber) view.setCartPageNumberPlusEnable(true)
//        if (pageNumber == maxPageNumber) view.setCartPageNumberPlusEnable(false)
//        loadCart()
    }

    override fun minusPageNumber() {
//        pageNumber = (--pageNumber).coerceAtLeast(minPageNumber)
//
//        view.setCartPageNumberPlusEnable(true)
//        if (pageNumber < minPageNumber) return
//        if (pageNumber > minPageNumber) view.setCartPageNumberMinusEnable(true)
//        if (pageNumber == minPageNumber) view.setCartPageNumberMinusEnable(false)
//        loadCart()
    }

    override fun plusQuantity(cartProductState: CartProductState) {
        cartProductState.quantity = (++cartProductState.quantity).coerceAtMost(MAX_COUNT_VALUE)
        cartRepository.updateCartProductQuantity(
            id = cartProductState.id, quantity = cartProductState.quantity,
            onFailure = {}, onSuccess = {
            cart.updateProductQuantityByIndex(cartProductState.id, cartProductState.quantity)
            view.updateItem(cartProductState)
            updatePaymentAmount()
        }
        )
    }

    override fun minusQuantity(cartProductState: CartProductState) {
        cartProductState.quantity = (--cartProductState.quantity).coerceAtLeast(MIN_COUNT_VALUE)
        cartRepository.updateCartProductQuantity(
            id = cartProductState.id, quantity = cartProductState.quantity,
            onFailure = {}, onSuccess = {
            cart.updateProductQuantityByIndex(cartProductState.id, cartProductState.quantity)
            view.updateItem(cartProductState)
            updatePaymentAmount()
        }
        )
    }

    override fun updatePickedByCartId(cartId: Long, checked: Boolean) {
        cart.updatePickedByIndex(cartId, checked)
        updatePaymentAmount()
    }

    override fun updatePaymentAmount() {
        val sum = cart.getPickedProductsTotalPrice()
        view.setTotalCost(sum)
    }

    override fun deleteCartProduct(cartProductState: CartProductState) {
        cartRepository.deleteCartProduct(
            id = cartProductState.id,
            onFailure = {},
            onSuccess = {},
        )
        loadCart()
    }

    override fun changeAllPicked() {
        if (cart.isAllPicked()) {
            cart.setAllPicked(false)
            return
        }
        cart.setAllPicked(true)
    }

    override fun pickAll() {
        when (cart.isAllPicked()) {
            true -> {
                cart.setAllPicked(false)
                view.setAllPickChecked(false)
            }
            false -> {
                cart.setAllPicked(true)
                view.setAllPickChecked(true)
            }
        }
        updatePaymentAmount()
        updatePickedCartProductCount()
        view.setCartProducts(cart.products.map(CartProduct::toUi))
    }

    private fun getMaxPageNumber(cartsSize: Int): Int {
        if (cartsSize == 0) return 1
        return (cartsSize - 1) / maxProductsPerPage + 1
    }
}
