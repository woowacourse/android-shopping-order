package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.util.LoadState
import woowacourse.shopping.ui.util.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val cartItemsFlow: StateFlow<CartItems> = cartRepository.cartItems
    private val selectedItemsFlow = MutableStateFlow<Set<Int>>(emptySet())
    private val recommendProductsFlow = MutableStateFlow<List<Product>>(emptyList())
    private val currentPageIdxFlow = MutableStateFlow(0)
    private val cartLoadStateFlow = MutableStateFlow<LoadState>(LoadState.Initial)
    private val _currentScreen = MutableStateFlow(CartFlow.CART)
    val currentScreen: StateFlow<CartFlow> = _currentScreen.asStateFlow()

    val cartUiState: StateFlow<CartUiState> =
        combine(
            cartItemsFlow,
            selectedItemsFlow,
            currentPageIdxFlow,
            recommendProductsFlow,
            cartLoadStateFlow,
        ) { cartItems, selectedItems, currentPageIdx, recommendProducts, loadState ->
            val curCartItems =
                cartItems.subList(currentPageIdx * PAGE_SIZE, (currentPageIdx + 1) * PAGE_SIZE)
            CartUiState(
                cartItems = curCartItems.values.toUiModel(selectedItems),
                currentPage = currentPageIdx + 1,
                hasPreviousPage = !curCartItems.isFirst,
                hasNextPage = !curCartItems.isLast,
                isAllSelected = selectedItems.containsAll(cartItems.values.map { it.id }),
                totalPrice = cartItems.calculatePrice(selectedItems),
                totalCount = cartItems.calculateQuantity(selectedItems),
                showPageNavigator = cartItems.size() > PAGE_SIZE,
                recommendProducts = recommendProducts.toUiModel(cartItems),
                loadState = loadState,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState(),
        )

    init {
        initCartItems()
    }

    private fun initCartItems() {
        viewModelScope.launch {
            cartLoadStateFlow.update { LoadState.Loading }
            cartRepository.refreshCartItems()
            loadRecommendProduct()
            cartLoadStateFlow.update { LoadState.Success }
        }
    }

    private suspend fun loadRecommendProduct() {
        val recommended = recentProductRepository.getMostRecentProduct() ?: return
        val productList = productRepository.getProducts(0, Int.MAX_VALUE)
        val categoryProducts = productList.getCategoryProducts(recommended.category.value)

        val result =
            categoryProducts - (cartItemsFlow.value.values.map { it.product }).toSet()
        recommendProductsFlow.update { result.take(MAX_RECOMMEND_SIZE) }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            cartRepository.removeCartItem(cartId)

            val totalPage = (cartItemsFlow.value.size() + PAGE_SIZE - 1) / PAGE_SIZE
            if (currentPageIdxFlow.value >= totalPage) {
                currentPageIdxFlow.update { maxOf(0, totalPage - 1) }
            }
            selectedItemsFlow.update { it - cartId }
        }
    }

    fun addCartItem(productId: Int) {
        viewModelScope.launch {
            cartRepository.addProduct(productId, 1)
            val cartItem = cartItemsFlow.value.findByProductId(productId) ?: return@launch

            selectedItemsFlow.update {
                it + cartItem.id
            }
        }
    }

    fun increaseCartItemQuantity(cartId: Int) {
        viewModelScope.launch {
            val targetQuantity = cartItemsFlow.value.getQuantityByCartId(cartId)

            cartRepository.updateQuantity(cartId, targetQuantity.value + 1)
        }
    }

    fun decreaseCartItemQuantity(cartId: Int) {
        viewModelScope.launch {
            val targetQuantity = cartItemsFlow.value.getQuantityByCartId(cartId)

            if (targetQuantity.value == 1) {
                cartRepository.removeCartItem(cartId)
                selectedItemsFlow.update { it - cartId }
            } else {
                cartRepository.updateQuantity(cartId, targetQuantity.value - 1)
            }
        }
    }

    fun toggleSelection(id: Int) {
        selectedItemsFlow.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun toggleAllSelection() {
        val current = selectedItemsFlow.value
        if (current.size == cartItemsFlow.value.values.size) {
            selectedItemsFlow.update { emptySet() }
        } else {
            selectedItemsFlow.update {
                cartItemsFlow.value.values
                    .map { it.id }
                    .toSet()
            }
        }
    }

    fun goToNextPage() {
        if (!cartUiState.value.hasNextPage) return
        currentPageIdxFlow.update { it + 1 }
    }

    fun goToPreviousPage() {
        if (!cartUiState.value.hasPreviousPage) return
        currentPageIdxFlow.update { it - 1 }
    }

    fun increaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            val target =
                cartItemsFlow.value.findByProductId(productId) ?: return@launch

            cartRepository.updateQuantity(target.id, target.quantity.value + 1)
        }
    }

    fun decreaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            val target =
                cartItemsFlow.value.findByProductId(productId) ?: return@launch

            if (target.quantity.value == 1) {
                cartRepository.removeCartItem(target.id)
                selectedItemsFlow.update { it - target.id }
            } else {
                cartRepository.updateQuantity(target.id, target.quantity.value - 1)
            }
        }
    }

    fun onClickOrder() {
        if (_currentScreen.value == CartFlow.CART) {
            _currentScreen.value = CartFlow.RECOMMEND
        } else {
            viewModelScope.launch {
                cartRepository.order(selectedItemsFlow.value.toList())
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val MAX_RECOMMEND_SIZE = 10

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                    CartViewModel(
                        application.appContainer.cartRepository,
                        application.appContainer.recentProductRepository,
                        application.appContainer.productRepository,
                    )
                }
            }
    }
}
