package woowacourse.shopping.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseInjection.decreaseCartProductQuantityUseCase
import woowacourse.shopping.di.UseCaseInjection.getCartProductsUseCase
import woowacourse.shopping.di.UseCaseInjection.getCartRecommendProductsUseCase
import woowacourse.shopping.di.UseCaseInjection.getCatalogProductUseCase
import woowacourse.shopping.di.UseCaseInjection.increaseCartProductQuantityUseCase
import woowacourse.shopping.di.UseCaseInjection.removeCartProductUseCase
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.GetCartRecommendProductsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.RemoveCartProductUseCase
import woowacourse.shopping.ui.model.CartProductUiModel

class CartViewModel(
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
    private val getCartRecommendProductsUseCase: GetCartRecommendProductsUseCase,
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
) : ViewModel() {
    private val _uiModel: MutableLiveData<CartProductUiModel> = MutableLiveData(CartProductUiModel())
    val uiModel: LiveData<CartProductUiModel> get() = _uiModel

    init {
        loadCartProducts()
    }

    private fun loadCartProducts(page: Page = uiModel.value?.cartProducts?.page ?: EMPTY_PAGE) {
        updateUiModel { current ->
            current.copy(isProductsLoading = true)
        }

        viewModelScope.launch {
            getCartProductsUseCase(
                page = page.current,
                size = DEFAULT_PAGE_SIZE,
            ).onSuccess { cartProducts ->
                updateUiModel { current ->
                    current.copy(
                        cartProducts = cartProducts,
                        isProductsLoading = false,
                    )
                }
            }.onFailure {
                updateUiModel { current ->
                    current.copy(connectionErrorMessage = it.message.toString())
                }
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
                    updateUiModel { current ->
                        current.copy(editedProductIds = current.editedProductIds.plus(productId))
                    }
                    loadCartProducts()
                }.onFailure {
                    updateUiModel { current ->
                        current.copy(connectionErrorMessage = it.message.toString())
                    }
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun increasePage(step: Int = DEFAULT_PAGE_STEP) {
        val page = uiModel.value?.cartProducts?.page ?: EMPTY_PAGE
        loadCartProducts(page.copy(current = page.current + step))
    }

    fun decreasePage(step: Int = DEFAULT_PAGE_STEP) {
        val page = uiModel.value?.cartProducts?.page ?: EMPTY_PAGE
        loadCartProducts(page.copy(current = page.current - step))
    }

    fun increaseCartProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = uiModel.value?.cartProducts?.getProductByProductId(productId) ?: return@launch

            increaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    updateUiModel { current ->
                        current.copy(
                            cartProducts = current.cartProducts.updateQuantity(product, newQuantity),
                            editedProductIds = current.editedProductIds.plus(productId),
                            isProductsLoading = false,
                        )
                    }
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun decreaseCartProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = uiModel.value?.cartProducts?.getProductByProductId(productId) ?: return@launch
            decreaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    when (newQuantity > MINIMUM_QUANTITY) {
                        true ->
                            updateUiModel { current ->
                                current.copy(
                                    cartProducts = current.cartProducts.updateQuantity(product, newQuantity),
                                    editedProductIds = current.editedProductIds.plus(productId),
                                )
                            }

                        false -> loadCartProducts()
                    }
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun toggleCartProductSelection(cartId: Long) {
        val cartProduct = uiModel.value?.cartProducts?.getProductByCartId(cartId) ?: return

        updateUiModel { current ->
            current.copy(cartProducts = current.cartProducts.updateSelection(cartProduct))
        }
    }

    fun toggleAllCartProductsSelection() {
        updateUiModel { current ->
            current.copy(
                cartProducts = current.cartProducts.toggleAllSelection(),
            )
        }
    }

    fun loadRecommendedProducts() {
        viewModelScope.launch {
            getCartRecommendProductsUseCase()
                .onSuccess { products ->
                    updateUiModel { current ->
                        current.copy(recommendedProducts = products)
                    }
                }.onFailure {
                    updateUiModel { current ->
                        current.copy(connectionErrorMessage = it.message.toString())
                    }
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun increaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = uiModel.value?.recommendedProducts?.getProductByProductId(productId) ?: return@launch

            increaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    when (product.quantity <= MINIMUM_QUANTITY) {
                        true -> updateRecommendedProduct(productId)
                        false ->
                            updateUiModel { current ->
                                current.copy(
                                    recommendedProducts = current.recommendedProducts.updateQuantity(product, newQuantity),
                                    editedProductIds = current.editedProductIds.plus(productId),
                                )
                            }
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
                    updateUiModel { current ->
                        current.copy(
                            recommendedProducts = current.recommendedProducts.updateProduct(cartProduct.copy(isSelected = true)),
                        )
                    }
                }.onFailure {
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun decreaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = uiModel.value?.recommendedProducts?.getProductByProductId(productId) ?: return@launch

            decreaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    when (product.quantity <= MINIMUM_QUANTITY) {
                        true -> updateRecommendedProduct(productId)
                        false ->
                            updateUiModel { current ->
                                current.copy(
                                    recommendedProducts = current.recommendedProducts.updateQuantity(product, newQuantity),
                                    editedProductIds = current.editedProductIds.plus(productId),
                                )
                            }
                    }
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun getSelectedProductIds(): Set<Long> {
        val uiModel = uiModel.value ?: return emptySet()

        val selectedCartProductsIds: List<Long> = uiModel.cartProducts.getSelectedProductIds()
        val selectedRecommendedProductsIds: List<Long> = uiModel.recommendedProducts.getSelectedProductIds()

        return (selectedCartProductsIds + selectedRecommendedProductsIds).toSet()
    }

    private fun updateUiModel(update: (CartProductUiModel) -> CartProductUiModel) {
        val current = _uiModel.value ?: return
        _uiModel.value = update(current)
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
