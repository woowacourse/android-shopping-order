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
import retrofit2.HttpException
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.remote.NetworkObserver
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
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

    private var offset = 0
    private val pageSize = 20

    init {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(uiInfo = it.uiInfo.copy(isLoading = true)) }
                observeNetwork()
                observeCart()
                observeRecentItems()
            } finally {
                _uiState.update { it.copy(uiInfo = it.uiInfo.copy(isLoading = false)) }
            }
        }

    }

    private suspend fun observeNetwork() {
        networkObserver.observeNetwork().collect { isAvailable ->
            _uiState.update {
                it.copy(uiInfo = it.uiInfo.copy(isNetworkAvailable = isAvailable))
            }

            if (isAvailable && _uiState.value.products.isEmpty()) {
                loadMore()
                loadRecentItems()
            }
        }
    }

    fun loadProducts(
        page: Int = 0,
        size: Int = offset,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(uiInfo = it.uiInfo.copy(cartErrorMessage = null)) }
            runCatching {
                val apiResult =
                    productRepository.getProducts(page = page, size = size)
                val cartItemQuantity = cartRepository.getTotalCartItemQuantity()

                offset += apiResult.products.size
                _uiState.update {
                    it.copy(
                        products =
                            apiResult.products
                                .map { product ->
                                    val quantity =
                                        cartRepository.getCartItemQuantity(productId = product.id)
                                    product.toUiModel(quantity = quantity)
                                }.toImmutableList(),
                        cartSummary =
                            it.cartSummary.copy(
                                cartSize = cartItemQuantity,
                                canLoadMore = !apiResult.isLastPage,
                            ),
                        uiInfo = it.uiInfo.copy(cartErrorMessage = null),
                    )
                }
            }.onFailure { throwable ->
                if (throwable is IOException || throwable is HttpException) {
                    _uiState.update {
                        it.copy(uiInfo = it.uiInfo.copy(cartErrorMessage = "카트 업데이트 오류"))
                    }
                } else {
                    throw throwable
                }
            }
        }
    }

    private fun observeCart() {
        loadProducts((offset / pageSize), pageSize)
    }

    private suspend fun observeRecentItems() {
        recentItemRepository.getRecentItems().collect { recentItems ->
            _uiState.update {
                it.copy(
                    recentItems =
                        recentItems
                            .map { productId ->
                                productRepository.getProductById(productId).toUiModel()
                            }.toImmutableList(),
                )
            }
        }
    }

    fun loadRecentItems() {
        viewModelScope.launch {
            val recentItems =
                recentItemRepository
                    .getRecentItems()
                    .first()
                    .map { productRepository.getProductById(it).toUiModel() }
                    .toImmutableList()

            _uiState.update { it.copy(recentItems = recentItems) }
        }
    }

    fun loadMore() {
        if (!_uiState.value.uiInfo.isNetworkAvailable || !_uiState.value.cartSummary.canLoadMore || _uiState.value.uiInfo.isLoading) return

        viewModelScope.launch {
            try {
                val apiResult = productRepository.getProducts(page = (offset / pageSize), size = pageSize)
                val loadProducts =
                    apiResult.products.map {
                        val quantity = cartRepository.getCartItemQuantity(it.id)
                        it.toUiModel(quantity = quantity)
                    }

                offset += loadProducts.size

                _uiState.update {
                    it.copy(
                        products = (it.products + loadProducts).toImmutableList(),
                        cartSummary = it.cartSummary.copy(canLoadMore = !apiResult.isLastPage),
                    )
                }
            } catch (_: IOException) {
                _uiState.update { it }
            } finally {
                loadProducts()
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
                    _uiState.update {
                        it.copy(uiInfo = it.uiInfo.copy(cartErrorMessage = "카트 아이템 오류입니다."))
                    }
                } else {
                    throw throwable
                }
            }
            loadProducts()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    ShoppingViewModel(
                        productRepository = app.appContainer.productRepository,
                        cartRepository = app.appContainer.cartRepository,
                        recentItemRepository = app.appContainer.recentItemRepository,
                        networkObserver = app.appContainer.networkObserver,
                    )
                }
            }
    }
}
