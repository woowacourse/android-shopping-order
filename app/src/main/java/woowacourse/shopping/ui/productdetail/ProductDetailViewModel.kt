package woowacourse.shopping.ui.productdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.navigation.ProductDetail
import woowacourse.shopping.ui.common.error.ErrorMessageMapper

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val recentProductRepo: RecentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    private val _events = Channel<String>(Channel.BUFFERED)
    private val route: ProductDetail = savedStateHandle.toRoute()
    private val productId: Long = route.id
    private val isFromBanner: Boolean = route.isFromBanner

    val uiState = _uiState.asStateFlow()
    val events = _events.receiveAsFlow()


    init {
        loadProduct()
    }

    fun increase() {
        _uiState.update {
            it.copy(selectedQuantity = it.selectedQuantity + 1)
        }
    }

    fun decrease() {
        _uiState.update {
            it.copy(selectedQuantity = maxOf(1, it.selectedQuantity - 1))
        }
    }

    fun addToCart() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (state.existingCartItemId == null) {
                    val newId = cartRepo.add(productId, state.selectedQuantity)
                    _uiState.update {
                        it.copy(
                            existingCartItemId = newId,
                            existingQuantity = state.selectedQuantity
                        )
                    }
                } else {
                    val newQuantity = state.existingQuantity + state.selectedQuantity
                    cartRepo.updateQuantity(state.existingCartItemId, newQuantity)
                    _uiState.update { it.copy(existingQuantity = newQuantity) }
                }
            } catch (e: Exception) {
                handleError("addToCart", e, "장바구니에 담을 수 없어요.")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val product = productRepo.findProduct(productId)
                val existing = cartRepo.getAllCartItems().findByProductId(productId)
                val bannerProduct =
                    recentProductRepo
                        .getLastViewedProduct()
                        ?.takeIf { !isFromBanner && it.id != productId }

                _uiState.update {
                    it.copy(
                        product = product,
                        selectedQuantity = 1,
                        existingCartItemId = existing?.id,
                        existingQuantity = existing?.quantity ?: 0,
                        lastViewedProduct = bannerProduct,
                    )
                }
                recentProductRepo.add(productId)
            } catch (e: Exception) {
                handleError("loadProduct", e, "상품 정보를 불러올 수 없어요.")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun handleError(
        tag: String,
        e: Exception,
        defaultMessage: String,
    ) {
        if (e is CancellationException) throw e
        Log.e(TAG, "$tag 에러", e)
        _events.send(ErrorMessageMapper.toUserMessage(e, defaultMessage))
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"

        fun provideFactory(
            container: AppContainer,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val savedStateHandle = extras.createSavedStateHandle()

                    return ProductDetailViewModel(
                        savedStateHandle = savedStateHandle,
                        productRepo = container.productRepository,
                        cartRepo = container.cartRepository,
                        recentProductRepo = container.recentProductRepository,
                    ) as T
                }
            }
    }
}
