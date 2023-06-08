package woowacourse.shopping.feature.cart

import com.example.domain.Pagination
import com.example.domain.cart.Cart
import com.example.domain.cart.CartProduct
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.model.CartProductState
import woowacourse.shopping.model.mapper.toUi

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
) : CartContract.Presenter {

    private val cart: Cart = Cart()
    private val maxProductsPerPage: Int = 5
    private val minPageNumber: Int = 1

    private var maxPageNumber: Int = Integer.MAX_VALUE

    private var pageNumber: Int = 1

    override fun initContents() {
        cartRepository.requestFetchCartProductsUnit(
            Cart.MAX_SIZE, pageNumber,
            failure = {}, success = { cartProducts: List<CartProduct>, _: Pagination ->
            cart.setAll(cartProducts)
            loadCart()
            view.setCartPageNumber(pageNumber)
            view.showCartProducts()
        }
        )
    }

    override fun loadCart() {
        cartRepository.requestFetchCartProductsUnit(
            maxProductsPerPage, pageNumber,
            failure = {}, success = { cartProducts: List<CartProduct>, pagination: Pagination ->
            val cartProductStates: List<CartProductState> = cartProducts.map(CartProduct::toUi)
            maxPageNumber = pagination.lastPage
            pickAll()
            view.setCartPageNumber(pageNumber)
            view.setCartProducts(cartProductStates)
            view.showCartProducts()
        }
        )

        view.setCartPageNumber(pageNumber)
        if (minPageNumber < maxPageNumber) view.showPageSelectorView()
    }

    override fun updatePickedCartProductCount() {
        val count = cart.getPickedCount()
        view.setCartProductCount(count)
    }

    override fun plusPageNumber() {
        pageNumber = (++pageNumber).coerceAtMost(maxPageNumber)

        view.setCartPageNumberMinusEnable(true)
        if (pageNumber > maxPageNumber) return
        if (pageNumber < maxPageNumber) view.setCartPageNumberPlusEnable(true)
        if (pageNumber == maxPageNumber) view.setCartPageNumberPlusEnable(false)
        loadCart()
    }

    override fun minusPageNumber() {
        pageNumber = (--pageNumber).coerceAtLeast(minPageNumber)

        view.setCartPageNumberPlusEnable(true)
        if (pageNumber < minPageNumber) return
        if (pageNumber > minPageNumber) view.setCartPageNumberMinusEnable(true)
        if (pageNumber == minPageNumber) view.setCartPageNumberMinusEnable(false)
        loadCart()
    }

    override fun plusQuantity(cartProductState: CartProductState) {
        val cartProduct: CartProduct? = cart.getById(cartProductState.id)
        if (cartProduct != null) {
            cartProduct.quantity++
            cartProductState.quantity = cartProduct.quantity
            cartRepository.updateCartProductQuantity(
                id = cartProduct.id, quantity = cartProduct.quantity,
                failure = {}, success = {
                cart.setProductQuantityById(cartProduct.id, cartProduct.quantity)
                view.updateItem(cartProductState)
                updatePaymentAmount()
            }
            )
        }
    }

    override fun minusQuantity(cartProductState: CartProductState) {
        val cartProduct: CartProduct? = cart.getById(cartProductState.id)
        if (cartProduct != null) {
            cartProduct.quantity--
            cartProductState.quantity = cartProduct.quantity
            cartRepository.updateCartProductQuantity(
                id = cartProduct.id, quantity = cartProduct.quantity,
                failure = {}, success = {
                cart.setProductQuantityById(cartProduct.id, cartProduct.quantity)
                view.updateItem(cartProductState)
                updatePaymentAmount()
            }
            )
        }
    }

    override fun updatePickedByCartId(cartId: Long, checked: Boolean) {
        cart.setPickedById(cartId, checked)
        updatePaymentAmount()
    }

    override fun updatePaymentAmount() {
        val sum = cart.getPickedProductsTotalPrice()
        view.setTotalCost(sum)
    }

    override fun deleteCartProduct(cartProductState: CartProductState) {
        cartRepository.deleteCartProduct(
            id = cartProductState.id, failure = {},
            success = {
                loadCart()
            },
        )
    }

    override fun changeAllPicked() {
        if (cart.isAllPicked) {
            cart.setAllPicked(false)
            return
        }
        cart.setAllPicked(true)
    }

    override fun pickAll() {
        when (cart.isAllPicked) {
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

    override fun attachCartToOrder() {
        view.showOrderPage(cart.getPickedProducts().toUi())
    }
}
