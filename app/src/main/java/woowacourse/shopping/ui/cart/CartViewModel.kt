package woowacourse.shopping.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
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
import woowacourse.shopping.ui.cart.adapter.CartProductViewHolder
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class CartViewModel(
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
    private val getCartRecommendProductsUseCase: GetCartRecommendProductsUseCase,
    private val orderProductsUseCase: OrderProductsUseCase,
) : ViewModel(),
    CartProductViewHolder.OnClickHandler {
    private val _cartProducts: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val cartProducts: LiveData<Products> get() = _cartProducts

    private val _recommendedProducts: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val recommendedProducts: LiveData<Products> get() = _recommendedProducts

    private val _editedProductIds: MutableLiveData<Set<Long>> = MutableLiveData(emptySet())
    val editedProductIds: LiveData<Set<Long>> get() = _editedProductIds

    private val _totalOrderPrice: MediatorLiveData<Int> =
        combineLiveData(
            cartProducts,
            recommendedProducts,
            transform = { cart, recommend ->
                cart.getSelectedCartProductsPrice() + recommend.getSelectedCartRecommendProductsPrice()
            },
            INIT_ORDER_PRICE,
        )
    val totalOrderPrice: LiveData<Int> get() = _totalOrderPrice

    private val _totalQuantity: MediatorLiveData<Int> =
        combineLiveData(
            cartProducts,
            recommendedProducts,
            transform = { cart, recommend ->
                cart.getSelectedCartProductQuantity() + recommend.getSelectedCartRecommendProductQuantity()
            },
            INIT_ORDER_QUANTITY,
        )
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isOrdered: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val isOrdered: SingleLiveData<Unit> get() = _isOrdered

    init {
        loadCartProducts()
    }

    override fun onRemoveCartProductClick(
        cartId: Long,
        productId: Long,
    ) {
        removeCartProduct(cartId, productId)
    }

    override fun onIncreaseClick(productId: Long) {
        increaseCartProductQuantity(productId)
    }

    override fun onDecreaseClick(productId: Long) {
        decreaseCartProductQuantity(productId)
    }

    override fun onSelectClick(cartId: Long) {
        toggleCartProductSelection(cartId)
    }

    fun increasePage(step: Int = DEFAULT_PAGE_STEP) {
        val page = cartProducts.value?.page ?: EMPTY_PAGE
        loadCartProducts(page.copy(current = page.current + step))
    }

    fun decreasePage(step: Int = DEFAULT_PAGE_STEP) {
        val page = cartProducts.value?.page ?: EMPTY_PAGE
        loadCartProducts(page.copy(current = page.current - step))
    }

    fun toggleAllCartProductsSelection() {
        _cartProducts.value = cartProducts.value?.updateAllSelection()
    }

    fun loadRecommendedProducts() {
        viewModelScope.launch {
            val result = getCartRecommendProductsUseCase()
            result
                .onSuccess { products ->
                    _recommendedProducts.value = products
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    fun increaseRecommendedProductQuantity(productId: Long) {
        viewModelScope.launch {
            val result =
                increaseCartProductQuantityUseCase(
                    product =
                        recommendedProducts.value?.getProductByProductId(productId)
                            ?: return@launch,
                )
            result
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
            val result =
                decreaseCartProductQuantityUseCase(
                    product =
                        recommendedProducts.value?.getProductByProductId(productId)
                            ?: return@launch,
                )
            result
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
        val selectedCartProductsIds: List<Long> =
            cartProducts.value?.getSelectedCartProductIds() ?: emptyList()
        val selectedRecommendedProductsIds: List<Long> =
            recommendedProducts.value?.getSelectedCartRecommendProductIds() ?: emptyList()
        val selectedProductIds: Set<Long> =
            (selectedCartProductsIds + selectedRecommendedProductsIds).toSet()

        if (selectedProductIds.isEmpty()) return

        viewModelScope.launch {
            val result = orderProductsUseCase(selectedProductIds)
            result
                .onSuccess {
                    _editedProductIds.value = selectedProductIds
                    _isOrdered.setValue(Unit)
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    private fun loadCartProducts(page: Page = cartProducts.value?.page ?: EMPTY_PAGE) {
        _isLoading.value = true
        viewModelScope.launch {
            val result =
                getCartProductsUseCase(
                    page = page.current,
                    size = DEFAULT_PAGE_SIZE,
                )
            result
                .onSuccess { cartProducts ->
                    _cartProducts.value = cartProducts
                    _isLoading.value = false
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    private fun removeCartProduct(
        cartId: Long,
        productId: Long,
    ) {
        viewModelScope.launch {
            val result = removeCartProductUseCase(cartId)

            result
                .onSuccess {
                    _editedProductIds.value = editedProductIds.value?.plus(productId)
                    loadCartProducts()
                }.onFailure {
                    Log.e("CartViewModel", it.message.toString())
                }
        }
    }

    private fun increaseCartProductQuantity(productId: Long) {
        viewModelScope.launch {
            val result =
                increaseCartProductQuantityUseCase(
                    product = cartProducts.value?.getProductByProductId(productId) ?: return@launch,
                )
            result
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

    private fun decreaseCartProductQuantity(productId: Long) {
        viewModelScope.launch {
            val result =
                decreaseCartProductQuantityUseCase(
                    product = cartProducts.value?.getProductByProductId(productId) ?: return@launch,
                )
            result
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

    private fun toggleCartProductSelection(cartId: Long) {
        _cartProducts.value = cartProducts.value?.toggleSelectionByCartId(cartId)
    }

    companion object {
        const val DEFAULT_PAGE_STEP: Int = 1
        const val PAGE_INDEX_OFFSET: Int = 1
        const val DEFAULT_PAGE_SIZE: Int = 5
        private const val INIT_ORDER_PRICE = 0
        private const val INIT_ORDER_QUANTITY = 0

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY]) as ShoppingApp

                    return CartViewModel(
                        getCartProductsUseCase = application.getCartProductsUseCase,
                        removeCartProductUseCase = application.removeCartProductUseCase,
                        increaseCartProductQuantityUseCase = application.increaseCartProductQuantityUseCase,
                        decreaseCartProductQuantityUseCase = application.decreaseCartProductQuantityUseCase,
                        getCartRecommendProductsUseCase = application.getCartRecommendProductsUseCase,
                        orderProductsUseCase = application.orderProductsUseCase,
                    ) as T
                }
            }

        private fun <T, R> combineLiveData(
            source1: LiveData<T>,
            source2: LiveData<T>,
            transform: (T, T) -> R,
            initialValue: R,
        ): MediatorLiveData<R> =
            MediatorLiveData<R>().apply {
                var data1: T? = null
                var data2: T? = null

                value = initialValue
                addSource(source1) {
                    data1 = it
                    if (data1 != null && data2 != null) {
                        value = transform(data1!!, data2!!)
                    }
                }

                addSource(source2) {
                    data2 = it
                    if (data1 != null && data2 != null) {
                        value = transform(data1!!, data2!!)
                    }
                }
            }
    }
}
