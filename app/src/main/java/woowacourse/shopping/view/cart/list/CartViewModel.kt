package woowacourse.shopping.view.cart.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.CurrentScreen
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.cart.list.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.cart.recommend.RecommendProductEventListener
import woowacourse.shopping.view.home.product.HomeViewItem
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

    private var selectedCartItemIds: List<Int> = emptyList()

    init {
        loadCartItems()
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

    private fun makeOrder() {
        orderRepository.postOrder(
            selectedCartItemIds,
            onSuccess = {
                _recommendListUiEvent.value = Event(RecommendListUiEvent.NavigateBackToCartList)
            },
            onFailure = {
            },
        )
    }

    private fun loadCartItems() {
        val cartListUiState = cartListUiState.value ?: return
        _cartListUiState.value = cartListUiState.copy(isLoading = true)
        println(cartListUiState.currentPageIndex)
        cartRepository.getCartItems(
            page = cartListUiState.currentPageIndex,
            size = 5,
            sort = "asc",
            onSuccess = { cart ->
                println("previous enabled : ${cart.first.not()}")
                println("next enabled : ${cart.last.not()}")
                _cartListUiState.value =
                    cartListUiState.copy(
                        isLoading = false,
                        cartViewItems =
                            cart.cartItems.map { cartItem ->
                                CartViewItem(
                                    cartItem,
                                    cartUiState.value?.isEntireCheckboxSelected == true || cartItem.cartItemId in selectedCartItemIds,
                                )
                            },
                        previousPageEnabled = cart.first.not(),
                        nextPageEnabled = cart.last.not(),
                    )
            },
            onFailure = {
            },
        )
    }

    fun loadRecommendedItems() {
        val recentProduct = recentProductRepository.findMostRecentProduct()
        productRepository.getProducts(
            category = recentProduct?.category,
            page = 0,
            size = 100,
            sort = "asc",
            onSuccess = { productData ->
                val cartItems = cartListUiState.value?.cartViewItems ?: return@getProducts
                val products =
                    productData.products.shuffled().take(10).map { productItem ->
                        val quantity =
                            cartItems.firstOrNull { it.cartItem.product.id == productItem.id }?.cartItem?.quantity
                        HomeViewItem.ProductViewItem(productItem, quantity ?: 0)
                    }
                _recommendedListUiState.value =
                    recommendedListUiState.value?.copy(
                        isLoading = false,
                        recommendedProducts = products,
                    )
            },
            onFailure = {
            },
        )
    }

    fun navigateToPreviousPage() {
        println("navigate to previous page")

        _cartListUiState.value =
            cartListUiState.value?.copy(
                currentPageIndex = cartListUiState.value?.currentPageIndex?.minus(1) ?: return,
            )
        loadCartItems()
    }

    fun navigateToNextPage() {
        println("navigate to next page")
        _cartListUiState.value =
            cartListUiState.value?.copy(
                currentPageIndex = cartListUiState.value?.currentPageIndex?.plus(1) ?: return,
            )
        loadCartItems()
    }

    override fun onCartItemClick(productId: Int) {
        val lastlyViewedProduct = recentProductRepository.findMostRecentProduct()?.productId
        _cartListUiEvent.value =
            Event(
                CartListUiEvent.NavigateToProductDetail(
                    productId = productId,
                    lastlyViewed = productId == lastlyViewedProduct,
                ),
            )
    }

    override fun onDeleteButtonClick(itemId: Int) {
        cartRepository.deleteCartItem(
            cartItemId = itemId,
            onSuccess = {
                selectedCartItemIds = selectedCartItemIds.filter { it != itemId }
                loadCartItems()
            },
            onFailure = {
            },
        )
    }

    override fun onSelectChanged(
        itemId: Int,
        isSelected: Boolean,
    ) {
        _cartListUiState.value =
            cartListUiState.value?.copy(
                cartViewItems =
                    cartListUiState.value?.cartViewItems?.map {
                        if (it.cartItem.cartItemId == itemId) {
                            it.copy(isSelected = isSelected)
                        } else {
                            it
                        }
                    } ?: return,
            )
        val cartItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == itemId }
                ?: return
        if (isSelected) {
            selectedCartItemIds += itemId
            addTotalPrice(cartItem.cartItem.totalPrice())
        } else {
            selectedCartItemIds -= itemId
            subtractTotalPrice(cartItem.cartItem.totalPrice())
        }
    }

//    override fun addCartQuantity(cartItemId: Int) {
//        val cartViewItem =
//            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
//                ?: return
//        val changedItem = cartViewItem.copy(cartItem = cartViewItem.cartItem.plusQuantity())
//        addTotalPrice(changedItem.cartItem.product.price)
//    }
//
//    override fun subtractCartQuantity(cartItemId: Int) {
//        val cartViewItem =
//            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
//                ?: return
//        val changedItem = cartViewItem.copy(cartItem = cartViewItem.cartItem.minusQuantity())
//        subtractTotalPrice(changedItem.cartItem.product.price)
//        updateCartQuantity(cartItemId, changedItem)
//    }

    private fun updateCartQuantity(
        cartItemId: Int,
        changedItem: CartViewItem,
    ) {
        cartRepository.updateCartItem(
            cartItemId = cartItemId,
            quantity = changedItem.cartItem.quantity,
            onSuccess = {
                val updatedViewItems =
                    cartListUiState.value?.cartViewItems?.map {
                        if (it.cartItem.cartItemId == cartItemId) {
                            changedItem
                        } else {
                            it
                        }
                    } ?: return@updateCartItem
                _cartListUiState.value =
                    cartListUiState.value?.copy(
                        cartViewItems = updatedViewItems,
                    )
            },
            onFailure = {
            },
        )
    }

    override fun navigateToDetail(productId: Int) {
        val lastlyViewedProduct = recentProductRepository.findMostRecentProduct()?.productId
        _recommendListUiEvent.value =
            Event(
                RecommendListUiEvent.NavigateToProductDetail(
                    productId,
                    productId == lastlyViewedProduct,
                ),
            )
    }

    override fun navigateToCart() {
        _recommendListUiEvent.value = Event(RecommendListUiEvent.NavigateBackToCartList)
    }

    override fun addToCart(product: ProductItemDomain) {
        cartRepository.addCartItem(
            productId = product.id,
            quantity = 1,
            onSuccess = {
                cartRepository.getCartItems(
                    page = 0,
                    size = 1,
                    sort = "desc",
                    onSuccess = { cartDomain ->
                        val products = recommendedListUiState.value?.recommendedProducts
                        val target =
                            products?.firstOrNull { it.product.id == product.id }?.increase()
                                ?: return@getCartItems
                        println(target)
                        val updatedProducts =
                            products.map {
                                if (it.product.id == product.id) {
                                    target
                                } else {
                                    it
                                }
                            }
                        _recommendedListUiState.value =
                            recommendedListUiState.value?.copy(
                                recommendedProducts = updatedProducts,
                            )
                        selectedCartItemIds += cartDomain.cartItems.firstOrNull()?.cartItemId
                            ?: return@getCartItems
                    },
                    onFailure = {
                    },
                )
            },
            onFailure = {
            },
        )
    }

    override fun addQuantity(cartItemId: Int) {
        println("cartItemId : $cartItemId")
        when (currentScreen.value ?: return) {
            CurrentScreen.CART -> {
                val cartViewItem =
                    cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
                        ?: return
                val changedItem = cartViewItem.copy(cartItem = cartViewItem.cartItem.plusQuantity())
                addTotalPrice(changedItem.cartItem.product.price)
                updateCartQuantity(cartItemId, changedItem)
            }

            CurrentScreen.RECOMMEND -> {
                println("recommend")
                println(cartListUiState.value?.cartViewItems)
                val targetCartItem =
                    cartListUiState.value?.cartViewItems?.firstOrNull {
                        it.cartItem.product.id == cartItemId
                    }?.increment() ?: return
                println(targetCartItem)
                updateRecommendedProducts(targetCartItem, cartItemId)
            }
        }
    }

    override fun subtractQuantity(cartItemId: Int) {
        when (currentScreen.value ?: return) {
            CurrentScreen.CART -> {
                val cartViewItem =
                    cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
                        ?: return
                val changedItem =
                    cartViewItem.copy(cartItem = cartViewItem.cartItem.minusQuantity())
                subtractTotalPrice(changedItem.cartItem.product.price)
                updateCartQuantity(cartItemId, changedItem)
            }

            CurrentScreen.RECOMMEND -> {
                val targetCartItem =
                    cartListUiState.value?.cartViewItems?.firstOrNull {
                        it.cartItem.product.id == cartItemId
                    }?.decrement() ?: return
                if (targetCartItem.cartItem.quantity == 0) {
                    cartRepository.deleteCartItem(
                        cartItemId = targetCartItem.cartItem.cartItemId,
                        onSuccess = {
                            selectedCartItemIds -= targetCartItem.cartItem.cartItemId
                            val updatedProducts =
                                recommendedListUiState.value?.recommendedProducts?.map {
                                    if (it.product.id == cartItemId) {
                                        it.copy(_quantity = targetCartItem.cartItem.quantity)
                                    } else {
                                        it
                                    }
                                } ?: return@deleteCartItem
                            _recommendedListUiState.value =
                                recommendedListUiState.value?.copy(
                                    recommendedProducts = updatedProducts,
                                )
                        },
                        onFailure = {
                        },
                    )
                }
                updateRecommendedProducts(targetCartItem, cartItemId)
            }
        }
    }

    private fun updateRecommendedProducts(
        targetCartItem: CartViewItem,
        productId: Int,
    ) {
        cartRepository.updateCartItem(
            cartItemId = targetCartItem.cartItem.cartItemId,
            quantity = targetCartItem.cartItem.quantity,
            onSuccess = {
                val updatedProducts =
                    recommendedListUiState.value?.recommendedProducts?.map {
                        if (it.product.id == productId) {
                            it.copy(_quantity = targetCartItem.cartItem.quantity)
                        } else {
                            it
                        }
                    } ?: return@updateCartItem
                _recommendedListUiState.value =
                    recommendedListUiState.value?.copy(
                        recommendedProducts = updatedProducts,
                    )
            },
            onFailure = {
            },
        )
    }

    fun updateEntireCheck(isSelected: Boolean) {
        val updatedCartViewItems =
            if (isSelected) {
                cartListUiState.value?.cartViewItems?.map {
                    it.copy(isSelected = true)
                } ?: return
            } else {
                cartListUiState.value?.cartViewItems?.map {
                    it.copy(isSelected = false)
                } ?: return
            }
        _cartListUiState.value =
            cartListUiState.value?.copy(
                cartViewItems = updatedCartViewItems,
            )
        _cartUiState.value =
            cartUiState.value?.copy(
                isEntireCheckboxSelected = isSelected,
                totalPrice =
                    updatedCartViewItems.sumOf {
                        if (it.isSelected) {
                            it.cartItem.totalPrice()
                        } else {
                            0
                        }
                    },
            )
    }

    private fun addTotalPrice(additionalPrice: Int) {
        val cartUiState = cartUiState.value ?: return
        _cartUiState.value =
            cartUiState.copy(
                totalPrice = cartUiState.totalPrice + additionalPrice,
            )
    }

    private fun subtractTotalPrice(subtractPrice: Int) {
        val cartUiState = cartUiState.value ?: return
        _cartUiState.value =
            cartUiState.copy(
                totalPrice = cartUiState.totalPrice - subtractPrice,
            )
    }
}
