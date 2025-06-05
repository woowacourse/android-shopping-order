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
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.GetCartRecommendProductsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.RemoveCartProductUseCase

class CartViewModel(
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
    private val getCartRecommendProductsUseCase: GetCartRecommendProductsUseCase,
    private val getCatalogProductUseCase: GetCatalogProductUseCase,
) : ViewModel() {
    private val _cartProducts: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val cartProducts: LiveData<Products> get() = _cartProducts

    private val _recommendedProducts: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val recommendedProducts: LiveData<Products> get() = _recommendedProducts

    private val _editedProductIds: MutableLiveData<Set<Long>> = MutableLiveData(emptySet())
    val editedProductIds: LiveData<Set<Long>> get() = _editedProductIds

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError: MutableLiveData<String> = MutableLiveData()
    val isError: LiveData<String> get() = _isError

    init {
        loadCartProducts()
    }

    private fun loadCartProducts(page: Page = cartProducts.value?.page ?: EMPTY_PAGE) {
        _isLoading.value = true
        viewModelScope.launch {
            getCartProductsUseCase(
                page = page.current,
                size = DEFAULT_PAGE_SIZE,
            ).onSuccess { cartProducts ->
                _cartProducts.value = cartProducts
                _isLoading.value = false
            }.onFailure {
                _isError.value = it.message
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
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                    loadCartProducts()
                }.onFailure {
                    _isError.value = it.message
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun increasePage(step: Int = DEFAULT_PAGE_STEP) {
        val page = cartProducts.value?.page ?: EMPTY_PAGE
        loadCartProducts(page.copy(current = page.current + step))
    }

    fun decreasePage(step: Int = DEFAULT_PAGE_STEP) {
        val page = cartProducts.value?.page ?: EMPTY_PAGE
        loadCartProducts(page.copy(current = page.current - step))
    }

    fun increaseCartProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = cartProducts.value?.getProductByProductId(productId) ?: return@launch
            increaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    _cartProducts.value = cartProducts.value?.updateQuantity(product, newQuantity)
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun decreaseCartProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = cartProducts.value?.getProductByProductId(productId) ?: return@launch
            decreaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    when (newQuantity > MINIMUM_QUANTITY) {
                        true -> _cartProducts.value = cartProducts.value?.updateQuantity(product, newQuantity)
                        false -> loadCartProducts()
                    }
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun toggleCartProductSelection(cartId: Long) {
        val cartProduct = cartProducts.value?.getProductByCartId(cartId) ?: return
        _cartProducts.value = cartProducts.value?.updateSelection(cartProduct)
    }

    fun toggleAllCartProductsSelection() {
        _cartProducts.value = cartProducts.value?.toggleAllSelection()
    }

    fun loadRecommendedProducts() {
        viewModelScope.launch {
            getCartRecommendProductsUseCase()
                .onSuccess { products ->
                    _recommendedProducts.value = products
                }.onFailure {
                    _isError.value = it.message
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun increaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = recommendedProducts.value?.getProductByProductId(productId) ?: return@launch
            increaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    when (product.quantity <= MINIMUM_QUANTITY) {
                        true -> updateRecommendedProduct(productId)
                        false -> _recommendedProducts.value = recommendedProducts.value?.updateQuantity(product, newQuantity)
                    }
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    private fun updateRecommendedProduct(productId: Long) {
        viewModelScope.launch {
            getCatalogProductUseCase(productId)
                .onSuccess { cartProduct ->
                    _recommendedProducts.value = recommendedProducts.value?.updateProduct(cartProduct.copy(isSelected = true))
                }.onFailure {
                    _isError.value = it.message
                    Log.e("CatalogViewModel", it.message.toString())
                }
        }
    }

    fun decreaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = recommendedProducts.value?.getProductByProductId(productId) ?: return@launch
            decreaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    _recommendedProducts.value = recommendedProducts.value?.updateQuantity(product, newQuantity)
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun getSelectedProductIds(): Set<Long> {
        val selectedCartProductsIds: List<Long> = cartProducts.value?.getSelectedProductIds() ?: emptyList()
        val selectedRecommendedProductsIds: List<Long> = recommendedProducts.value?.getSelectedProductIds() ?: emptyList()

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
