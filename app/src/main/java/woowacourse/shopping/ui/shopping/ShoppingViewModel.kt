package woowacourse.shopping.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.remote.NetworkObserver
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.ui.model.mapper.toUiModel

class ShoppingViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
    private val networkObserver: NetworkObserver,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    private var offset = 0
    private val pageSize = 20

    init {
        observeNetwork()
        observeCartQuantities()
        observeRecentItems()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkObserver.observeNetwork().collect { isAvailable ->
                _uiState.value =
                    _uiState.value.copy(isNetworkAvailable = isAvailable)

                if (isAvailable && _uiState.value.products.isEmpty()) {
                    loadMore()
                }
            }
        }
    }

    private fun observeCartQuantities() {
        viewModelScope.launch {
            cartRepository.getCartQuantityMap().collect { quantityMap ->
                _uiState.update { state ->
                    state.copy(
                        products =
                            state.products
                                .map { product ->
                                    product.copy(quantity = quantityMap[product.id] ?: 0)
                                }.toImmutableList(),
                        cartSize = quantityMap.values.sum(),
                    )
                }
            }
        }
    }

    private fun observeRecentItems() {
        viewModelScope.launch {
            recentItemRepository.getRecentItems().collect { products ->
                if (products.isEmpty()) return@collect

                _uiState.update { state ->
                    state.copy(
                        recentItems = products.map { it.toUiModel() }.toImmutableList(),
                    )
                }
            }
        }
    }

    fun loadMore() {
        if (!_uiState.value.isNetworkAvailable ||
            !_uiState.value.canLoadMore ||
            _uiState.value.isLoading ||
            _uiState.value.isPagingMore
        ) {
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPagingMore = true,
                    cartErrorMessage = null,
                )
            }
            productRepository
                .getProducts(page = offset / pageSize, size = pageSize)
                .onSuccess { result ->
                    val quantityMap = cartRepository.getCartQuantityMap().first()

                    val loadProducts =
                        result.products.map { product ->
                            product.toUiModel(quantity = quantityMap[product.id] ?: 0)
                        }

                    offset += loadProducts.size

                    _uiState.update { state ->
                        state.copy(
                            products = (state.products + loadProducts).toImmutableList(),
                            cartSize = quantityMap.values.sum(),
                            canLoadMore = result.isLastPage.not(),
                            cartErrorMessage = null,
                        )
                    }
                }.onFailure {
                    _uiState.update { state ->
                        state.copy(cartErrorMessage = "상품을 불러오지 못했습니다.")
                    }
                }
            _uiState.update { it.copy(isPagingMore = false) }
        }
    }

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartRepository
                .setCartItem(productId = productId, quantity = quantity)
                .onSuccess {
                    _uiState.update { it.copy(cartErrorMessage = null) }
                }.onFailure {
                    _uiState.update {
                        it.copy(cartErrorMessage = "카트 아이템 오류입니다.")
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val appContainer = (this[APPLICATION_KEY] as ShoppingApplication).appContainer

                    ShoppingViewModel(
                        productRepository = appContainer.productRepository,
                        cartRepository = appContainer.cartRepository,
                        recentItemRepository = appContainer.recentItemRepository,
                        networkObserver = appContainer.networkObserver,
                    )
                }
            }
    }
}
