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
    private var cachedCartProducts = mutableListOf<CartProduct>()

    override fun setUpCarts() {
        repository.getAllProductInCart().getOrNull()?.let { cartProducts ->
            cachedCartProducts.clear()
            cachedCartProducts.addAll(cartProducts)
            cartItems.updateItem(cachedCartProducts)
        }
        val cartProducts = repository.getSubList(cartOffset.getOffset(), STEP).getOrNull()
        view.setCarts(
            cartProducts?.map { it.toUIModel() } ?: emptyList(),
            CartUIModel(
                cartOffset.getOffset() + STEP < cachedCartProducts.size,
                0 < cartOffset.getOffset(),
                cartOffset.getOffset() / STEP + 1,
            ),
        )
        updateCartItems()
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
        repository.remove(id).getOrNull()?.let {
            if (cartOffset.getOffset() == cachedCartProducts.size) {
                cartOffset = cartOffset.minus(STEP)
            }
        }
        setUpCarts()
        updateCartItems()
    }

    override fun navigateToItemDetail(id: Long) {
        val product = repository.findById(id).getOrNull()
        product.let {
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
        cachedCartProducts.find { it.product.id == id }?.let {
            if (isChecked) {
                cartItems.insert(it)
            } else {
                cartItems.remove(it.product.id)
            }
        }
        updateCartItems()
        setAllCheckbox()
    }

    override fun setCartItemsPrice() {
        view.setCartItemsPrice(cartItems.getPrice())
    }

    override fun onAllCheckboxClick(isChecked: Boolean) {
        repository.getSubList(cartOffset.getOffset(), STEP).getOrNull()?.let { cartProducts ->
            cartProducts.map { it.toUIModel() }.forEach {
                onCheckChanged(it.product.id, isChecked)
                view.updateChecked(it.product.id, isChecked)
            }
        }
    }

    override fun setAllCheckbox() {
        repository.getSubList(cartOffset.getOffset(), STEP).getOrNull()?.let {
            val allChecked = it.map { it.toUIModel() }.all { cartItems.isContain(it.product.id) }
            view.setAllCheckbox(allChecked)
        }
    }

    override fun setAllOrderCount() {
        view.setAllOrderCount(cartItems.getSize())
    }

    override fun increaseCount(id: Long) {
        val cartProduct = cachedCartProducts.find { it.product.id == id }
        cartProduct?.id?.let {
            repository.updateCount(it, getCount(id) + 1).getOrNull().let {
                cachedCartProducts.find { it.product.id == id }?.quantity = getCount(id) + 1
                cartItems.updateItem(cachedCartProducts)
            }
            view.updateItem(id, getCount(id))
            if (cartItems.isContain(id)) {
                updateCartItems()
            }
        }
    }

    override fun decreaseCount(id: Long) {
        val cartProduct = cachedCartProducts.find { it.product.id == id }
        cartProduct?.id?.let {
            repository.updateCount(it, getCount(id) - 1).getOrNull().let {
                cachedCartProducts.find { it.product.id == id }?.quantity = getCount(id) - 1
                cartItems.updateItem(cachedCartProducts)
            }
            view.updateItem(id, getCount(id))
            if (cartItems.isContain(id)) {
                updateCartItems()
            }
        }
    }

    override fun navigateToOrder() {
        view.navigateToOrder(cartItems.toUIModel())
    }

    private fun getCount(id: Long): Int {
        return cachedCartProducts.find { it.product.id == id }?.quantity ?: 0
    }

    private fun updateCartItems() {
        setAllOrderCount()
        setCartItemsPrice()
    }

    companion object {
        private const val STEP = 5
    }
}
