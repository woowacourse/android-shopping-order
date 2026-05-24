package woowacourse.shopping.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
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
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.ui.model.mapper.toUiModel

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
) : ViewModel() {
    private val id: Long = savedStateHandle[PRODUCT_ID] ?: 0L

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
                    val lastViewedItemId = recentItemRepository.getLastViewedItemId()
                    val lastViewItem =
                        if (lastViewedItemId != null && lastViewedItemId != id) {
                            productRepository.getProductById(lastViewedItemId).getOrNull()
                        } else {
                            null
                        }

                    recentItemRepository.addRecentItem(product)
                    _uiState.update {
                        it.copy(
                            product = product.toUiModel(),
                            recentItem = lastViewItem?.toUiModel(),
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
        private const val PRODUCT_ID = "productId"

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    val appContainer = (this[APPLICATION_KEY] as ShoppingApplication).appContainer

                    DetailViewModel(
                        savedStateHandle = savedStateHandle,
                        productRepository = appContainer.productRepository,
                        cartRepository = appContainer.cartRepository,
                        recentItemRepository = appContainer.recentItemRepository,
                    )
                }
            }
    }
}
