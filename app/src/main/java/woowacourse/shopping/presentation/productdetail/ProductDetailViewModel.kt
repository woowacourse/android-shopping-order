package woowacourse.shopping.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.addToCartUseCase
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.productdetail.model.DetailUiState

sealed interface ProductDetailEvent {
    data class AddToCart(
        val message: String,
    ) : ProductDetailEvent

    object NavigateToBack : ProductDetailEvent
}

class ProductDetailViewModel(
    private val productRepository: ProductRepository = RepositoryProvider.productRepository,
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ProductDetailEvent>()
    val event: SharedFlow<ProductDetailEvent> = _event.asSharedFlow()

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

    fun addItemToCart(
        id: Long,
        quantity: Int = 1,
    ) {
        viewModelScope.launch {
            addToCartUseCase(cartRepository, id, quantity)
            _event.emit(ProductDetailEvent.AddToCart("상품을 추가했습니다"))
            _event.emit(ProductDetailEvent.NavigateToBack)
        }
    }

    fun loadProduct(id: Long) {
        if (_uiState.value !is DetailUiState.Loading) return

        viewModelScope.launch {
            try {
                val loadedProduct =
                    productRepository.getProductById(id)
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

                productRepository.upsertRecentProduct(id)
            } catch (_: Exception) {
                _uiState.value = DetailUiState.Error("상품 로딩에 실패했습니다.")
            }
        }
    }
}
