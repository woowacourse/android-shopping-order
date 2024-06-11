package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.mapper.toUiModel
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecommendRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.CartModel
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.util.Event

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recommendRepository: RecommendRepository,
    initialTotalCartItemCount: Int,
) : ViewModel(), CartHandler {
    private val _error = MutableLiveData<Event<CartError>>()
    val error: LiveData<Event<CartError>> = _error

    private val _orderState = MutableLiveData<OrderState>(OrderState.CartList)
    val orderState: LiveData<OrderState> = _orderState

    private val _orderEvent = MutableLiveData<Event<OrderEvent>>()
    val orderEvent: LiveData<Event<OrderEvent>> = _orderEvent

    private val _recommendedProduct = MutableLiveData<Map<Long, ProductModel>>()
    val recommendedProduct: LiveData<Map<Long, ProductModel>> get() = _recommendedProduct

    private val _cartItems: MutableLiveData<UiState<List<CartModel>>> = MutableLiveData(UiState.Loading)
    val cartItems: LiveData<UiState<List<CartModel>>> = _cartItems

    private val _changedCartProducts: MutableMap<Long, Int> = mutableMapOf()
    val changedCartProducts: Map<Long, Int> get() = _changedCartProducts.toMap()

    private val cartItemsData
        get() = (cartItems.value as? UiState.Success)?.data ?: emptyList()

    val isAllCartItemsSelected: LiveData<Boolean> =
        cartItems.map {
            if (cartItemsData.isNotEmpty()) {
                cartItemsData.all { it.isChecked }
            } else {
                false
            }
        }

    private val selectedCartItems: LiveData<List<CartModel>> =
        cartItems.map { cartItemsData.filter { it.isChecked } }

    val totalPrice: LiveData<Long> = selectedCartItems.map { it.sumOf { it.price * it.quantity } }

    val totalCount: LiveData<Int> = selectedCartItems.map { it.sumOf { it.quantity } }

    val showSkeleton: LiveData<Boolean> = cartItems.map { it is UiState.Loading }

    val showTotalCheckBox: LiveData<Boolean> = orderState.map { it is OrderState.CartList }

    init {
        loadAllCartItems(initialTotalCartItemCount)
    }

    private fun loadAllCartItems(pageSize: Int) {
        viewModelScope.launch {
            cartRepository.load(0, pageSize)
                .map { carts -> carts.map { it.toUiModel() } }
                .onSuccess { loadedCarts ->
                    val newCartItems = processLoadedCarts(loadedCarts)
                    _cartItems.value = UiState.Success(newCartItems)
                }
                .onFailure {
                    _error.value = Event(CartError.CartItemsNotFound)
                }
        }
    }

    private fun processLoadedCarts(loadedCarts: List<CartModel>): List<CartModel> {
        val currentCartItems = cartItemsData.associateBy { it.cartId }
        return loadedCarts.map { cartModel ->
            val find = currentCartItems[cartModel.cartId]
            if (find == null) {
                cartModel.copy(isChecked = INITIAL_CHECK_VALUE)
            } else {
                cartModel.copy(isChecked = find.isChecked)
            }
        }
    }

    override fun deleteCartItem(cartId: Long) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(cartId)
                .onSuccess {
                    val deletedItem = cartItemsData.find { it.cartId == cartId } ?: return@launch
                    _changedCartProducts[deletedItem.productId] = 0
                    updateDeletedCartItem(cartId)
                }
                .onFailure {
                    _error.value = Event(CartError.CartItemNotDeleted)
                }
        }
    }

    override fun selectCartItem(cartId: Long) {
        val checkedCartItems =
            cartItemsData.map { cartItem ->
                if (cartItem.cartId == cartId) {
                    cartItem.copy(isChecked = !cartItem.isChecked)
                } else {
                    cartItem
                }
            }
        _cartItems.value = UiState.Success(checkedCartItems)
    }

    override fun selectAllCartItems(isChecked: Boolean) {
        val updatedItems = cartItemsData.map { it.copy(isChecked = isChecked) }
        _cartItems.value = UiState.Success(updatedItems)
    }

    override fun handleOrderState() {
        when (orderState.value ?: return) {
            OrderState.CartList -> {
                if (totalCount.value == 0) return
                _orderEvent.value = Event(OrderEvent.MoveToRecommend)
            }

            OrderState.Recommend -> {
                val cartItemIds = selectedCartItems.value?.map { it.cartId } ?: emptyList()
                val totalPrice = totalPrice.value ?: 0
                if (cartItemIds.isEmpty()) return
                _orderEvent.value = Event(OrderEvent.MoveToPayment(cartItemIds, totalPrice))
            }
        }
    }

    override fun decreaseQuantity(productId: Long) {
        val selectedItem = cartItemsData.find { it.productId == productId } ?: return
        val updatedQuantity = selectedItem.quantity - 1
        if (orderState.value is OrderState.CartList && updatedQuantity < 1) return
        val selectedCartId = selectedItem.cartId

        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(selectedCartId, updatedQuantity)
                .onSuccess {
                    if (updatedQuantity == 0) {
                        updateDeletedCartItem(selectedCartId)
                    } else {
                        updateCartItems(selectedCartId, updatedCartItem = selectedItem.copy(quantity = updatedQuantity))
                    }
                    _changedCartProducts[productId] = updatedQuantity
                    updateRecommendedProducts(productId, updatedQuantity)
                }
                .onFailure {
                    _error.value = Event(CartError.CartItemsNotModified)
                }
        }
    }

    override fun increaseQuantity(productId: Long) {
        val selectedItem = cartItemsData.find { it.productId == productId } ?: return
        val updatedQuantity = selectedItem.quantity + 1
        val selectedCartId = selectedItem.cartId

        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(selectedCartId, updatedQuantity)
                .onSuccess {
                    updateCartItems(selectedCartId, updatedCartItem = selectedItem.copy(quantity = updatedQuantity))
                    _changedCartProducts[productId] = updatedQuantity
                    updateRecommendedProducts(productId, updatedQuantity)
                }
                .onFailure { _error.value = Event(CartError.CartItemsNotModified) }
        }
    }

    private fun updateDeletedCartItem(deletedCartId: Long) {
        val updated = cartItemsData.filter { it.cartId != deletedCartId }
        _cartItems.value = UiState.Success(updated)
    }

    private fun updateCartItems(
        cartId: Long,
        updatedCartItem: CartModel,
    ) {
        val result =
            cartItemsData.map { cartModel ->
                if (cartModel.cartId == cartId) {
                    updatedCartItem
                } else {
                    cartModel
                }
            }
        _cartItems.value = UiState.Success(result)
    }

    private fun updateRecommendedProducts(
        productId: Long,
        resultQuantity: Int,
    ) {
        if (recommendedProduct.value?.get(productId) == null) return
        _recommendedProduct.value =
            recommendedProduct.value?.toMutableMap()?.apply {
                this[productId]?.let {
                    this[productId] = it.copy(quantity = resultQuantity)
                }
            }
    }

    fun loadRecommendProducts() {
        viewModelScope.launch {
            val existingProductIds = cartItemsData.map { it.productId }
            recommendRepository.generateRecommendProducts(existingProductIds)
                .map { recommend -> recommend.map { it.toUiModel() } }
                .onSuccess { recommendModel ->
                    _recommendedProduct.value = recommendModel.associateBy { it.id }
                }
                .onFailure {
                    _error.value = Event(CartError.RecommendItemsNotFound)
                }
        }
    }

    override fun addProductToCart(productId: Long) {
        viewModelScope.launch {
            cartRepository.saveNewCartItem(productId, INITIAL_COUNT)
                .onSuccess {
                    updateRecommendedProducts(productId, INITIAL_COUNT)
                    loadAllCartItems(cartItemsData.sumOf { it.quantity } + INITIAL_COUNT)
                    _changedCartProducts[productId] = INITIAL_COUNT
                }
                .onFailure { _error.value = Event(CartError.CartItemsNotModified) }
        }
    }

    fun setOrderState(state: OrderState) {
        _orderState.value = state
    }

    companion object {
        const val INITIAL_CHECK_VALUE = true
        const val INITIAL_COUNT = 1

        class Factory(
            private val cartRepository: CartRepository,
            private val recommendRepository: RecommendRepository,
            private val initialTotalCartItemCount: Int,
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(
                    cartRepository,
                    recommendRepository,
                    initialTotalCartItemCount,
                ) as T
            }
        }
    }
}
