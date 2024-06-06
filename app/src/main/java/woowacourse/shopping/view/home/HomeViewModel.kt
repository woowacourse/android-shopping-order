package woowacourse.shopping.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.CartDomain
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.home.product.HomeViewItem
import woowacourse.shopping.view.state.HomeProductUiState
import woowacourse.shopping.view.state.HomeUiEvent
import woowacourse.shopping.view.state.RecentProductUiState

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(), HomeEventListener, QuantityEventListener {
    private val _homeProductUiState: MutableLiveData<HomeProductUiState> =
        MutableLiveData(HomeProductUiState())
    val homeProductUiState: LiveData<HomeProductUiState>
        get() = _homeProductUiState

    private val _recentProductUiState: MutableLiveData<RecentProductUiState> =
        MutableLiveData(RecentProductUiState())
    val recentProductUiState: LiveData<RecentProductUiState>
        get() = _recentProductUiState

    private val _homeUiEvent: MutableLiveData<Event<HomeUiEvent>> = MutableLiveData()
    val homeUiEvent: LiveData<Event<HomeUiEvent>>
        get() = _homeUiEvent

    init {
        loadRecentItems()
        loadProducts()
    }

    override fun addQuantity(cartItemId: Int) {
        val targetCartItem =
            homeProductUiState.value?.cartItems?.firstOrNull { it.cartItemId == cartItemId }
                ?: return
        val updatedCartItem = targetCartItem.increaseQuantity()
        updateQuantity(updatedCartItem)
    }

    override fun subtractQuantity(cartItemId: Int) {
        val targetCartItem =
            homeProductUiState.value?.cartItems?.firstOrNull { it.cartItemId == cartItemId }
                ?: return
        val updatedCartItem = targetCartItem.decreaseQuantity()
        if (updatedCartItem.quantity == 0) {
            removeCartItem(updatedCartItem)
            return
        }
        updateQuantity(updatedCartItem)
    }

    private fun updateQuantity(targetCartItem: CartData) {
        viewModelScope.launch {
            val result =
                cartRepository.updateCartItem(targetCartItem.cartItemId, targetCartItem.quantity)
                    .getOrNull()

            val uiState = homeProductUiState.value

            if (result == null || uiState == null) {
                notifyError()
                return@launch
            }

            val cartItems = uiState.cartItems.map {
                if (it.productId == targetCartItem.productId) {
                    it.copy(quantity = targetCartItem.quantity)
                } else {
                    it
                }
            }

            val productItems = uiState.productItems.map {
                if (it.product.id == targetCartItem.productId) {
                    it.copy(_quantity = targetCartItem.quantity)
                } else {
                    it
                }
            }

            _homeProductUiState.value =
                homeProductUiState.value?.copy(
                    productItems = productItems,
                    cartItems = cartItems,
                    totalCartQuantity = cartItems.sumOf { it.quantity },
                )
        }
    }

    override fun navigateToDetail(productId: Int) {
        _homeUiEvent.value = Event(HomeUiEvent.NavigateToDetail(productId))
    }


    override fun navigateToCart() {
        _homeUiEvent.value = Event(HomeUiEvent.NavigateToCart)
    }

    override fun loadMore() {
        loadProducts()
    }

    override fun addToCart(product: ProductItemDomain) {
        viewModelScope.launch {
            val result = cartRepository.addCartItem(product.id, 1).getOrNull()
            val cartData = cartRepository.getEntireCartItems().getOrNull()
            val changedItem = cartData?.firstOrNull { it.productId == product.id }?.increaseQuantity()
            val uiState = homeProductUiState.value

            if (result == null || uiState == null || cartData == null || changedItem == null) {
                notifyError()
                return@launch
            }

            val productItems =
                uiState.productItems.map {
                    if (it.product.id == changedItem.productId) {
                        it.copy(_quantity = 1)
                    } else {
                        it
                    }
                }

            val cartItems = uiState.cartItems.map {
                if (it.productId == changedItem.productId) {
                    it.copy(quantity = changedItem.quantity)
                } else {
                    it
                }
            }

            _homeProductUiState.value =
                homeProductUiState.value?.copy(
                    totalCartQuantity = uiState.totalCartQuantity + 1,
                    cartItems = cartItems,
                    productItems = productItems,
                )
        }
    }

    fun loadRecentItems() {
        viewModelScope.launch {
            val recentProductItems = recentProductRepository.findAll(RECENT_PRODUCTS_LIMIT)
            _recentProductUiState.value =
                recentProductUiState.value?.copy(
                    isLoading = false,
                    isEmpty = recentProductItems.isEmpty(),
                    productItems = recentProductItems,
                )
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val productData = productRepository.getProducts(
                page = homeProductUiState.value?.currentPageNumber ?: 0,
                size = PAGE_SIZE,
                category = CATEGORY_UNDEFINED,
                sort = DEFAULT_SORT_ORDER
            ).getOrNull()

            val totalCartQuantity = cartRepository.getCartTotalQuantity().getOrNull() ?: 0
            val uiState = homeProductUiState.value

            if (productData == null || uiState == null) {
                notifyError()
                return@launch
            }

            val productItems = productData.orderableProducts.map {
                HomeViewItem.ProductViewItem(it.productItemDomain, it.cartData?.quantity ?: 0)
            }
            val cartData = productData.orderableProducts.mapNotNull(OrderableProduct::cartData)

            _homeProductUiState.value =
                homeProductUiState.value?.copy(
                    isLoading = false,
                    totalCartQuantity = totalCartQuantity,
                    currentPageNumber = uiState.currentPageNumber.plus(1),
                    productItems = uiState.productItems.plus(productItems),
                    cartItems = uiState.cartItems.plus(cartData),
                    loadMoreAvailable = productData.last.not(),
                )
        }
    }

    private fun removeCartItem(updatedCartItem: CartData) {
        viewModelScope.launch {
            val result =
                cartRepository.deleteCartItem(cartItemId = updatedCartItem.cartItemId).getOrNull()
            val uiState = homeProductUiState.value

            if (result == null || uiState == null) {
                notifyError()
                return@launch
            }

            val leftItems =
                uiState.cartItems.filter {
                    it.productId != updatedCartItem.productId
                }

            val productItems =
                uiState.productItems.map {
                    if (it.product.id == updatedCartItem.productId) {
                        it.copy(_quantity = updatedCartItem.quantity)
                    } else {
                        it
                    }
                }

            _homeProductUiState.value =
                homeProductUiState.value?.copy(
                    productItems = productItems,
                    cartItems = leftItems,
                    totalCartQuantity = leftItems.sumOf { it.quantity },
                )
        }
    }

    private fun notifyError() {
        _homeUiEvent.value = Event(HomeUiEvent.Error)
    }

    companion object {
        private val CATEGORY_UNDEFINED: String? = null
        private const val DEFAULT_SORT_ORDER = "asc"
        private const val PAGE_SIZE = 20
        private const val RECENT_PRODUCTS_LIMIT = 10
    }
}
