package woowacourse.shopping.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.ui.model.mapper.toUiModel
import kotlin.coroutines.cancellation.CancellationException

class DetailViewModel(
    private val id: String,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<DetailUiEvent>()
    val uiEvent: SharedFlow<DetailUiEvent> = _uiEvent.asSharedFlow()

    private val hideRecentItem: Boolean = false

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            try {
                val product = productRepository.getProductById(id)
                val lastViewId = recentItemRepository.getLastViewedItem()
                val lastViewedItem =
                    if (hideRecentItem || lastViewId == null) {
                        null
                    } else {
                        productRepository.getProductById(lastViewId)
                    }
                val quantity = cartRepository.getCartItemQuantity(id) ?: 1

                recentItemRepository.addRecentItem(product)

                _uiState.value =
                    _uiState.value.copy(
                        product = product.toUiModel(),
                        quantity = quantity,
                        recentItem = lastViewedItem?.toUiModel(),
                        totalPrice = product.getPrice() * quantity,
                    )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiEvent.emit(DetailUiEvent.ShowToastMessage(e.message ?: "알 수 없는 오류가 발생했습니다."))
                _uiEvent.emit(DetailUiEvent.Dismiss)
            }
        }
    }

    fun updateQuantity(quantity: Int) {
        val nextQuantity = quantity.coerceAtLeast(1)

        _uiState.value =
            _uiState.value.copy(
                quantity = nextQuantity,
                totalPrice = _uiState.value.product.price * nextQuantity,
            )
    }

    fun addToCart() {
        viewModelScope.launch {
            try {
                val product = productRepository.getProductById(id)
                cartRepository.setCartItem(product.id, _uiState.value.quantity)

                _uiEvent.emit(DetailUiEvent.NavToCart)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiEvent.emit(DetailUiEvent.ShowToastMessage(e.message ?: "알 수 없는 오류가 발생했습니다."))
                _uiEvent.emit(DetailUiEvent.Dismiss)
            }
        }
    }

    fun onDismiss() {
        viewModelScope.launch {
            _uiEvent.emit(DetailUiEvent.Dismiss)
        }
    }

    fun navToDetail(productId: String) {
        viewModelScope.launch {
            _uiEvent.emit(DetailUiEvent.NavToDetail(productId = productId))
        }
    }

    companion object {
        fun Factory(productId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication

                    DetailViewModel(
                        id = productId,
                        cartRepository = app.appContainer.cartRepository,
                        productRepository = app.appContainer.productRepository,
                        recentItemRepository = app.appContainer.recentItemRepository,
                    )
                }
            }
    }
}
