package woowacourse.shopping.ui.cart.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.model.ProductWithQuantity
import woowacourse.shopping.model.Quantity
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.CartItemsUiState

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CountButtonClickListener {
    val cart: MutableLiveData<CartItemsUiState> = MutableLiveData()

    val productWithQuantity: LiveData<List<ProductWithQuantity>> =
        cart.map {
            it.cartItems.map { cart ->
                ProductWithQuantity(productRepository.find(cart.productId), cart.quantity)
            }
        }

    init {
        loadCartItems()
    }

    fun removeCartItem(productId: Long) {
        cartRepository.deleteCartItem(findCartIdByProductId(productId))
        cart.value = CartItemsUiState(cartRepository.getAllCartItems())
        loadCartItems()
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
            cart.value = CartItemsUiState(cartRepository.getAllCartItems(), isLoading = true)
        }.onSuccess {
            handler.postDelayed({
                cart.value =
                    cart.value?.copy(isLoading = false)
            }, 2000)
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
}
