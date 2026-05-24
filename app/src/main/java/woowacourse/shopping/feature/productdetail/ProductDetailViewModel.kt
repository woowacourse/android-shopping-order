package woowacourse.shopping.feature.productdetail

import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.feature.common.state.ProductUiModel

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEvent>()
    val event: SharedFlow<AddEvent> = _event.asSharedFlow()

    fun initialLoading(
        productId: Long,
        recentProductId: Long? = null,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(productState = ProductDetailLoadingState.Loading) }
            val productLoadingState = getProduct(productId)
            if (recentProductId != null && productId != recentProductId) {
                val recentProductLoadingState = getProduct(recentProductId)
                _uiState.update {
                    it.copy(
                        productState = productLoadingState,
                        recentProductState = recentProductLoadingState,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        productState = productLoadingState,
                    )
                }
            }
        }
    }

    suspend fun getProduct(productId: Long): ProductDetailLoadingState =
        runCatching { productRepository.getProduct(productId) }
            .fold(
                onSuccess = { ProductDetailLoadingState.Success(toProductUiModel(it)) },
                onFailure = {
                    ProductDetailLoadingState.Error(
                        it.message
                            ?: "unknown error",
                    )
                },
            )

    fun toProductUiModel(product: Product): ProductUiModel =
        ProductUiModel(
            name = product.name,
            price = product.priceAmount(),
            imageUrl = product.imageUrl,
            id = product.id,
            quantity = 0,
        )

    fun increase() {
        _uiState.update {
            it.copy(quantity = it.quantity + 1)
        }
    }

    fun decrease() {
        _uiState.update {
            it.copy(quantity = it.quantity - 1)
        }
    }

    fun addToCart() {
        val loadingState =
            uiState.value.productState as? ProductDetailLoadingState.Success
                ?: return
        viewModelScope.launch {
            val cart = cartRepository.loadCart()
            val previousQuantity =
                cart.quantityOf(
                    productId = loadingState.product.id,
                )
            val product = productRepository.getProduct(loadingState.product.id)
            cartRepository.setProductQuantity(product, previousQuantity + uiState.value.quantity)
            _event.emit(AddEvent("장바구니에 담기 성공했습니다."))

        }
    }

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    val appDependencies = runBlocking { app.appDependenciesDeferred.await() }
                    ProductDetailViewModel(
                        productRepository = appDependencies.productRepository,
                        cartRepository = appDependencies.cartRepository,
                    )
                }
            }
    }
}

sealed interface ProductDetailLoadingState {
    data object None : ProductDetailLoadingState

    data object Loading : ProductDetailLoadingState

    data class Success(
        val product: ProductUiModel,
    ) : ProductDetailLoadingState

    data class Error(
        val errorString: String,
    ) : ProductDetailLoadingState
}

data class AddEvent(val message: String)
