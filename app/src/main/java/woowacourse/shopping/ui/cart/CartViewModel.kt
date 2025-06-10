package woowacourse.shopping.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineExceptionHandler
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

        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                updateUiModel { current -> current.copy(connectionErrorMessage = e.message.toString()) }
                Log.e(TAG, e.message.toString())
            },
        ) {
            val products =
                getCartProductsUseCase(
                    page = page.current,
                    size = DEFAULT_PAGE_SIZE,
                )

            updateUiModel { current ->
                current.copy(
                    cartProducts = products,
                    isProductsLoading = false,
                )
            }
        }
    }

    fun removeCartProduct(
        cartId: Long,
        productId: Long,
    ) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                updateUiModel { current -> current.copy(connectionErrorMessage = e.message.toString()) }
                Log.e(TAG, e.message.toString())
            },
        ) {
            removeCartProductUseCase(cartId)
            updateUiModel { current ->
                current.copy(editedProductIds = current.editedProductIds.plus(productId))
            }
            loadCartProducts()
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
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                Log.e(TAG, e.message.toString())
            },
        ) {
            val product = uiModel.value?.cartProducts?.getProductByProductId(productId) ?: return@launch
            val newQuantity = increaseCartProductQuantityUseCase(product)

            updateUiModel { current ->
                current.copy(
                    cartProducts = current.cartProducts.updateQuantity(product, newQuantity),
                    editedProductIds = current.editedProductIds.plus(productId),
                    isProductsLoading = false,
                )
            }
        }
    }

    fun decreaseCartProductQuantity(productId: Long) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                Log.e(TAG, e.message.toString())
            },
        ) {
            val product = uiModel.value?.cartProducts?.getProductByProductId(productId) ?: return@launch
            val newQuantity = decreaseCartProductQuantityUseCase(product)

            when (newQuantity > MINIMUM_QUANTITY) {
                true -> {
                    updateUiModel { current ->
                        current.copy(
                            cartProducts = current.cartProducts.updateQuantity(product, newQuantity),
                            editedProductIds = current.editedProductIds.plus(productId),
                        )
                    }
                }

                false -> loadCartProducts()
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
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                updateUiModel { current -> current.copy(connectionErrorMessage = e.message.toString()) }
                Log.e(TAG, e.message.toString())
            },
        ) {
            val products = getCartRecommendProductsUseCase()

            updateUiModel { current ->
                current.copy(recommendedProducts = products)
            }
        }
    }

    fun increaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                Log.e(TAG, e.message.toString())
            },
        ) {
            val product = uiModel.value?.recommendedProducts?.getProductByProductId(productId) ?: return@launch
            val newQuantity = increaseCartProductQuantityUseCase(product)

            when (product.quantity <= MINIMUM_QUANTITY) {
                true -> updateRecommendedProduct(productId)
                false -> {
                    updateUiModel { current ->
                        current.copy(
                            recommendedProducts = current.recommendedProducts.updateQuantity(product, newQuantity),
                            editedProductIds = current.editedProductIds.plus(productId),
                        )
                    }
                }
            }
        }
    }

    private fun updateRecommendedProduct(productId: Long) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                Log.e(TAG, e.message.toString())
            },
        ) {
            val product = getCatalogProductUseCase(productId)

            updateUiModel { current ->
                current.copy(
                    recommendedProducts = current.recommendedProducts.updateProduct(product.copy(isSelected = true)),
                )
            }
        }
    }

    fun decreaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, e ->
                Log.e(TAG, e.message.toString())
            },
        ) {
            val product = uiModel.value?.recommendedProducts?.getProductByProductId(productId) ?: return@launch
            val newQuantity = decreaseCartProductQuantityUseCase(product)

            when (product.quantity <= MINIMUM_QUANTITY) {
                true -> updateRecommendedProduct(productId)
                false -> {
                    updateUiModel { current ->
                        current.copy(
                            recommendedProducts = current.recommendedProducts.updateQuantity(product, newQuantity),
                            editedProductIds = current.editedProductIds.plus(productId),
                        )
                    }
                }
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
        private const val TAG: String = "CartViewModel"

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
