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
        observeNetwork()
        observeCart()
        observeRecentItems()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkObserver.observeNetwork().collect { isAvailable ->
                _uiState.value =
                    _uiState.value.copy(isNetworkAvailable = isAvailable)

                if (isAvailable && _uiState.value.products.isEmpty()) {
                    loadMore()
                    loadRecentItems()
                }
            }
        }
    }

    fun loadProducts(
        page: Int = 0,
        size: Int = offset,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = _uiState.value.products.isEmpty(), cartErrorMessage = null) }
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
                        cartSize = cartItemQuantity,
                        cartErrorMessage = null,
                        canLoadMore = !apiResult.isLastPage,
                    )
                }
            }.onFailure { throwable ->
                if (throwable is IOException || throwable is HttpException) {
                    _uiState.update {
                        it.copy(cartErrorMessage = "카트 업데이트 오류")
                    }
                } else {
                    throw throwable
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun observeCart() {
        loadProducts((offset / pageSize), pageSize)
    }

    private fun observeRecentItems() {
        viewModelScope.launch {
            recentItemRepository.getRecentItems().collect { recentItems ->
                _uiState.value =
                    _uiState.value.copy(
                        recentItems = recentItems.map { it.toUiModel() }.toImmutableList(),
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
                    .map { it.toUiModel() }
                    .toImmutableList()

            _uiState.value = _uiState.value.copy(recentItems = recentItems)
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
            _uiState.update { it.copy(isPagingMore = true) }
            try {
                val apiResult = productRepository.getProducts(page = (offset / pageSize), size = pageSize)
                val loadProducts =
                    apiResult.products.map {
                        val quantity = cartRepository.getCartItemQuantity(it.id)
                        it.toUiModel(quantity = quantity)
                    }

                offset += loadProducts.size

                _uiState.value =
                    _uiState.value.copy(
                        products = (_uiState.value.products + loadProducts).toImmutableList(),
                        canLoadMore = !apiResult.isLastPage,
                    )
            } catch (_: IOException) {
                _uiState.update { state ->
                    state.copy(cartErrorMessage = "상품을 추가로 불러오는 데 실패했습니다.")
                }
            } catch (_: HttpException) {
                _uiState.update { state ->
                    state.copy(cartErrorMessage = "서버 통신 오류가 발생했습니다.")
                }
            } finally {
                _uiState.update { it.copy(isPagingMore = false) }
            }
        }
    }

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            runCatching {
                cartRepository.setCartItem(productId = productId, quantity = quantity)
            }.onFailure { throwable ->
                if (throwable is IOException || throwable is HttpException) {
                    _uiState.update {
                        it.copy(cartErrorMessage = "카트 아이템 오류입니다.")
                    }
                } else {
                    throw throwable
                }
            }
            loadProducts()
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
