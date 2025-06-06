package woowacourse.shopping.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseModule.decreaseCartProductQuantityUseCase
import woowacourse.shopping.di.UseCaseModule.getCartProductsUseCase
import woowacourse.shopping.di.UseCaseModule.getCartRecommendProductsUseCase
import woowacourse.shopping.di.UseCaseModule.getCatalogProductUseCase
import woowacourse.shopping.di.UseCaseModule.increaseCartProductQuantityUseCase
import woowacourse.shopping.di.UseCaseModule.removeCartProductUseCase
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.GetCartRecommendProductsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.RemoveCartProductUseCase
import woowacourse.shopping.ui.model.CartProductUiState

class CartViewModel(
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
    private val getCartRecommendProductsUseCase: GetCartRecommendProductsUseCase,
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<CartProductUiState> = MutableLiveData(CartProductUiState())
    val uiState: LiveData<CartProductUiState> get() = _uiState

    init {
        loadCartProducts()
    }

    private fun loadCartProducts(page: Page = uiState.value?.cartProducts?.page ?: EMPTY_PAGE) {
        _uiState.value = uiState.value?.copy(isProductsLoading = true)

        viewModelScope.launch {
            getCartProductsUseCase(
                page = page.current,
                size = DEFAULT_PAGE_SIZE,
            ).onSuccess { cartProducts ->
                val uiState = uiState.value ?: return@launch

                _uiState.value =
                    uiState.copy(
                        cartProducts = cartProducts,
                        isProductsLoading = false,
                    )
            }.onFailure {
                _uiState.value = uiState.value?.copy(connectionErrorMessage = it.message.toString())
                Log.e("CartViewModel", it.message.toString())
            }
        }
    }

    fun removeCartProduct(
        cartId: Long,
        productId: Long,
    ) {
        viewModelScope.launch {
            removeCartProductUseCase(cartId)
                .onSuccess {
                    val uiState = uiState.value ?: return@launch

                    _uiState.value =
                        uiState.copy(
                            editedProductIds = uiState.editedProductIds.plus(productId),
                        )

                    loadCartProducts()
                }.onFailure {
                    _uiState.value = uiState.value?.copy(connectionErrorMessage = it.message.toString())
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun increasePage(step: Int = DEFAULT_PAGE_STEP) {
        val page = uiState.value?.cartProducts?.page ?: EMPTY_PAGE
        loadCartProducts(page.copy(current = page.current + step))
    }

    fun decreasePage(step: Int = DEFAULT_PAGE_STEP) {
        val page = uiState.value?.cartProducts?.page ?: EMPTY_PAGE
        loadCartProducts(page.copy(current = page.current - step))
    }

    fun increaseCartProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = uiState.value?.cartProducts?.getProductByProductId(productId) ?: return@launch

            increaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    val uiState = uiState.value ?: return@launch

                    _uiState.value =
                        uiState.copy(
                            cartProducts = uiState.cartProducts.updateQuantity(product, newQuantity),
                            editedProductIds = uiState.editedProductIds.plus(productId),
                            isProductsLoading = false,
                        )
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun decreaseCartProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = uiState.value?.cartProducts?.getProductByProductId(productId) ?: return@launch
            decreaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    val uiState = uiState.value ?: return@launch

                    when (newQuantity > MINIMUM_QUANTITY) {
                        true ->
                            _uiState.value =
                                uiState.copy(
                                    cartProducts = uiState.cartProducts.updateQuantity(product, newQuantity),
                                    editedProductIds = uiState.editedProductIds.plus(productId),
                                )

                        false -> loadCartProducts()
                    }
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun toggleCartProductSelection(cartId: Long) {
        val uiState = uiState.value ?: return
        val cartProduct = uiState.cartProducts.getProductByCartId(cartId) ?: return

        _uiState.value =
            uiState.copy(
                cartProducts = uiState.cartProducts.updateSelection(cartProduct),
            )
    }

    fun toggleAllCartProductsSelection() {
        val uiState = uiState.value ?: return

        _uiState.value =
            uiState.copy(
                cartProducts = uiState.cartProducts.toggleAllSelection(),
            )
    }

    fun loadRecommendedProducts() {
        viewModelScope.launch {
            getCartRecommendProductsUseCase()
                .onSuccess { products ->
                    _uiState.value =
                        uiState.value?.copy(
                            recommendedProducts = products,
                        )
                }.onFailure {
                    _uiState.value = uiState.value?.copy(connectionErrorMessage = it.message.toString())
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun increaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = uiState.value?.recommendedProducts?.getProductByProductId(productId) ?: return@launch

            increaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    val uiState = uiState.value ?: return@launch

                    when (product.quantity <= MINIMUM_QUANTITY) {
                        true -> updateRecommendedProduct(productId)
                        false ->
                            _uiState.value =
                                uiState.copy(
                                    recommendedProducts = uiState.recommendedProducts.updateQuantity(product, newQuantity),
                                    editedProductIds = uiState.editedProductIds.plus(productId),
                                )
                    }
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    private fun updateRecommendedProduct(productId: Long) {
        viewModelScope.launch {
            getCatalogProductUseCase(productId)
                .onSuccess { cartProduct ->
                    val uiState = uiState.value ?: return@launch

                    _uiState.value =
                        uiState.copy(
                            recommendedProducts = uiState.recommendedProducts.updateProduct(cartProduct.copy(isSelected = true)),
                        )
                }.onFailure {
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun decreaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = uiState.value?.recommendedProducts?.getProductByProductId(productId) ?: return@launch

            decreaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    val uiState = uiState.value ?: return@launch

                    when (product.quantity <= MINIMUM_QUANTITY) {
                        true -> updateRecommendedProduct(productId)
                        false ->
                            _uiState.value =
                                uiState.copy(
                                    recommendedProducts = uiState.recommendedProducts.updateQuantity(product, newQuantity),
                                    editedProductIds = uiState.editedProductIds.plus(productId),
                                )
                    }
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun getSelectedProductIds(): Set<Long> {
        val uiState = uiState.value ?: return emptySet()

        val selectedCartProductsIds: List<Long> = uiState.cartProducts.getSelectedProductIds()
        val selectedRecommendedProductsIds: List<Long> = uiState.recommendedProducts.getSelectedProductIds()

        return (selectedCartProductsIds + selectedRecommendedProductsIds).toSet()
    }

    companion object {
        const val DEFAULT_PAGE_STEP: Int = 1
        const val PAGE_INDEX_OFFSET: Int = 1
        private const val DEFAULT_PAGE_SIZE: Int = 5

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T =
                    CartViewModel(
                        getCartProductsUseCase = getCartProductsUseCase,
                        removeCartProductUseCase = removeCartProductUseCase,
                        increaseCartProductQuantityUseCase = increaseCartProductQuantityUseCase,
                        decreaseCartProductQuantityUseCase = decreaseCartProductQuantityUseCase,
                        getCartRecommendProductsUseCase = getCartRecommendProductsUseCase,
                        getCatalogProductUseCase = getCatalogProductUseCase,
                    ) as T
            }
    }
}
