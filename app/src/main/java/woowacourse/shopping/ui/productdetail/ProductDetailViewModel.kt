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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.ui.nav.ProductDetail
import java.io.IOException

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val recentProductRepo: RecentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    private val _event = MutableSharedFlow<ProductDetailEvent>()
    val event: SharedFlow<ProductDetailEvent> = _event.asSharedFlow()
    private val detailRoute = savedStateHandle.toRoute<ProductDetail>()
    private val productId: Long = detailRoute.productId
    private val isFromBanner: Boolean = detailRoute.isFromBanner

    init {
        loadProduct()
    }

    fun retry() {
        loadProduct()
    }

    fun increase() {
        _uiState.update {
            it.copy(selectedQuantity = it.selectedQuantity + 1)
        }
    }

    fun decrease() {
        _uiState.update {
            it.copy(selectedQuantity = maxOf(1, it.selectedQuantity - 1))
        }
    }

    fun addToCart() {
        val currentState = _uiState.value
        val productToSave = currentState.product ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                cartRepo.add(productToSave, quantity = currentState.selectedQuantity)
                _event.emit(ProductDetailEvent.AddToCartSuccess)
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(errorMessage = "장바구니에 상품을 담지 못했습니다.")
                }
            } catch (_: HttpException) {
                _uiState.update {
                    it.copy(errorMessage = "장바구니에 상품을 담지 못했습니다.")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val product = productRepo.findProduct(productId)
                if (product == null) {
                    _uiState.update {
                        it.copy(errorMessage = "상품 정보를 불러오지 못했습니다.")
                    }
                    return@launch
                }

                val bannerProduct =
                    recentProductRepo
                        .getLastViewedProduct()
                        ?.takeIf { !isFromBanner && it.id != productId }

                _uiState.update {
                    it.copy(
                        product = product,
                        selectedQuantity = 1,
                        lastViewedProduct = bannerProduct,
                        errorMessage = null,
                    )
                }
                recentProductRepo.add(productId)
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(errorMessage = "상품 정보를 불러오지 못했습니다.")
                }
            } catch (_: HttpException) {
                _uiState.update {
                    it.copy(errorMessage = "상품 정보를 불러오지 못했습니다.")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    companion object {
        fun provideFactory(
            container: AppContainer,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T =
                    ProductDetailViewModel(
                        savedStateHandle = extras.createSavedStateHandle(),
                        productRepo = container.productRepository,
                        cartRepo = container.cartRepository,
                        recentProductRepo = container.recentProductRepository,
                    ) as T
            }
    }
}

sealed interface ProductDetailEvent {
    data object AddToCartSuccess : ProductDetailEvent
}
