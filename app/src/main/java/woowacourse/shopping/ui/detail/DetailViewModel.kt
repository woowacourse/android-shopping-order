package woowacourse.shopping.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.ui.model.mapper.toUiModel
import kotlin.coroutines.cancellation.CancellationException

class DetailViewModel(
    private val id: Long,
    private val hideRecentItem: Boolean,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<DetailEvent>()
    val event = _event.receiveAsFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            try {
                val product = productRepository.getProductById(id)

                val quantity = cartRepository.getCartItemQuantity(id) ?: 1

                val lastViewedItem =
                    if (hideRecentItem) {
                        null
                    } else {
                        val lastViewedItemId = recentItemRepository.getLastViewedItemId()
                        if (lastViewedItemId != null && lastViewedItemId != id) {
                            runCatching { productRepository.getProductById(lastViewedItemId) }.getOrNull()
                        } else {
                            null
                        }
                    }

                recentItemRepository.addRecentItemId(id)

                _uiState.value =
                    _uiState.value.copy(
                        product = product.toUiModel(),
                        quantity = quantity,
                        recentItem = lastViewedItem?.takeIf { it.id != id }?.toUiModel(),
                        totalPrice = product.getPrice() * quantity,
                    )
            } catch (e: CancellationException) {
                throw e
            } catch (_: IllegalArgumentException) {
                _event.send(DetailEvent.ShowProductNotFoundMessage)
                _event.send(DetailEvent.NavigateBack)
            } catch (_: Exception) {
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
            } catch (e: CancellationException) {
                throw e
            } catch (_: IllegalArgumentException) {
                _event.send(DetailEvent.ShowAddCartFailureMessage)
            } catch (_: Exception) {
                _event.send(DetailEvent.ShowAddCartFailureMessage)
            }
        }
    }

    companion object {
        fun provideFactory(
            id: Long,
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
