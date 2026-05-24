package woowacourse.shopping.ui.cart.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.cart.recommend.RecommendUiState
import woowacourse.shopping.ui.util.LoadState
import woowacourse.shopping.ui.util.toUiModel

class RecommendViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val cartItemsFlow = cartRepository.cartItems

    private val recommendProductsFlow = MutableStateFlow<List<Product>>(emptyList())
    private val recommendLoadStateFlow = MutableStateFlow<LoadState>(LoadState.Initial)

    val uiState: StateFlow<RecommendUiState> =
        combine(
            recommendProductsFlow,
            cartItemsFlow,
            recommendLoadStateFlow,
        ) { recommendProducts, cartItems, loadState ->
            RecommendUiState(
                recommendProducts = recommendProducts.toUiModel(cartItems),
                loadState = loadState,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecommendUiState(),
        )

    init {
        loadRecommendProduct()
    }

    private fun loadRecommendProduct() {
        viewModelScope.launch {
            recommendLoadStateFlow.update { LoadState.Loading }
            runCatching {
                val recommended = recentProductRepository.getMostRecentProduct()

                if (recommended == null) {
                    recommendLoadStateFlow.update { LoadState.Success }
                    return@launch
                }
                val productList = productRepository.getProducts(0, Int.MAX_VALUE)
                val categoryProducts = productList.getCategoryProducts(recommended.category.value)
                val result =
                    categoryProducts -
                        cartItemsFlow.value.values
                            .map { it.product }
                            .toSet()
                recommendProductsFlow.update { result.take(MAX_RECOMMEND_SIZE) }
                recommendLoadStateFlow.update { LoadState.Success }
            }.onFailure { throwable ->
                recommendLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun increaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            runCatching {
                val target = cartItemsFlow.value.findByProductId(productId) ?: return@launch
                cartRepository.updateQuantity(target.id, target.quantity.value + 1)
            }.onFailure { throwable ->
                recommendLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun decreaseRecommendProduct(productId: Int) {
        viewModelScope.launch {
            runCatching {
                val target = cartItemsFlow.value.findByProductId(productId) ?: return@launch
                if (target.quantity.value == 1) {
                    cartRepository.removeCartItem(target.id)
                } else {
                    cartRepository.updateQuantity(target.id, target.quantity.value - 1)
                }
            }.onFailure { throwable ->
                recommendLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    companion object {
        private const val MAX_RECOMMEND_SIZE = 10

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                    RecommendViewModel(
                        cartRepository = application.appContainer.cartRepository,
                        productRepository = application.appContainer.productRepository,
                        recentProductRepository = application.appContainer.recentProductRepository,
                    )
                }
            }
    }
}
