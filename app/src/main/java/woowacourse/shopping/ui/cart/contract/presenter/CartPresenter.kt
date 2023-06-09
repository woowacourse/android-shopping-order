package woowacourse.shopping.ui.cart.contract.presenter

import com.example.domain.model.CartItems
import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartUIModel
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.cart.CartOffset
import woowacourse.shopping.ui.cart.contract.CartContract

class CartPresenter(
    val repository: CartRepository,
    val view: CartContract.View,
    offset: Int = 0,
) : CartContract.Presenter {
    private val cartItems = CartItems()
    private var cartOffset = CartOffset(offset, repository)

    override fun setUpCarts() {
        repository.getSubList(cartOffset.getOffset(), STEP) { cartProducts ->
            updateCartItems()
            showCartItems(cartProducts)
            refreshCartSummary()
        }
    }

    private fun showCartItems(cartProducts: List<CartProduct>) {
        repository.getAllProductInCart {
            view.setCarts(
                cartProducts.map { it.toUIModel() },
                CartUIModel(
                    cartOffset.getOffset() + STEP < it.size,
                    0 < cartOffset.getOffset(),
                    cartOffset.getOffset() / STEP + 1,
                ),
            )
        }
    }

    private fun updateCartItems() {
        repository.getAllProductInCart {
            cartItems.updateItem(it)
        }
    }

    override fun pageUp() {
        cartOffset = cartOffset.plus(STEP)
        setUpCarts()
    }

    override fun pageDown() {
        cartOffset = cartOffset.minus(STEP)
        setUpCarts()
    }

    override fun removeItem(id: Long) {
        repository.remove(id) {
            checkPage(cartOffset.getOffset())
            setUpCarts()
            refreshCartSummary()
        }
    }

    private fun checkPage(offset: Int) {
        repository.getAllProductInCart { cartProducts ->
            if (offset == cartProducts.size) {
                cartOffset = cartOffset.minus(STEP)
            }
        }
    }

    override fun navigateToItemDetail(id: Long) {
        repository.findById(id) {
            if (it != null) {
                view.navigateToItemDetail(it.product.toUIModel())
            }
        }
    }

    override fun saveOffsetState(outState: MutableMap<String, Int>) {
        outState[CartActivity.KEY_OFFSET] = cartOffset.getOffset()
    }

    override fun restoreOffsetState(state: Map<String, Int>) {
        val savedOffset = state[CartActivity.KEY_OFFSET] ?: 0
        cartOffset = CartOffset(savedOffset, repository)
    }

    override fun onCheckChanged(id: Long, isChecked: Boolean) {
        repository.findByProductId(id) {
            if (it == null) return@findByProductId
            if (isChecked) {
                cartItems.insert(it)
            } else {
                cartItems.remove(it.product.id)
            }
            refreshCartSummary()
            setAllCheckbox()
        }
    }

    override fun setCartItemsPrice() {
        view.setCartItemsPrice(cartItems.getPrice())
    }

    override fun onAllCheckboxClick(isChecked: Boolean) {
        repository.getSubList(cartOffset.getOffset(), STEP) { cartProducts ->
            cartProducts.map { it.toUIModel() }.forEach {
                onCheckChanged(it.product.id, isChecked)
                view.updateChecked(it.product.id, isChecked)
            }
        }
    }

    override fun setAllCheckbox() {
        repository.getSubList(cartOffset.getOffset(), STEP) {
            val allChecked =
                it.map { it.toUIModel() }.all { cartItems.isContain(it.product.id) }
            view.setAllCheckbox(allChecked)
        }
    }

    override fun setAllOrderCount() {
        view.setAllOrderCount(cartItems.getSize())
    }

    override fun increaseCount(id: Long) {
        repository.findByProductId(id) {
            if (it != null) {
                updateCartProductQuantity(it, it.quantity + 1)
                if (cartItems.isContain(id)) {
                    refreshCartSummary()
                }
            }
        }
    }

    override fun decreaseCount(id: Long) {
        repository.findByProductId(id) {
            if (it != null) {
                updateCartProductQuantity(it, it.quantity - 1)
                if (cartItems.isContain(id)) {
                    refreshCartSummary()
                }
            }
        }
    }

    override fun navigateToOrder() {
        view.navigateToOrder(cartItems.toUIModel())
    }

    private fun updateCartProductQuantity(carProduct: CartProduct, count: Int) {
        repository.updateCount(carProduct.id, count) {
            updateCartItems()
            view.updateItem(carProduct.product.id, count)
        }
    }

    private fun refreshCartSummary() {
        setAllOrderCount()
        setCartItemsPrice()
    }

    companion object {
        private const val STEP = 5
    }
}
