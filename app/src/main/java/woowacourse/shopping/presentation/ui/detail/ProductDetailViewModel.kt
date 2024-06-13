package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.mapper.toUiModel
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.util.Event

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentRepository: RecentRepository,
    private val productId: Long,
    isLastViewedItem: Boolean,
) : ViewModel(), DetailHandler {
    private val _product = MutableLiveData<ProductModel>()
    val product: LiveData<ProductModel> get() = _product

    private val _lastProduct = MutableLiveData(ProductModel.INVALID_PRODUCT_MODEL)
    val lastProduct: LiveData<ProductModel> get() = _lastProduct

    private val _error = MutableLiveData<Event<DetailError>>()
    val error: LiveData<Event<DetailError>> get() = _error

    private val _navigationEvent = MutableLiveData<Event<FromDetailToScreen>>()
    val navigationEvent: LiveData<Event<FromDetailToScreen>> get() = _navigationEvent

    val showLastProduct: LiveData<Boolean> = lastProduct.map { it != ProductModel.INVALID_PRODUCT_MODEL && it.id != productId }

    init {
        fetchInitialData()
        if (!isLastViewedItem) loadLastProduct()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            productRepository.loadById(productId)
                .onSuccess { product ->
                    _product.value = product.toUiModel(DEFAULT_PRODUCT_COUNT)
                    saveRecentProduct(product)
                }
                .onFailure {
                    _error.value = Event(DetailError.ProductItemsNotFound)
                }
        }
    }

    private fun saveRecentProduct(product: Product) {
        viewModelScope.launch {
            recentRepository.add(product)
        }
    }

    private fun loadLastProduct() {
        viewModelScope.launch {
            recentRepository.loadMostRecent()
                .map { it?.toUiModel() }
                .onSuccess { product ->
                    product?.let { _lastProduct.value = it }
                }.onFailure {
                    _error.value = Event(DetailError.RecentItemNotFound)
                }
        }
    }

    override fun addProductToCart() {
        viewModelScope.launch {
            cartRepository.loadAll()
                .onSuccess { carts ->
                    val cartItem = carts.find { it.product.id == productId }
                    addCartItem(cartItem)
                }
                .onFailure {
                    _error.value = Event(DetailError.CartItemNotFound)
                }
        }
    }

    private fun addCartItem(cartItem: Cart?) {
        val product = product.value ?: return

        if (cartItem == null) {
            saveNewCartItem(product)
        } else {
            updateExistCartItem(cartItem, product)
        }
    }

    private fun saveNewCartItem(product: ProductModel) {
        viewModelScope.launch {
            cartRepository.saveNewCartItem(productId, product.quantity)
                .onSuccess {
                    _navigationEvent.value = Event(FromDetailToScreen.ShoppingWithUpdated(productId, product.quantity))
                }
                .onFailure {
                    _error.value = Event(DetailError.CartItemNotFound)
                }
        }
    }

    private fun updateExistCartItem(
        cartItem: Cart,
        product: ProductModel,
    ) {
        val newQuantity = cartItem.quantity + product.quantity
        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(cartItem.cartId, newQuantity)
                .onSuccess { _navigationEvent.value = Event(FromDetailToScreen.ShoppingWithUpdated(productId, newQuantity)) }
                .onFailure { _error.value = Event(DetailError.CartItemNotFound) }
        }
    }

    override fun navigateToDetailWithRecentViewed(productId: Long) {
        _navigationEvent.value = Event(FromDetailToScreen.ProductDetail(productId))
    }

    override fun navigateToBack() {
        _navigationEvent.value = Event(FromDetailToScreen.Shopping)
    }

    override fun increaseQuantity(productId: Long) {
        val updatedQuantity = product.value?.quantity?.plus(1) ?: 1
        _product.value = product.value?.copy(quantity = updatedQuantity)
    }

    override fun decreaseQuantity(productId: Long) {
        val updatedQuantity = product.value?.quantity?.minus(1) ?: 1
        if (updatedQuantity < 1) return
        _product.value = product.value?.copy(quantity = updatedQuantity)
    }

    companion object {
        const val DEFAULT_PRODUCT_COUNT = 1

        class Factory(
            private val productRepository: ProductRepository,
            private val cartRepository: CartRepository,
            private val recentRepository: RecentRepository,
            private val productId: Long,
            private val isLastViewedItem: Boolean,
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductDetailViewModel(
                    productRepository,
                    cartRepository,
                    recentRepository,
                    productId,
                    isLastViewedItem,
                ) as T
            }
        }
    }
}
