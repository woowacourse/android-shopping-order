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
        repository.getAllProductInCart(onSuccess = {
            cachedCartProducts.clear()
            cachedCartProducts.addAll(it)
            cartItems.updateItem(cachedCartProducts)
            repository.getSubList(cartOffset.getOffset(), STEP, onSuccess = { cartProducts ->
                view.setCarts(
                    cartProducts.map { it.toUIModel() },
                    CartUIModel(
                        cartOffset.getOffset() + 5 < cachedCartProducts.size,
                        0 < cartOffset.getOffset(),
                        cartOffset.getOffset() / 5 + 1,
                    ),
                )
            })
        }, {})
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
        repository.remove(id, onSuccess = {
            if (cartOffset.getOffset() == cachedCartProducts.size) {
                cartOffset = cartOffset.minus(STEP)
            }
            setUpCarts()
        }, onFailure = {})
    }

    override fun navigateToItemDetail(id: Long) {
        /*val product = repository.findById(id)?.product
        product.let {
            if (it != null) {
                view.navigateToItemDetail(it.toUIModel())
            }
        }*/
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
        repository.getSubList(cartOffset.getOffset(), STEP, onSuccess = { cartProducts ->
            cartProducts.map { it.toUIModel() }.forEach {
                onCheckChanged(it.product.id, isChecked)
                view.updateChecked(it.product.id, isChecked)
            }
        })
    }

    override fun setAllCheckbox() {
        repository.getSubList(cartOffset.getOffset(), STEP, onSuccess = {
            val allChecked = it.map { it.toUIModel() }.all { cartItems.isContain(it.product.id) }
            view.setAllCheckbox(allChecked)
        })
    }

    override fun setAllOrderCount() {
        view.setAllOrderCount(cartItems.getSize())
    }

    override fun increaseCount(id: Long) {
        val cartProduct = cachedCartProducts.find { it.product.id == id }
        cartProduct?.id?.let {
            repository.updateCount(it, getCount(id + 1), {
                cachedCartProducts.find { it.product.id == id }?.quantity = getCount(id) + 1
                cartItems.updateItem(cachedCartProducts)
                view.updateItem(id, getCount(id))
                if (cartItems.isContain(id)) {
                    updateCartItems()
                }
            }, {})
        }
    }

    override fun decreaseCount(id: Long) {
        val cartProduct = cachedCartProducts.find { it.product.id == id }
        cartProduct?.id?.let {
            repository.updateCount(it, getCount(id - 1), {
                cachedCartProducts.find { it.product.id == id }?.quantity = getCount(id) - 1
                cartItems.updateItem(cachedCartProducts)
                view.updateItem(id, getCount(id))
                if (cartItems.isContain(id)) {
                    updateCartItems()
                }
            }, {})
        }
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
