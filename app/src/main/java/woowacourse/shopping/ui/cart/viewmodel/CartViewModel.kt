package woowacourse.shopping.ui.cart.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cart.CartWithProduct
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.model.ProductWithQuantity
import woowacourse.shopping.model.Quantity
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.CartItemsUiState
import woowacourse.shopping.ui.cart.CartUiModel

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CountButtonClickListener {
    private val _cart: MutableLiveData<CartItemsUiState> = MutableLiveData()

    val cart: LiveData<CartItemsUiState> = _cart

    init {
        loadCartItems()
    }

    fun removeCartItem(productId: Long) {
        cartRepository.deleteCartItem(findCartIdByProductId(productId))
        _cart.value = CartItemsUiState(
            cartRepository.getAllCartItemsWithProduct().map { it.toUiModel() },
            isLoading = false
        )
        loadCartItems()
    }

    fun clickCheckBox(productId: Long) {
        val checkedCart = _cart.value?.cartItems?.firstOrNull { it.productId == productId }
            ?: error("해당하는 카트 아이템이 없습니다.")
        val currentList = requireNotNull(_cart.value?.cartItems?.toMutableList())
        currentList[currentList.indexOf(checkedCart)] =
            checkedCart.copy(isChecked = !checkedCart.isChecked)
        _cart.value = CartItemsUiState(currentList, isLoading = false)
    }

    override fun plusCount(productId: Long) {
        cartRepository.patchCartItem(
            findCartIdByProductId(productId),
            findCartItemQuantityByProductId(productId).inc().value,
        )
        loadCartItems()
    }

    override fun minusCount(productId: Long) {
        cartRepository.patchCartItem(
            findCartIdByProductId(productId),
            findCartItemQuantityByProductId(productId).dec().value,
        )
        loadCartItems()
    }

    private fun loadCartItems() {
        val handler = Handler(Looper.getMainLooper())
        runCatching {
            _cart.value = CartItemsUiState(
                cartRepository.getAllCartItemsWithProduct().map { it.toUiModel() },
                isLoading = true
            )
        }.onSuccess {
            handler.postDelayed({
                _cart.value =
                    cart.value?.copy(isLoading = false)
            }, 500)
        }
    }

    private fun findCartIdByProductId(productId: Long): Long {
        return cart.value?.cartItems?.firstOrNull { it.productId == productId }?.id
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    private fun findCartItemQuantityByProductId(productId: Long): Quantity {
        return cart.value?.cartItems?.firstOrNull { it.productId == productId }?.quantity
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    private fun CartWithProduct.toUiModel() = CartUiModel(
        this.id,
        this.product.id,
        this.product.name,
        this.product.price,
        this.quantity,
        this.product.imageUrl
    )
}
