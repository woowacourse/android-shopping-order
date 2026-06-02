package woowacourse.shopping.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.domain.AddToCartUseCase
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.productdetail.model.DetailUiState
import woowacourse.shopping.route.ProductDetail

sealed interface ProductDetailEvent {
    data class AddToCart(
        val message: String,
    ) : ProductDetailEvent

    object NavigateToBack : ProductDetailEvent
}

class ProductDetailViewModel(
    private val productId: Long,
    private val productRepository: ProductRepository = AppContainer.productRepository,
    private val cartRepository: CartRepository = AppContainer.cartRepository,
    private val addToCartUseCase: AddToCartUseCase = AppContainer.addToCartUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ProductDetailEvent>()
    val event: SharedFlow<ProductDetailEvent> = _event.asSharedFlow()

    init {
        loadProduct()
    }

    fun increaseQuantity() {
        _uiState.update { state ->
            if (state is DetailUiState.Success) {
                state.copy(quantity = state.quantity + 1)
            } else {
                state
            }
        }
    }

    fun decreaseQuantity() {
        _uiState.update { state ->
            if (state is DetailUiState.Success && state.quantity > 1) {
                state.copy(quantity = state.quantity - 1)
            } else {
                state
            }
        }
    }

    fun addItemToCart(quantity: Int = 1) {
        viewModelScope.launch {
            addToCartUseCase(productId, quantity)
            _event.emit(ProductDetailEvent.AddToCart("상품을 추가했습니다"))
            _event.emit(ProductDetailEvent.NavigateToBack)
        }
    }

    private fun loadProduct() {
        viewModelScope.launch {
            try {
                val loadedProduct =
                    productRepository.getProductById(productId)
                        ?: throw NoSuchElementException()

                val recentProduct =
                    productRepository
                        .getRecentProductsStream(size = 1)
                        .firstOrNull()
                        ?.firstOrNull()
                        ?.toUiModel()

                _uiState.value =
                    DetailUiState.Success(
                        product = loadedProduct.toUiModel(),
                        quantity = 1,
                        recentProduct = recentProduct,
                    )

                productRepository.upsertRecentProduct(productId)
            } catch (_: Exception) {
                _uiState.value = DetailUiState.Error("상품 로딩에 실패했습니다.")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    ProductDetailViewModel(
                        productId = savedStateHandle.toRoute<ProductDetail>().productId,
                    )
                }
            }
    }
}
