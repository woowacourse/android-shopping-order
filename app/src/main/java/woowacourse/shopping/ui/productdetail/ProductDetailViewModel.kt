package woowacourse.shopping.ui.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.repository.http.common.RemoteException
import woowacourse.shopping.ui.common.recentlyviewed.RecentViewedProductsMapper
import woowacourse.shopping.ui.navigation.ProductDetail

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val route: ProductDetail = savedStateHandle.toRoute()
    private val productId = route.productId

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    init {
        observeNetworkState()
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            productRepository
                .findAllByIds(setOf(productId))
                .onSuccess { productsById ->
                    val foundProduct = productsById[productId]

                    if (foundProduct != null) {
                        recentProductRepository.recordView(foundProduct.id)
                        refreshProductDetail(foundProduct.id)
                    } else {
                        updateErrorState(Exception("상품을 찾을 수 없습니다."))
                    }
                }.onFailure { throwable ->
                    updateErrorState(throwable)
                }
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            val isSuccess = changeQuantity(delta = 1)
            val message =
                if (isSuccess) {
                    "장바구니에 상품을 담았습니다."
                } else {
                    "장바구니 담기에 실패했습니다."
                }
            _snackbarEvent.emit(message)
        }
    }

    fun increaseQuantity() {
        viewModelScope.launch {
            changeQuantity(delta = 1)
        }
    }

    fun decreaseQuantity() {
        viewModelScope.launch {
            changeQuantity(delta = -1)
        }
    }

    private suspend fun changeQuantity(delta: Int): Boolean {
        val product = _uiState.value.product ?: return false
        val nextQuantity = (_uiState.value.quantity + delta).coerceAtLeast(0)
        if (nextQuantity == _uiState.value.quantity) return false

        _uiState.update { current ->
            current.copy(
                quantity = nextQuantity,
                isAdding = true,
                errorMessage = null,
            )
        }

        return cartRepository
            .setQuantity(product.id, nextQuantity)
            .fold(
                onSuccess = {
                    _uiState.update { current ->
                        current.copy(isAdding = false)
                    }
                    true
                },
                onFailure = { throwable ->
                    refreshProductDetail(product.id)
                    updateErrorState(throwable)
                    false
                },
            )
    }

    private suspend fun refreshProductDetail(productId: Long) {
        val productsById =
            productRepository
                .findAllByIds(setOf(productId))
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

        val product = productsById[productId] ?: return

        val lastViewedRecentProduct =
            recentProductRepository.getLatestViewedProductExcluding(productId)

        val lastViewedProductsById =
            productRepository
                .findAllByIds(
                    listOfNotNull(lastViewedRecentProduct?.productId).toSet(),
                ).getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

        val lastViewedProduct =
            RecentViewedProductsMapper.toProduct(
                recentProduct = lastViewedRecentProduct,
                productsById = lastViewedProductsById,
            )

        val quantity =
            cartRepository
                .getCartItemsByProductIds(setOf(productId))
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }.firstOrNull()
                ?.quantity ?: 0

        _uiState.update { current ->
            current.copy(
                product = product,
                lastViewedProduct = lastViewedProduct,
                quantity = quantity,
                isAdding = false,
                errorMessage = null,
            )
        }
    }

    private fun updateErrorState(throwable: Throwable) {
        _uiState.update { current ->
            current.copy(
                isAdding = false,
                errorMessage = throwable.toUserMessage(),
            )
        }
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

class ProductDetailViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        val savedStateHandle = extras.createSavedStateHandle()

        return ProductDetailViewModel(
            savedStateHandle = savedStateHandle,
            productRepository = ShoppingRepositoryProvider.productRepository,
            cartRepository = ShoppingRepositoryProvider.cartRepository,
            recentProductRepository = ShoppingRepositoryProvider.recentProductRepository,
            networkMonitor = ShoppingRepositoryProvider.networkMonitor,
        ) as T
    }
}

private fun Throwable.toUserMessage(): String =
    when (this) {
        is RemoteException -> userMessage
        else -> "알 수 없는 오류가 발생했습니다."
    }
