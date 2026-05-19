package woowacourse.shopping.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import retrofit2.HttpException
import woowacourse.shopping.data.remote.NetworkObserver
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.model.mapper.toUiModel
import java.io.IOException

class ShoppingViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
    private val networkObserver: NetworkObserver,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    private var products: List<Product> = emptyList()
    private var cartItems: List<CartItem> = emptyList()
    private var offset = 0
    private val pageSize = 20

    init {
        observeCartItems()
        observeNetwork()
        observeRecentItems()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            cartRepository.cartItems.collect { items ->
                cartItems = items
                renderProducts()
            }
        }
    }

    private fun renderProducts() {
        val quantityByProductId = cartItems.associate { it.product.id to it.quantity }

        _uiState.update {
            it.copy(
                products =
                    products
                        .map { product ->
                            product.toUiModel(quantity = quantityByProductId[product.id])
                        }.toImmutableList(),
                cartSize = cartItems.sumOf { cartItem -> cartItem.quantity },
            )
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkObserver.observeNetwork().collect { isAvailable ->
                _uiState.update { it.copy(isNetworkAvailable = isAvailable) }

                if (isAvailable && products.isEmpty()) {
                    refreshCartItems()
                    loadMore()
                    loadRecentItems()
                }
            }
        }
    }

    private suspend fun refreshCartItems() {
        runCatching {
            cartRepository.refreshCartItems()
        }.onFailure { throwable ->
            if (throwable is IOException || throwable is HttpException) {
                _uiState.update { it.copy(cartErrorMessage = "Failed to update cart.") }
            } else {
                throw throwable
            }
        }
    }

    fun loadProducts(
        page: Int = 0,
        size: Int = offset,
        shouldRefreshCart: Boolean = true,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(cartErrorMessage = null, isLoading = true) }
            runCatching {
                if (shouldRefreshCart) {
                    refreshCartItems()
                }

                val apiResult = productRepository.getProducts(page = page, size = size)

                products = apiResult.products
                offset = apiResult.products.size
                _uiState.update {
                    it.copy(
                        cartErrorMessage = null,
                        canLoadMore = !apiResult.isLastPage,
                    )
                }
                renderProducts()
            }.onFailure { throwable ->
                if (throwable is IOException || throwable is HttpException) {
                    _uiState.update { it.copy(cartErrorMessage = "Failed to load products.") }
                } else {
                    throw throwable
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun observeRecentItems() {
        viewModelScope.launch {
            recentItemRepository.getRecentItems().collect { recentItems ->
                _uiState.update {
                    it.copy(
                        recentItems = recentItems.map { product -> product.toUiModel() }.toImmutableList(),
                    )
                }
            }
        }
    }

    fun loadRecentItems() {
        viewModelScope.launch {
            val recentItems =
                recentItemRepository
                    .getRecentItems()
                    .first()
                    .map { it.toUiModel() }
                    .toImmutableList()

            _uiState.update { it.copy(recentItems = recentItems) }
        }
    }

    fun loadMore() {
        if (!_uiState.value.isNetworkAvailable || !_uiState.value.canLoadMore || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val apiResult = productRepository.getProducts(page = (offset / pageSize), size = pageSize)

                products += apiResult.products
                offset += apiResult.products.size

                _uiState.update {
                    it.copy(
                        canLoadMore = !apiResult.isLastPage,
                    )
                }
                renderProducts()
            } catch (_: IOException) {
                _uiState.update { it.copy(cartErrorMessage = "Failed to load products.") }
            } catch (_: HttpException) {
                _uiState.update { it.copy(cartErrorMessage = "Failed to load products.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateQuantity(
        productId: String,
        quantity: Int,
    ) {
        viewModelScope.launch {
            runCatching {
                cartRepository.setCartItem(productId = productId, quantity = quantity)
            }.onFailure { throwable ->
                if (throwable is IOException || throwable is HttpException) {
                    _uiState.update { it.copy(cartErrorMessage = "Failed to update cart item.") }
                } else {
                    throw throwable
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
            recentItemRepository: RecentItemRepository,
            networkObserver: NetworkObserver,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    ShoppingViewModel(
                        productRepository = productRepository,
                        cartRepository = cartRepository,
                        recentItemRepository = recentItemRepository,
                        networkObserver = networkObserver,
                    )
                }
            }
    }
}
