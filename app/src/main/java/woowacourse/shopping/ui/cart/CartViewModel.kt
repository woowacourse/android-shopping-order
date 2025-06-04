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
import woowacourse.shopping.di.UseCaseModule.increaseCartProductQuantityUseCase
import woowacourse.shopping.di.UseCaseModule.orderProductsUseCase
import woowacourse.shopping.di.UseCaseModule.removeCartProductUseCase
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.GetCartRecommendProductsUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.OrderProductsUseCase
import woowacourse.shopping.domain.usecase.RemoveCartProductUseCase
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class CartViewModel(
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
    private val getCartRecommendProductsUseCase: GetCartRecommendProductsUseCase,
    private val orderProductsUseCase: OrderProductsUseCase,
) : ViewModel() {
    private val _cartProducts: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val cartProducts: LiveData<Products> get() = _cartProducts

    private val _recommendedProducts: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val recommendedProducts: LiveData<Products> get() = _recommendedProducts

    private val _editedProductIds: MutableLiveData<Set<Long>> = MutableLiveData(emptySet())
    val editedProductIds: LiveData<Set<Long>> get() = _editedProductIds

    private val _totalOrderPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalOrderPrice: LiveData<Int> get() = _totalOrderPrice

    private val _isOrdered: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val isOrdered: SingleLiveData<Unit> get() = _isOrdered

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
                    _cartProducts.value =
                        cartProducts.value?.updateProductQuantity(
                            productId,
                            newQuantity,
                        )
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
                    if (newQuantity > MINIMUM_QUANTITY) {
                        _cartProducts.value =
                            cartProducts.value?.updateProductQuantity(
                                productId,
                                newQuantity,
                            )
                    } else {
                        loadCartProducts()
                    }
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun toggleCartProductSelection(cartId: Long) {
        _cartProducts.value = cartProducts.value?.toggleSelectionByCartId(cartId)
    }

    fun toggleAllCartProductsSelection() {
        _cartProducts.value = cartProducts.value?.updateAllSelection()
    }

    fun updateOrderInfo() {
        _totalOrderPrice.value =
            (cartProducts.value?.getSelectedCartProductsPrice() ?: 0) +
            (recommendedProducts.value?.getSelectedCartRecommendProductsPrice() ?: 0)
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
                    _recommendedProducts.value =
                        recommendedProducts.value?.updateProductQuantity(
                            productId,
                            newQuantity,
                        )
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun decreaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch {
            val product = recommendedProducts.value?.getProductByProductId(productId) ?: return@launch
            decreaseCartProductQuantityUseCase(product)
                .onSuccess { newQuantity ->
                    if (newQuantity > MINIMUM_QUANTITY) {
                        _recommendedProducts.value =
                            recommendedProducts.value?.updateProductQuantity(
                                productId,
                                newQuantity,
                            )
                    } else {
                        loadCartProducts()
                    }
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun orderProducts() {
        val selectedCartProductsIds: List<Long> = cartProducts.value?.getSelectedCartProductIds() ?: emptyList()
        val selectedRecommendedProductsIds: List<Long> = recommendedProducts.value?.getSelectedCartRecommendProductIds() ?: emptyList()
        val selectedProductIds: Set<Long> = (selectedCartProductsIds + selectedRecommendedProductsIds).toSet()

        if (selectedProductIds.isEmpty()) return

        viewModelScope.launch {
            orderProductsUseCase(selectedProductIds)
                .onSuccess {
                    _editedProductIds.value = selectedProductIds
                    _isOrdered.postValue(Unit)
                }.onFailure {
                    _isError.value = it.message
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    companion object {
        const val DEFAULT_PAGE_STEP: Int = 1
        const val PAGE_INDEX_OFFSET: Int = 1
        const val DEFAULT_PAGE_SIZE: Int = 5

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
                        orderProductsUseCase = orderProductsUseCase,
                    ) as T
            }
    }
}
