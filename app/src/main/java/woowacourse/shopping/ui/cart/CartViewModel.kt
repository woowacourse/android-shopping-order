package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.common.Event
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.product.remote.retrofit.DataCallback
import woowacourse.shopping.data.product.remote.retrofit.RemoteProductRepository
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity

class CartViewModel(
    private val productRepository: RemoteProductRepository,
    private val cartRepository: RemoteCartRepository,
) : ViewModel(), CartListener {
    private val _cartUiState = MutableLiveData<Event<CartUiState>>()
    val cartUiState: LiveData<Event<CartUiState>> get() = _cartUiState

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _changedCartEvent = MutableLiveData<Event<Unit>>()
    val changedCartEvent: LiveData<Event<Unit>> get() = _changedCartEvent

    init {
        loadAllCartItems()
    }

    private fun loadAllCartItems() {
        _cartUiState.value = Event(CartUiState.Success(listOf()))
        val totalQuantityCount = cartRepository.syncGetCartQuantityCount()
        cartRepository.getAllCartItem(
            totalQuantityCount,
            object : DataCallback<List<CartItem>> {
                override fun onSuccess(result: List<CartItem>) {
                    result.forEach {
                        loadProduct(it)
                    }
                }

                override fun onFailure(t: Throwable) {
                }
            },
        )
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val uiModels = productUiModels() ?: listOf()
        val totalPrice = uiModels.sumOf { it.totalPrice() }
        _totalPrice.value = totalPrice
    }

    private fun loadProduct(cartItem: CartItem) {
        productRepository.find(
            cartItem.productId,
            object : DataCallback<Product> {
                override fun onSuccess(result: Product) {
                    updateCartUiState(result, cartItem)
                    updateTotalPrice()
                }

                override fun onFailure(t: Throwable) {
                }
            },
        )
    }

    @Synchronized
    private fun updateCartUiState(
        product: Product,
        cartItem: CartItem,
    ) {
        val oldCartUiModels =
            if (cartUiState.value?.peekContent() is CartUiState.Success) {
                (cartUiState.value?.peekContent() as CartUiState.Success).cartUiModels
            } else {
                listOf()
            }
        val resultCartUiModel = CartUiModel.from(product, cartItem)
        val newCartUiModels = oldCartUiModels.upsert(resultCartUiModel).sortedBy { it.cartItemId }
        val newCartUiState = Event(CartUiState.Success(newCartUiModels))
        _cartUiState.value = newCartUiState
    }

    private fun List<CartUiModel>.upsert(cartUiModel: CartUiModel): List<CartUiModel> {
        val list = toMutableList()
        if (this.none { it.cartItemId == cartUiModel.cartItemId }) {
            list += cartUiModel
        } else {
            this.forEachIndexed { index, listItem ->
                if (listItem.cartItemId == cartUiModel.cartItemId) {
                    list[index] = cartUiModel
                }
            }
        }
        return list.toList()
    }

    override fun deleteCartItem(productId: Int) {
        _changedCartEvent.value = Event(Unit)
        val cartUiModel = productUiModel(productId) ?: return
        cartRepository.deleteCartItem(
            cartUiModel.cartItemId,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    updateDeletedCart()
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    private fun updateDeletedCart() {
        loadAllCartItems()
    }

    override fun increaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = productUiModel(productId) ?: return
        var newQuantity = cartUiModel.quantity
        setQuantity(cartUiModel.cartItemId, ++newQuantity)
    }

    override fun decreaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = productUiModel(productId) ?: return
        if (cartUiModel.quantity.count == 1) {
            deleteCartItem(productId)
            return
        }

        var newQuantity = cartUiModel.quantity
        setQuantity(cartUiModel.cartItemId, --newQuantity)
    }

    private fun setQuantity(
        cartItemId: Int,
        quantity: Quantity,
    ) {
        cartRepository.setCartItemQuantity(
            cartItemId,
            quantity,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    loadAllCartItems()
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    private fun setError() {
        _cartUiState.value = Event(CartUiState.Failure)
    }

    private fun productUiModel(productId: Int): CartUiModel? {
        return productUiModels()?.find { it.productId == productId }
    }

    private fun productUiModels(): List<CartUiModel>? {
        val cartUiState = cartUiState.value?.peekContent() ?: return null
        if (cartUiState is CartUiState.Success) {
            return cartUiState.cartUiModels
        }
        return null
    }

    companion object {
        private const val INITIALIZE_CART_SIZE = 0
        private const val INITIALIZE_PAGE = 0
        private const val PAGE_SIZE = 5
    }
}
