package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.list.CartItemClickListener
import woowacourse.shopping.view.cart.list.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.cart.recommend.RecommendProductEventListener
import woowacourse.shopping.view.home.product.HomeViewItem
import woowacourse.shopping.view.home.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.view.state.CartListUiEvent
import woowacourse.shopping.view.state.CartListUiState
import woowacourse.shopping.view.state.CartUiState
import woowacourse.shopping.view.state.RecommendListUiEvent
import woowacourse.shopping.view.state.RecommendListUiState

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel(),
    CartItemClickListener,
    QuantityEventListener,
    RecommendProductEventListener {
    private val _currentScreen: MutableLiveData<CurrentScreen> = MutableLiveData(CurrentScreen.CART)
    val currentScreen: LiveData<CurrentScreen>
        get() = _currentScreen

    private val _cartUiState: MutableLiveData<CartUiState> = MutableLiveData(CartUiState())
    val cartUiState: LiveData<CartUiState>
        get() = _cartUiState

    private val _navigateBackToHome: MutableLiveData<Event<Unit>> = MutableLiveData()
    val navigateBackToHome: LiveData<Event<Unit>>
        get() = _navigateBackToHome

    private val _cartListUiState: MutableLiveData<CartListUiState> =
        MutableLiveData(CartListUiState())
    val cartListUiState: LiveData<CartListUiState>
        get() = _cartListUiState

    private val _cartListUiEvent: MutableLiveData<Event<CartListUiEvent>> = MutableLiveData()
    val cartListUiEvent: LiveData<Event<CartListUiEvent>>
        get() = _cartListUiEvent

    private val _recommendedListUiState: MutableLiveData<RecommendListUiState> =
        MutableLiveData(RecommendListUiState())
    val recommendedListUiState: LiveData<RecommendListUiState>
        get() = _recommendedListUiState

    private val _recommendListUiEvent: MutableLiveData<Event<RecommendListUiEvent>> =
        MutableLiveData()
    val recommendListUiEvent: LiveData<Event<RecommendListUiEvent>>
        get() = _recommendListUiEvent

    init {
        loadCartItems()
    }

    fun loadRecommendedItems() {
        viewModelScope.launch {
            val recommendedProducts = productRepository.getRecommendedProducts().getOrNull()
            val uiState = recommendedListUiState.value
            if (recommendedProducts == null || uiState == null) {
                return@launch
            }
            _recommendedListUiState.value =
                uiState.copy(
                    isLoading = false,
                    recommendedProducts = recommendedProducts.map {
                        ProductViewItem(it.productItemDomain, it.cartData?.quantity ?: 0)
                    }
                )
        }
    }

    fun navigate() {
        when (currentScreen.value ?: return) {
            CurrentScreen.CART -> {
                _cartListUiEvent.value = Event(CartListUiEvent.NavigateToRecommendList)
                _currentScreen.value = CurrentScreen.RECOMMEND
            }

            CurrentScreen.RECOMMEND -> {
                makeOrder()
            }
        }
    }

    fun navigateBackToHome() {
        _navigateBackToHome.value = Event(Unit)
    }

    override fun onCartItemClick(productId: Int) {
        viewModelScope.launch {
            val lastlyViewedProduct = recentProductRepository.findMostRecentProduct()?.productId
            _cartListUiEvent.value =
                Event(
                    CartListUiEvent.NavigateToProductDetail(
                        productId = productId,
                        lastlyViewed = productId == lastlyViewedProduct,
                    ),
                )
        }
    }

    override fun onDeleteButtonClick(itemId: Int) {
        viewModelScope.launch {
            val result = cartRepository.deleteCartItem(itemId).getOrNull()
            val uiState = cartUiState.value
            if (result == null || uiState == null) {
                return@launch
            }
            _cartUiState.value = uiState.copy(
                selectedCartItemIds = uiState.selectedCartItemIds.filter { it != itemId }
            )
        }
    }

    override fun onSelectChanged(
        itemId: Int,
        isSelected: Boolean,
    ) {
        val cartViewItems = cartListUiState.value?.cartViewItems?.map {
            if (it.cartItem.cartItemId == itemId)
                it.copy(isSelected = isSelected) else it
        } ?: return
        println(cartViewItems)
        _cartListUiState.value =
            cartListUiState.value?.copy(cartViewItems = cartViewItems)
        val cartItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == itemId }
                ?: return
        if (isSelected) {
            _cartUiState.value = cartUiState.value?.copy(
                selectedCartItemIds = cartUiState.value?.selectedCartItemIds?.plus(
                    itemId
                ) ?: return
            )
        } else {
            _cartUiState.value = cartUiState.value?.copy(
                selectedCartItemIds = cartUiState.value?.selectedCartItemIds?.minus(
                    itemId
                ) ?: return
            )
        }
        setTotalPrice()
    }

    override fun navigateToDetail(productId: Int) {
        viewModelScope.launch {
            val lastlyViewedProduct = recentProductRepository.findMostRecentProduct()?.productId
            _recommendListUiEvent.value =
                Event(
                    RecommendListUiEvent.NavigateToProductDetail(
                        productId,
                        productId == lastlyViewedProduct,
                    ),
                )
        }
    }

    override fun navigateToCart() {
        _recommendListUiEvent.value = Event(RecommendListUiEvent.NavigateBackToCartList)
    }

    override fun addToCart(product: ProductItemDomain) {
        viewModelScope.launch {
            val result = cartRepository.addCartItem(product.id, 1).getOrNull()
            val entireCartItems = cartRepository.getEntireCartItems().getOrNull()
            val uiState = recommendedListUiState.value
            if (result == null || uiState == null || entireCartItems == null) {
                return@launch
            }
            val target =
                uiState.recommendedProducts.firstOrNull { it.product.id == product.id }?.increase()
            val cartItemId = entireCartItems.firstOrNull { it.productId == product.id }?.cartItemId
            if (target == null || cartItemId == null) {
                return@launch
            }
            val updatedProducts = uiState.recommendedProducts.map {
                if (it.product.id == target.product.id) target else it
            }
            _recommendedListUiState.value =
                recommendedListUiState.value?.copy(
                    recommendedProducts = updatedProducts,
                )
            _cartUiState.value = cartUiState.value?.copy(
                selectedCartItemIds = cartUiState.value?.selectedCartItemIds?.plus(
                    cartItemId
                ) ?: return@launch
            )
        }
    }

    override fun addQuantity(cartItemId: Int) {
        val cartViewItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
                ?: return
        val changedItem = cartViewItem.copy(cartItem = cartViewItem.cartItem.plusQuantity())
        updateCartQuantity(cartItemId, changedItem)
    }

    override fun subtractQuantity(cartItemId: Int) {
        val cartViewItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
                ?: return
        val changedItem =
            cartViewItem.copy(cartItem = cartViewItem.cartItem.minusQuantity())
        updateCartQuantity(cartItemId, changedItem)
    }

    fun updateEntireCheck(isSelected: Boolean) {
        val updatedCartViewItems = cartListUiState.value?.cartViewItems?.map {
            it.copy(isSelected = isSelected)
        } ?: return
        _cartListUiState.value =
            cartListUiState.value?.copy(cartViewItems = updatedCartViewItems)
        _cartUiState.value =
            cartUiState.value?.copy(
                isEntireCheckboxSelected = isSelected,
                totalPrice =
                updatedCartViewItems.sumOf { if (it.isSelected) it.cartItem.totalPrice() else 0 },
            )
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            _cartListUiState.value = cartListUiState.value?.copy(isLoading = true)
            val cartListUiState = cartListUiState.value
            val cartUiState = cartUiState.value
            if (cartUiState == null || cartListUiState == null) {
                return@launch
            }
            cartRepository.getEntireCartItemsForCart().onSuccess { cartItems ->
                _cartListUiState.value =
                    cartListUiState.copy(
                        isLoading = false,
                        cartViewItems =
                        cartItems.cartItems.map { cartItem ->
                            CartViewItem(
                                cartItem,
                                cartUiState.isEntireCheckboxSelected || cartItem.cartItemId in (cartUiState.selectedCartItemIds),
                            )
                        },
                    )
            }.onFailure {

            }
        }
    }

    private fun makeOrder() {
        viewModelScope.launch {
            orderRepository.postOrder(
                cartUiState.value?.selectedCartItemIds ?: return@launch
            ).onSuccess {
                _recommendListUiEvent.value = Event(RecommendListUiEvent.NavigateBackToHome)
            }.onFailure {

            }
        }
    }

    private fun updateCartQuantity(
        cartItemId: Int,
        changedItem: CartViewItem,
    ) {
        viewModelScope.launch {
            cartRepository.updateCartItem(
                cartItemId = cartItemId,
                quantity = changedItem.cartItem.quantity
            ).onSuccess {
                val updatedViewItems =
                    cartListUiState.value?.cartViewItems?.map {
                        if (it.cartItem.cartItemId == cartItemId) changedItem else it
                    } ?: return@launch
                _cartListUiState.value =
                    cartListUiState.value?.copy(
                        cartViewItems = updatedViewItems,
                    )
                setTotalPrice()
            }
        }
    }

    private fun setTotalPrice() {
        val totalPrice = cartListUiState.value?.cartViewItems?.sumOf {
            if (it.isSelected) it.cartItem.totalPrice() else 0
        } ?: return
        val isEntirelySelected =
            cartListUiState.value?.cartViewItems?.all { it.isSelected } ?: return
        _cartUiState.value = cartUiState.value?.copy(
            isEntireCheckboxSelected = isEntirelySelected,
            totalPrice = totalPrice
        )
    }
}
