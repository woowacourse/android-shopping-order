package woowacourse.shopping.ui.productDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.util.LoadState

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val productId: Int = savedStateHandle[EXTRA_PRODUCT_ID] ?: -1
    private val openedFromLastViewed: Boolean =
        savedStateHandle[EXTRA_OPENED_FROM_LAST_VIEWED] ?: false
    private val _uiState = MutableStateFlow(ProductDetailUiState(loadState = LoadState.Initial))
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        init()
    }

    private fun init() {
        if (productId == -1) {
            _uiState.update {
                it.copy(
                    loadState =
                        LoadState.Error(
                            throwable =
                                IllegalArgumentException(
                                    "productId가 없습니다.",
                                ),
                            message = "존재하지 않는 상품입니다.",
                        ),
                )
            }
            return
        }
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadState = LoadState.Loading) }

            runCatching { productRepository.getProduct(productId) }
                .onSuccess { product ->
                    if (product != null) {
                        val mostRecentProduct: Product? =
                            recentProductRepository.getMostRecentProduct()
                        val lastViewedProduct =
                            if (openedFromLastViewed || product.isSameProduct(mostRecentProduct)) {
                                null
                            } else {
                                mostRecentProduct
                            }
                        recentProductRepository.save(product)
                        _uiState.update {
                            it.copy(
                                product = product,
                                lastViewedProduct = lastViewedProduct,
                                loadState = LoadState.Success,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                loadState =
                                    LoadState.Error(
                                        throwable = NoSuchElementException("id=$productId 상품을 찾을 수 없습니다."),
                                        message = "상품을 찾을 수 없습니다.",
                                    ),
                            )
                        }
                    }
                }.onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            loadState =
                                LoadState.Error(
                                    throwable = throwable,
                                    message = throwable.message ?: "상품을 불러오는데 실패했습니다.",
                                ),
                        )
                    }
                }
        }
    }

    fun increaseQuantity() {
        _uiState.update {
            it.copy(
                currentQuantity = it.currentQuantity + 1,
            )
        }
    }

    fun decreaseQuantity() {
        if (_uiState.value.currentQuantity <= 1) return
        _uiState.update {
            it.copy(
                currentQuantity = it.currentQuantity - 1,
            )
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            val target = _uiState.value.product ?: return@launch
            cartRepository.addProduct(target.id, uiState.value.currentQuantity)
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "product_id"
        const val EXTRA_OPENED_FROM_LAST_VIEWED = "opened_from_last_viewed"

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    val application =
                        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                    ProductDetailViewModel(
                        savedStateHandle = savedStateHandle,
                        productRepository = application.appContainer.productRepository,
                        cartRepository = application.appContainer.cartRepository,
                        recentProductRepository = application.appContainer.recentProductRepository,
                    )
                }
            }
    }
}
