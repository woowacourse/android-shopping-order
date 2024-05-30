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
import woowacourse.shopping.model.Product
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

    val totalPrice: LiveData<Int> = _cart.map {
        it.cartItems.filter { it.isChecked }.sumOf { it.totalPrice }
    }

    val isTotalChbChecked: LiveData<Boolean> = _cart.map {
        it.cartItems.all { it.isChecked }
    }

    val checkedItemCount: LiveData<Int> = _cart.map {
        it.cartItems.filter { it.isChecked }.size
    }

    init {
        loadCartItems()
    }

    fun removeCartItem(productId: Long) {
        runCatching {
            cartRepository.deleteCartItem(findCartIdByProductId(productId))
        }.onSuccess {
            _cart.value = CartItemsUiState(
                cartRepository.getAllCartItemsWithProduct().map { it.toUiModel(findIsCheckedByProductId(it.product.id)) },
                isLoading = false
            ) }.onFailure {
            Log.d("테스트", "${it}")
        }
    }

    private fun findIsCheckedByProductId(productId:Long):Boolean {
        val current = requireNotNull(_cart.value)
        return current.cartItems.first { it.productId == productId }.isChecked
    }

    fun clickCheckBox(productId: Long) {
        val checkedCart = _cart.value?.cartItems?.firstOrNull { it.productId == productId }
            ?: error("해당하는 카트 아이템이 없습니다.")
        val currentList = requireNotNull(_cart.value?.cartItems?.toMutableList())
        currentList[currentList.indexOf(checkedCart)] =
            checkedCart.copy(isChecked = !checkedCart.isChecked)
        _cart.value = CartItemsUiState(currentList, isLoading = false)
    }

    fun totalCheckBoxCheck(isChecked: Boolean) {
        val currentCarts = requireNotNull(_cart.value)
        _cart.value = CartItemsUiState(currentCarts.cartItems.map {
            it.copy(isChecked = isChecked)
        }, isLoading = false)
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
                cartRepository.getAllCartItemsWithProduct().map { it.toUiModel(false) },
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

    private fun CartWithProduct.toUiModel(isChecked:Boolean) = CartUiModel(
        this.id,
        this.product.id,
        this.product.name,
        this.product.price,
        this.quantity,
        this.product.imageUrl,
        isChecked
    )
}
