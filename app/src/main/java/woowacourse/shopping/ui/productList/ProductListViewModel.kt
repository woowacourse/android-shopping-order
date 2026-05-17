package woowacourse.shopping.ui.productList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.cart.Cart
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val pagingState = MutableStateFlow(PagingState())
    private val recentProductsFlow = MutableStateFlow<List<Product>>(emptyList())
    private val cartFlow = MutableStateFlow(Cart())

    val uiState: StateFlow<ProductListUiState> =
        combine(pagingState, cartFlow, recentProductsFlow) { paging, cart, recents ->
            paging.toUiState(cart, recents)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductListUiState.Loading)

    init {
        init()
    }

    fun init() {
        observeRecentProducts()
        loadNextPage()
    }

    fun moreProducts() {
        loadNextPage()
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            cartRepository.addProduct(product)
            refreshCart()
        }
    }

    fun increase(productId: Int) {
        viewModelScope.launch {
            val target =
                cartFlow.value.cartItems.values
                    .find { it.product.id == productId } ?: return@launch
            cartRepository.increase(target.id, target.quantity.value + 1)
            refreshCart()
        }
    }

    fun decrease(productId: Int) {
        viewModelScope.launch {
            val target =
                cartFlow.value.cartItems.values
                    .find { it.product.id == productId } ?: return@launch
            cartRepository.decrease(target.id, target.quantity.value - 1)
            refreshCart()
        }
    }

    private fun observeRecentProducts() {
        recentProductRepository
            .getRecentProducts()
            .onEach { recentProductsFlow.value = it }
            .launchIn(viewModelScope)
    }

    private suspend fun refreshCart() {
        val cartItems = cartRepository.getAllCartItems()
        cartFlow.value = Cart(cartItems)
    }

    private fun loadNextPage() {
        val current = pagingState.value
        if (current.isLoading || !current.canLoadMore) return

        viewModelScope.launch {
            pagingState.update { it.copy(isLoading = true) }
            runCatching { productRepository.getProducts(current.currentPage, PAGE_SIZE) }
                .onSuccess { newProducts ->
                    pagingState.update {
                        it.copy(
                            products = it.products + newProducts.items,
                            currentPage = it.currentPage + 1,
                            canLoadMore = !newProducts.isLast,
                            isLoading = false,
                            loadError = null,
                        )
                    }
                    refreshCart()
                }.onFailure { throwable ->
                    pagingState.update {
                        it.copy(isLoading = false, loadError = throwable)
                    }
                }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20

        fun factory(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
            recentProductRepository: RecentProductRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    ProductListViewModel(productRepository, cartRepository, recentProductRepository)
                }
            }
    }
}
