package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.local.database.AppDatabase
import woowacourse.shopping.local.datasource.LocalRecentViewedDataSourceImpl
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.CartModel
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.ui.model.toUiModel
import woowacourse.shopping.presentation.util.Event
import woowacourse.shopping.remote.datasource.RemoteCartDataSourceImpl
import woowacourse.shopping.remote.datasource.RemoteOrderDataSourceImpl
import woowacourse.shopping.remote.datasource.RemoteProductDataSourceImpl

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val orderRepository: OrderRepository,
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
                .onSuccess { carts ->
                    val loadedCarts = carts.map { it.toUiModel() }
                    val currentCartItems = cartItemsData.associateBy { it.cartId }
                    val newCartItems =
                        loadedCarts.map { cartModel ->
                            val find = currentCartItems[cartModel.cartId]
                            if (find == null) {
                                cartModel.copy(isChecked = true)
                            } else {
                                cartModel.copy(isChecked = find.isChecked)
                            }
                        }
                    _cartItems.value = UiState.Success(newCartItems)
                }
                .onFailure {
                    _error.value = Event(CartError.CartItemsNotFound)
                }
        }
    }

    override fun onDeleteClick(cartId: Long) {
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

    override fun onCheckBoxClicked(cartId: Long) {
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

    override fun onTotalCheckBoxClicked(isChecked: Boolean) {
        val updatedItems = cartItemsData.map { it.copy(isChecked = isChecked) }
        _cartItems.value = UiState.Success(updatedItems)
    }

    override fun onOrderClicked() {
        when (orderState.value ?: return) {
            OrderState.CartList -> {
                if (totalCount.value == 0) return
                _orderEvent.value = Event(OrderEvent.MoveToRecommend)
            }

            OrderState.Recommend -> {
                val cartItemIds = selectedCartItems.value?.map { it.cartId } ?: emptyList()
                if (cartItemIds.isNotEmpty()) {
                    viewModelScope.launch {
                        orderRepository.completeOrder(cartItemIds)
                            .onSuccess {
                                selectedCartItems.value?.forEach { cartModel -> _changedCartProducts[cartModel.productId] = 0 }
                                _orderEvent.value = Event(OrderEvent.CompleteOrder)
                            }
                            .onFailure { }
                    }
                }
            }
        }
    }

    override fun onDecreaseQuantity(productId: Long) {
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

    override fun onIncreaseQuantity(productId: Long) {
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

    fun buildRecommendProducts() {
        viewModelScope.launch {
            recentRepository.loadMostRecent().onSuccess { product ->
                val recentViewedCategory = product?.category ?: return@launch
                viewModelScope.launch {
                    productRepository.loadWithCategory(recentViewedCategory, 0, 20)
                        .onSuccess { products ->
                            val existingCartItem = cartItemsData.groupBy { it.productId }
                            _recommendedProduct.value =
                                products.asSequence().filter { existingCartItem[it.id] == null }
                                    .map { it.toUiModel() }.associateBy { it.id }
                        }
                        .onFailure { }
                }
            }
        }
    }

    override fun onPlusButtonClick(productId: Long) {
        val initialCount = 1

        viewModelScope.launch {
            cartRepository.saveNewCartItem(productId, initialCount)
                .onSuccess {
                    updateRecommendedProducts(productId, initialCount)
                    loadAllCartItems(cartItemsData.sumOf { it.quantity } + initialCount)
                    _changedCartProducts[productId] = initialCount
                }
                .onFailure { _error.value = Event(CartError.CartItemsNotModified) }
        }
    }

    fun setOrderState(state: OrderState) {
        _orderState.value = state
    }

    companion object {
        class Factory(private val initialTotalCartItemCount: Int) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                return CartViewModel(
                    cartRepository = CartRepositoryImpl(remoteCartDataSource = RemoteCartDataSourceImpl()),
                    productRepository = ProductRepositoryImpl(RemoteProductDataSourceImpl()),
                    recentRepository = RecentProductRepositoryImpl(LocalRecentViewedDataSourceImpl(recentDao)),
                    orderRepository = OrderRepositoryImpl(RemoteOrderDataSourceImpl()),
                    initialTotalCartItemCount,
                ) as T
            }
        }
    }
}
