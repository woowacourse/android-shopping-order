package woowacourse.shopping.ui.cart.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.model.CartPageManager
import woowacourse.shopping.model.ProductWithQuantity
import woowacourse.shopping.model.Quantity
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.CartItemsUiState

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CountButtonClickListener {
    private val cartPageManager by lazy { CartPageManager(PAGE_SIZE) }
    private val _pageNumber: MutableLiveData<Int> = MutableLiveData()

    private val _canMoveNextPage: MutableLiveData<Boolean> = MutableLiveData()
    val canMoveNextPage: LiveData<Boolean> get() = _canMoveNextPage

    private val _canMovePreviousPage: MutableLiveData<Boolean> = MutableLiveData()
    val canMovePreviousPage: LiveData<Boolean> get() = _canMovePreviousPage

    val pageNumber: LiveData<Int> get() = _pageNumber

    val cart: MutableLiveData<CartItemsUiState> = MutableLiveData()

    val productWithQuantity: LiveData<List<ProductWithQuantity>> =
        cart.map {
            it.cartItems.map { cart ->
                ProductWithQuantity(productRepository.find(cart.productId), cart.quantity)
            }
        }

    init {
        loadCartItems()
        updatePageState()
    }

    fun removeCartItem(productId: Long) {
        val itemSize = cartRepository.getAllCartItems().size
        cartRepository.deleteCartItem(findCartIdByProductId(productId))
        _canMoveNextPage.value = cartPageManager.canMoveNextPage(itemSize)
        cart.value =
            CartItemsUiState(
                cartRepository.getCartItems(
                    cartPageManager.pageNum,
                    PAGE_SIZE,
                ),
            )
        loadCartItems()
    }

    fun plusPageNum() {
        cartPageManager.plusPageNum()
        loadCartItems()
        updatePageState()
    }

    fun minusPageNum() {
        cartPageManager.minusPageNum()
        loadCartItems()
        updatePageState()
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
            cart.value =
                CartItemsUiState(
                    cartRepository.getCartItems(
                        cartPageManager.pageNum,
                        PAGE_SIZE,
                    ),
                    isLoading = true,
                )
        }.onSuccess {
            handler.postDelayed({
                cart.value =
                    cart.value?.copy(isLoading = false)
            }, 2000)
        }
    }

    private fun updatePageState() {
        val itemSize = cartRepository.getAllCartItems().size
        _pageNumber.value = cartPageManager.pageNum
        _canMovePreviousPage.value = cartPageManager.canMovePreviousPage()
        _canMoveNextPage.value = cartPageManager.canMoveNextPage(itemSize)
    }

    private fun findCartIdByProductId(productId: Long): Long {
        return cart.value?.cartItems?.firstOrNull { it.productId == productId }?.id
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    private fun findCartItemQuantityByProductId(productId: Long): Quantity {
        return cart.value?.cartItems?.firstOrNull { it.productId == productId }?.quantity
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}
