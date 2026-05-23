package woowacourse.shopping.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.ui.model.mapper.toUiModel
import java.io.IOException

class DetailViewModel(
    private val id: String,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<DetailEvent>()
    val event = _event.receiveAsFlow()

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
            } catch (_: IllegalArgumentException) {
                _event.send(DetailEvent.ShowProductNotFoundMessage)
                _event.send(DetailEvent.NavigateBack)
            } catch (_: IOException) {
                _event.send(DetailEvent.ShowProductLoadFailureMessage)
                _event.send(DetailEvent.NavigateBack)
            } catch (_: HttpException) {
                _event.send(DetailEvent.ShowProductLoadFailureMessage)
                _event.send(DetailEvent.NavigateBack)
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
                _event.send(DetailEvent.NavigateToCart)
            } catch (_: IllegalArgumentException) {
                _event.send(DetailEvent.ShowAddCartFailureMessage)
            } catch (_: IOException) {
                _event.send(DetailEvent.ShowAddCartFailureMessage)
            } catch (_: HttpException) {
                _event.send(DetailEvent.ShowAddCartFailureMessage)
            }
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
