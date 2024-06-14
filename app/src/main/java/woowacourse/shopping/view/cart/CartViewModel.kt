package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.list.CartItemClickListener
import woowacourse.shopping.view.cart.list.CartListUiEvent
import woowacourse.shopping.view.cart.list.CartListUiState
import woowacourse.shopping.view.cart.list.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.cart.recommend.RecommendListUiEvent
import woowacourse.shopping.view.cart.recommend.RecommendListUiState
import woowacourse.shopping.view.cart.recommend.RecommendProductEventListener
import woowacourse.shopping.view.home.product.HomeViewItem.ProductViewItem

class CartViewModel(
    private val cartRepository: CartRepository,
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

    var alteredProductIds: Array<Int> = arrayOf()
        private set

    init {
        loadCartItems()
        loadRecommendedItems()
    }

    fun navigate() {
        when (currentScreen.value ?: return) {
            CurrentScreen.CART -> {
                _cartListUiEvent.value = Event(CartListUiEvent.NavigateToRecommendList)
                _currentScreen.value = CurrentScreen.RECOMMEND
            }

            CurrentScreen.RECOMMEND -> {
                _recommendListUiEvent.value =
                    Event(
                        RecommendListUiEvent.NavigateToOrder(
                            cartUiState.value?.selectedCartItems ?: return,
                        ),
                    )
            }
        }
    }

    fun updateEntireCheck(isSelected: Boolean) {
        val updatedCartViewItems =
            cartListUiState.value?.cartViewItems?.map {
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
            val productId =
                cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == itemId }?.cartItem?.product?.id
            if (productId != null) alteredProductIds += productId
            _cartListUiState.value =
                cartListUiState.value?.copy(
                    cartViewItems =
                        cartListUiState.value?.cartViewItems?.filter {
                            it.cartItem.cartItemId != itemId
                        } ?: return@launch,
                )

            _cartUiState.value =
                uiState.copy(
                    selectedCartItems = uiState.selectedCartItems.filter { it.cartItemId != itemId },
                )
        }
    }

    override fun onSelectChanged(
        itemId: Int,
        isSelected: Boolean,
    ) {
        val cartViewItems =
            cartListUiState.value?.cartViewItems?.map {
                if (it.cartItem.cartItemId == itemId) {
                    it.copy(isSelected = isSelected)
                } else {
                    it
                }
            } ?: return
        _cartListUiState.value =
            cartListUiState.value?.copy(cartViewItems = cartViewItems)
        val cartItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == itemId }
                ?: return
        if (isSelected) {
            _cartUiState.value =
                cartUiState.value?.copy(
                    selectedCartItems =
                        cartUiState.value?.selectedCartItems?.plus(
                            cartItem.cartItem,
                        ) ?: return,
                )
        } else {
            _cartUiState.value =
                cartUiState.value?.copy(
                    selectedCartItems =
                        cartUiState.value?.selectedCartItems?.filter {
                            it.cartItemId != cartItem.cartItem.cartItemId
                        } ?: return,
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

    private fun loadRecommendedItems() {
        viewModelScope.launch {
            val recommendedProducts =
                productRepository.getRecommendedProducts(requiredSize = RECOMMENDED_ITEM_SIZE)
                    .getOrNull()
            val uiState = recommendedListUiState.value
            if (recommendedProducts == null || uiState == null) {
                return@launch
            }
            _recommendedListUiState.value =
                uiState.copy(
                    isLoading = false,
                    recommendedProducts =
                        recommendedProducts.map {
                            ProductViewItem(it)
                        },
                )
        }
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
                                    cartUiState.isEntireCheckboxSelected ||
                                        cartItem.cartItemId in (cartUiState.selectedCartItems.map { it.cartItemId }),
                                )
                            },
                    )
            }
        }
    }

    override fun addToCart(product: ProductItemDomain) {
        viewModelScope.launch {
            alteredProductIds += product.id
            val result = cartRepository.addCartItem(product.id, 1).getOrNull()
            val entireCartItems = cartRepository.getEntireCartItems().getOrNull()
            val uiState = recommendedListUiState.value
            if (result == null || uiState == null || entireCartItems == null) {
                return@launch
            }
            val changedItem =
                entireCartItems.firstOrNull { it.product.id == product.id } ?: return@launch
            val updatedProducts =
                uiState.recommendedProducts.map {
                    if (it.orderableProduct.productItemDomain.id == changedItem.product.id) {
                        it.copy(
                            orderableProduct =
                                it.orderableProduct.copy(
                                    cartData =
                                        CartData(
                                            changedItem.cartItemId,
                                            changedItem.product.id,
                                            quantity = changedItem.quantity,
                                        ),
                                ),
                        )
                    } else {
                        it
                    }
                }
            _recommendedListUiState.value =
                recommendedListUiState.value?.copy(
                    recommendedProducts = updatedProducts,
                )
            _cartUiState.value =
                cartUiState.value?.copy(
                    selectedCartItems =
                        cartUiState.value?.selectedCartItems?.plus(
                            changedItem,
                        ) ?: return@launch,
                )
        }
    }

    override fun addQuantity(cartItemId: Int) {
        alteredProductIds += cartItemId
        when (currentScreen.value ?: return) {
            CurrentScreen.CART -> addCartQuantity(cartItemId)
            CurrentScreen.RECOMMEND -> addRecommendItemQuantity(cartItemId)
        }
    }

    override fun subtractQuantity(cartItemId: Int) {
        alteredProductIds += cartItemId
        when (currentScreen.value ?: return) {
            CurrentScreen.CART -> subtractCartQuantity(cartItemId)
            CurrentScreen.RECOMMEND -> subtractRecommendItemQuantity(cartItemId)
        }
    }

    private fun addCartQuantity(cartItemId: Int) {
        val cartViewItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
                ?: return
        val changedItem = cartViewItem.copy(cartItem = cartViewItem.cartItem.plusQuantity())
        updateCartQuantity(cartItemId, changedItem)
    }

    private fun subtractCartQuantity(cartItemId: Int) {
        val cartViewItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
                ?: return
        val changedItem = cartViewItem.copy(cartItem = cartViewItem.cartItem.minusQuantity())
        updateCartQuantity(cartItemId, changedItem)
    }

    private fun addRecommendItemQuantity(cartItemId: Int) {
        val targetCartItem =
            recommendedListUiState.value?.recommendedProducts?.firstOrNull { it.orderableProduct.cartData?.cartItemId == cartItemId }
                ?: return
        val updatedCartItem =
            targetCartItem.copy(
                orderableProduct =
                    targetCartItem.orderableProduct.copy(
                        cartData = targetCartItem.orderableProduct.cartData?.increaseQuantity(),
                    ),
            )
        updateQuantity(updatedCartItem.orderableProduct.cartData ?: return)
    }

    private fun subtractRecommendItemQuantity(cartItemId: Int) {
        val targetCartItem =
            recommendedListUiState.value?.recommendedProducts?.firstOrNull { it.orderableProduct.cartData?.cartItemId == cartItemId }
                ?: return
        val updatedCartItem =
            targetCartItem.copy(
                orderableProduct =
                    targetCartItem.orderableProduct.copy(
                        cartData = targetCartItem.orderableProduct.cartData?.decreaseQuantity(),
                    ),
            )
        if (updatedCartItem.orderableProduct.cartData?.quantity == 0) {
            removeRecommendedCartItem(updatedCartItem.orderableProduct.cartData)
            return
        }
        updateQuantity(updatedCartItem.orderableProduct.cartData ?: return)
    }

    private fun updateQuantity(targetCartItem: CartData) {
        viewModelScope.launch {
            val result =
                cartRepository.updateCartItem(targetCartItem.cartItemId, targetCartItem.quantity)
                    .getOrNull()
            val uiState = cartUiState.value
            val recommendUiState = recommendedListUiState.value
            if (result == null || recommendUiState == null || uiState == null) return@launch

            val updatedItems =
                recommendUiState.recommendedProducts.map {
                    if (it.orderableProduct.productItemDomain.id == targetCartItem.productId) {
                        it.copy(orderableProduct = it.orderableProduct.copy(cartData = targetCartItem))
                    } else {
                        it
                    }
                }
            _recommendedListUiState.value =
                recommendUiState.copy(recommendedProducts = updatedItems)
            _cartUiState.value =
                uiState.copy(
                    totalPrice =
                        uiState.totalPrice +
                            updatedItems.sumOf {
                                it.orderableProduct.cartData?.totalPrice(it.orderableProduct.productItemDomain.price)
                                    ?: 0
                            },
                )
        }
    }

    private fun updateCartQuantity(
        cartItemId: Int,
        changedItem: CartViewItem,
    ) {
        viewModelScope.launch {
            alteredProductIds += changedItem.cartItem.product.id
            cartRepository.updateCartItem(
                cartItemId = cartItemId,
                quantity = changedItem.cartItem.quantity,
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

    private fun removeRecommendedCartItem(updatedCartItem: CartData) {
        viewModelScope.launch {
            val result =
                cartRepository.deleteCartItem(cartItemId = updatedCartItem.cartItemId).getOrNull()
            val recommendListUiState = recommendedListUiState.value
            val uiState = cartUiState.value

            if (result == null || recommendListUiState == null || uiState == null) return@launch

            val leftItems =
                recommendListUiState.recommendedProducts.map {
                    it.copy(
                        orderableProduct =
                            it.orderableProduct.copy(
                                cartData =
                                    if (it.orderableProduct.productItemDomain.id == updatedCartItem.productId) {
                                        null
                                    } else {
                                        it.orderableProduct.cartData
                                    },
                            ),
                    )
                }

            _recommendedListUiState.value =
                recommendedListUiState.value?.copy(recommendedProducts = leftItems)

            _cartUiState.value =
                cartUiState.value?.copy(
                    totalPrice =
                        uiState.totalPrice +
                            leftItems.sumOf {
                                it.orderableProduct.cartData?.totalPrice(it.orderableProduct.productItemDomain.price)
                                    ?: 0
                            },
                )
        }
    }

    private fun setTotalPrice() {
        val totalPrice =
            cartListUiState.value?.cartViewItems?.sumOf {
                if (it.isSelected) it.cartItem.totalPrice() else 0
            } ?: return
        val isEntirelySelected =
            cartListUiState.value?.cartViewItems?.all { it.isSelected } ?: return
        _cartUiState.value =
            cartUiState.value?.copy(
                isEntireCheckboxSelected = isEntirelySelected,
                totalPrice = totalPrice,
            )
    }

    companion object {
        private const val RECOMMENDED_ITEM_SIZE = 10
    }
}
