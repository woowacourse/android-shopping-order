package woowacourse.shopping.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.recentitem.RecentItemRepository
import woowacourse.shopping.ui.model.mapper.toUiModel
import java.io.IOException

class DetailViewModel(
    private val id: String,
    private val hideRecentItem: Boolean,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            try {
                val product = productRepository.getProductById(id)
                cartRepository.refreshCartItems()
                val lastViewedItem =
                    if (hideRecentItem) {
                        null
                    } else {
                        recentItemRepository.getLastViewedItem()
                    }
                val quantity = cartRepository.getCartItemQuantity(id) ?: 1

                recentItemRepository.addRecentItem(product)

                _uiState.value =
                    _uiState.value.copy(
                        product = product.toUiModel(),
                        quantity = quantity,
                        recentItem = lastViewedItem?.takeIf { it.id != id }?.toUiModel(),
                        totalPrice = product.getPrice() * quantity,
                    )
            } catch (_: IllegalArgumentException) {
                _event.emit(DetailEvent.ShowProductNotFoundMessage)
                _event.emit(DetailEvent.NavigateBack)
            } catch (_: IOException) {
                _event.emit(DetailEvent.ShowProductLoadFailureMessage)
                _event.emit(DetailEvent.NavigateBack)
            } catch (_: HttpException) {
                _event.emit(DetailEvent.ShowProductLoadFailureMessage)
                _event.emit(DetailEvent.NavigateBack)
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
                _event.emit(DetailEvent.NavigateToCart)
            } catch (_: IllegalArgumentException) {
                _event.emit(DetailEvent.ShowAddCartFailureMessage)
            } catch (_: IOException) {
                _event.emit(DetailEvent.ShowAddCartFailureMessage)
            } catch (_: HttpException) {
                _event.emit(DetailEvent.ShowAddCartFailureMessage)
            }
        }
    }

    companion object {
        fun provideFactory(
            id: String,
            hideRecentItem: Boolean,
            productRepository: ProductRepository,
            cartRepository: CartRepository,
            recentItemRepository: RecentItemRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    DetailViewModel(
                        id = id,
                        hideRecentItem = hideRecentItem,
                        productRepository = productRepository,
                        cartRepository = cartRepository,
                        recentItemRepository = recentItemRepository,
                    )
                }
            }
    }
}
