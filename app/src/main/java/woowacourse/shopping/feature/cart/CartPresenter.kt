package woowacourse.shopping.feature.cart

import com.example.domain.CartProduct
import com.example.domain.repository.CartRepository
import woowacourse.shopping.model.CartProductState
import woowacourse.shopping.model.CartProductState.Companion.MAX_COUNT_VALUE
import woowacourse.shopping.model.CartProductState.Companion.MIN_COUNT_VALUE
import woowacourse.shopping.model.mapper.toUi

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository
) : CartContract.Presenter {

    private val maxProductsPerPage: Int = 5
    private val minPageNumber: Int = 1
    // todo 추가 필요
//    private val maxPageNumber: Int
//        get() = getMaxPageNumber(cartRepository.getAll().size)

    private var pageNumber: Int = 1

    override fun loadCart() {
        val startIndex = pageNumber * maxProductsPerPage - maxProductsPerPage
        val endIndex = pageNumber * maxProductsPerPage - 1

        cartRepository.getAll(onFailure = {}, onSuccess = {
            val items: List<CartProductState> = it.map(CartProduct::toUi)
            view.setCartPageNumber(pageNumber)
            view.setCartProducts(items)
            view.showCartProducts()
        })

//        val cartProducts: List<CartProductState> = cartRepository.getAll().map(CartProduct::toUi)
//        val items: List<CartProductState> =
//            cartProducts.filterIndexed { index, _ -> index in startIndex..endIndex }
//
//        view.setCartPageNumber(pageNumber)
//        view.setCartProducts(items)
        view.hidePageSelectorView()
//        if (minPageNumber < maxPageNumber) view.showPageSelectorView()
    }

    override fun loadCheckedCartProductCount() {
        cartRepository.getAll(onFailure = {}, onSuccess = { cartProducts ->
            val cartProductCount = cartProducts.filter { it.isPicked }.size
            view.setCartProductCount(cartProductCount)
        })
//        val cartProductCount = cartRepository.getAll().filter { it.checked }.size
//        view.setCartProductCount(cartProductCount)
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
            view.updateItem(cartProductState)
            // todo 계산 로직 필요
        }
        )
    }

    override fun minusQuantity(cartProductState: CartProductState) {
        cartProductState.quantity = (--cartProductState.quantity).coerceAtLeast(MIN_COUNT_VALUE)
        cartRepository.updateCartProductQuantity(
            id = cartProductState.id, quantity = cartProductState.quantity,
            onFailure = {}, onSuccess = {
            view.updateItem(cartProductState)
            // todo 계산 로직 필요
        }
        )
    }

    override fun updateChecked(productId: Int, checked: Boolean) {
//        cartRepository.updateCartProductChecked(productId, checked)
//        view.setTotalCost(PaymentCalculator.totalPaymentAmount(cartRepository.getAll()).toInt())
    }

    override fun deleteCartProduct(cartProductState: CartProductState) {
        cartRepository.deleteCartProduct(
            id = cartProductState.id,
            onFailure = {},
            onSuccess = {},
        )
        loadCart()
    }

    override fun checkAll() {
//        val cartProducts: List<CartProduct> = cartRepository.getAll()
//        val checked: Boolean = cartProducts.find { !it.checked } != null
//
//        cartProducts.forEach {
//            cartRepository.updateCartProductChecked(it.productId, checked)
//        }
//        view.setTotalCost(PaymentCalculator.totalPaymentAmount(cartRepository.getAll()).toInt())
    }

    private fun getMaxPageNumber(cartsSize: Int): Int {
        if (cartsSize == 0) return 1
        return (cartsSize - 1) / maxProductsPerPage + 1
    }
}
