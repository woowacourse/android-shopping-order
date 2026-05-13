package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.model.ProductId
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider

private const val PAGE_SIZE = 5

class CartViewModel(
    private val productRepository: ProductRepository = ShoppingRepositoryProvider.productRepository,
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState(cartListState = CartListUiState.Loading))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeNetworkState()
        loadPage(1)
    }

    fun loadPreviousPage() {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return
        if (!contentState.hasPrevious) return

        loadPage(contentState.currentPage - 1)
    }

    fun loadNextPage() {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return
        if (!contentState.hasNext) return

        loadPage(contentState.currentPage + 1)
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    cartListState = CartListUiState.Loading,
                )
            }
            runCatching {
                val totalCount = cartRepository.count()
                updatePage(page, totalCount)
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cartListState = CartListUiState.Error(throwable.message),
                    )
                }
            }
        }
    }

    private suspend fun updatePage(
        page: Int,
        totalCount: Int,
    ) {
        val totalPages = calculateTotalPages(totalCount)
        val currentPage = page.coerceIn(1, maxOf(totalPages, 1))
        val fromIndex = (currentPage - 1) * PAGE_SIZE

        val cartItems = cartRepository.getCartItems(fromIndex, PAGE_SIZE)
        val productMap = productRepository.findAllByIds(cartItems.map { it.productId }.toSet())

        val items =
            CartItemUiModelMapper.toUiModels(
                cartItems = cartItems,
                productsById = productMap,
            )

        _uiState.update { currentState ->
            currentState.copy(
                cartListState =
                    CartListUiState.Content(
                        items = items,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        hasPrevious = currentPage > 1,
                        hasNext = currentPage < totalPages,
                    ),
            )
        }
    }

    fun delete(productId: ProductId) {
        if (_uiState.value.cartListState is CartListUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                cartRepository.delete(productId)

                val remainingCount = cartRepository.count()
                val totalPages = calculateTotalPages(remainingCount)

                val currentContent = _uiState.value.cartListState as? CartListUiState.Content ?: return@runCatching

                val nextPage = currentContent.currentPage.coerceAtMost(maxOf(totalPages, 1))

                updatePage(nextPage, remainingCount)
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cartListState = CartListUiState.Error(throwable.message),
                    )
                }
            }
        }
    }

    fun increaseQuantity(productId: ProductId) {
        if (_uiState.value.cartListState is CartListUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                cartRepository.add(productId)
                updateCurrentPage()
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cartListState = CartListUiState.Error(throwable.message),
                    )
                }
            }
        }
    }

    fun decreaseQuantity(productId: ProductId) {
        if (_uiState.value.cartListState is CartListUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                cartRepository.delete(productId)
                updateCurrentPage()
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cartListState = CartListUiState.Error(throwable.message),
                    )
                }
            }
        }
    }

    private fun calculateTotalPages(totalCount: Int): Int {
        if (totalCount == 0) return 0
        return (totalCount - 1) / PAGE_SIZE + 1
    }

    private suspend fun updateCurrentPage() {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return

        val remainingCount = cartRepository.count()
        val totalPages = calculateTotalPages(remainingCount)
        val nextPage = contentState.currentPage.coerceAtMost(maxOf(totalPages, 1))

        updatePage(nextPage, remainingCount)
    }

    private fun observeNetworkState() {
        viewModelScope.launch {
            networkMonitor.isNetworkConnected.collect { isConnected ->
                _uiState.update { currentState ->
                    currentState.copy(isNetworkConnected = isConnected)
                }
            }
        }
    }
}
