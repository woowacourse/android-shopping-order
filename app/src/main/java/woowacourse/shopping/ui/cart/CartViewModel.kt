package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.RemoveCartProductUseCase

class CartViewModel(
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase,
    private val increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase,
    private val decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase,
) : ViewModel() {
    private val _cartProducts: MutableLiveData<Products> = MutableLiveData(EMPTY_PRODUCTS)
    val cartProducts: LiveData<Products> get() = _cartProducts

    private val _editedProductIds: MutableLiveData<Set<Long>> = MutableLiveData(emptySet())
    val editedProductIds: LiveData<Set<Long>> get() = _editedProductIds

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadCartProducts()
    }

    private fun loadCartProducts(page: Page = cartProducts.value?.page ?: EMPTY_PAGE) {
        runCatching {
            _isLoading.value = true
            getCartProductsUseCase(
                page = page.current,
                size = DEFAULT_PAGE_SIZE,
            ) { cartProducts ->
                _cartProducts.postValue(cartProducts)
            }
        }.onSuccess {
            _isLoading.value = false
        }
    }

    fun removeCartProduct(
        cartId: Long,
        productId: Long,
    ) {
        removeCartProductUseCase(cartId)
        _editedProductIds.postValue(editedProductIds.value?.plus(productId))
        loadCartProducts()
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
        increaseCartProductQuantityUseCase(
            product = cartProducts.value?.getProductByProductId(productId) ?: return,
        ) { newQuantity ->
            _cartProducts.postValue(
                cartProducts.value?.updateProductQuantity(
                    productId,
                    newQuantity,
                ),
            )
        }
        _editedProductIds.value = editedProductIds.value?.plus(productId)
    }

    fun decreaseCartProductQuantity(productId: Long) {
        decreaseCartProductQuantityUseCase(
            product = cartProducts.value?.getProductByProductId(productId) ?: return,
        ) { newQuantity ->
            if (newQuantity > MINIMUM_QUANTITY) {
                _cartProducts.postValue(
                    cartProducts.value?.updateProductQuantity(
                        productId,
                        newQuantity,
                    ),
                )
            } else {
                loadCartProducts()
            }
        }
        _editedProductIds.value = editedProductIds.value?.plus(productId)
    }

    fun updateCartProductSelection(cartId: Long) {
        _cartProducts.value = cartProducts.value?.toggleSelectionByCartId(cartId)
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
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY]) as ShoppingApp

                    return CartViewModel(
                        getCartProductsUseCase = application.getCartProductsUseCase,
                        removeCartProductUseCase = application.removeCartProductUseCase,
                        increaseCartProductQuantityUseCase = application.increaseCartProductQuantityUseCase,
                        decreaseCartProductQuantityUseCase = application.decreaseCartProductQuantityUseCase,
                    ) as T
                }
            }
    }
}
