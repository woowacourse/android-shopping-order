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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.ui.model.mapper.toUiModel

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
            productRepository
                .getProductById(id)
                .onSuccess { product ->
                    val lastViewItem =
                        if (hideRecentItem) {
                            null
                        } else {
                            val lastViewedItemId = recentItemRepository.getLastViewedItemId()
                            if (lastViewedItemId != null && lastViewedItemId != id) {
                                productRepository.getProductById(lastViewedItemId).getOrNull()
                            } else {
                                null
                            }
                        }
                    recentItemRepository.addRecentItem(product)
                    _uiState.update {
                        it.copy(
                            product = product.toUiModel(),
                            recentItem = lastViewItem?.takeIf { it.id != id }?.toUiModel(),
                            totalPrice = product.getPrice(),
                        )
                    }
                }.onFailure { e ->
                    if (e is IllegalArgumentException) {
                        _event.send(DetailEvent.ShowProductNotFoundMessage)
                    } else {
                        _event.send(DetailEvent.ShowProductLoadFailureMessage)
                    }
                    _event.send(DetailEvent.NavigateBack)
                }
        }
    }

    fun updateQuantity(quantity: Int) {
        val nextQuantity = quantity.coerceAtLeast(1)

        _uiState.update {
            it.copy(
                quantity = nextQuantity,
                totalPrice = it.product.price * nextQuantity,
            )
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            cartRepository
                .addCartItemQuantity(
                    productId = id,
                    quantity = _uiState.value.quantity,
                ).onSuccess {
                    _event.send(DetailEvent.NavigateToCart)
                }.onFailure {
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
