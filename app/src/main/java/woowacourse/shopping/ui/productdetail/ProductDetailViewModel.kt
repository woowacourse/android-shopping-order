package woowacourse.shopping.ui.productdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import java.io.IOException

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val recentProductRepo: RecentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val isFromBanner: Boolean =
        savedStateHandle[ProductDetailActivity.EXTRA_IS_FROM_BANNER] ?: false
    private val productId: Long =
        requireNotNull(savedStateHandle[ProductDetailActivity.EXTRA_PRODUCT_ID]) {
            "ProductDetail 화면을 띄우기 위해 상품 ID가 필요합니다."
        }

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
                val existing =
                    cartRepo
                        .getAllCartItems()
                        .items
                        .find { it.product.id == productId }
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
        val msg =
            when (e) {
                is IOException -> "네트워크 연결을 확인해주세요."
                is HttpException ->
                    when (e.code()) {
                        401, 403 -> "다시 로그인이 필요해요."
                        in 500..599 -> "서버에 일시적 문제가 있어요."
                        else -> defaultMessage
                    }

                else -> defaultMessage
            }
        _events.send(msg)
    }

    companion object {
        private const val TAG = "ProductDetailViewModel"

        fun provideFactory(
            productRepo: ProductRepository,
            cartRepo: CartRepository,
            recentProductRepo: RecentProductRepository,
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
                        productRepo = productRepo,
                        cartRepo = cartRepo,
                        recentProductRepo = recentProductRepo,
                    ) as T
                }
            }
    }
}
