package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.local.database.AppDatabase
import woowacourse.shopping.local.datasource.LocalRecentViewedDataSourceImpl
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.ui.model.toUiModel
import woowacourse.shopping.presentation.util.Event
import woowacourse.shopping.remote.datasource.RemoteCartDataSourceImpl
import woowacourse.shopping.remote.datasource.RemoteProductDataSourceImpl

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

    private val _moveEvent = MutableLiveData<Event<FromDetailToScreen>>()
    val moveEvent: LiveData<Event<FromDetailToScreen>> get() = _moveEvent

    val showLastProduct: LiveData<Boolean> = lastProduct.map { it != ProductModel.INVALID_PRODUCT_MODEL && it.id != productId }

    init {
        fetchInitialData()
        if (!isLastViewedItem) loadLastProduct()
    }

    private fun fetchInitialData() {
        productRepository.loadById(productId) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val product = result.data
                    _product.value = product.toUiModel(DEFAULT_PRODUCT_COUNT)
                    saveRecentProduct(product)
                }
                is NetworkResult.Error -> {
                    _error.value = Event(DetailError.ProductItemsNotFound)
                }
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
                .onSuccess { product ->
                    product?.let {
                        _lastProduct.value = it.toUiModel()
                    }
                }.onFailure {
                    _error.value = Event(DetailError.RecentItemNotFound)
                }
        }
    }

    override fun onAddCartClick() {
        viewModelScope.launch {
            cartRepository.getCount()
                .onSuccess { cartCount ->
                    findCartItem(cartCount)
                }
                .onFailure {
                    _error.value = Event(DetailError.CartItemNotFound)
                }
        }
    }

    private fun findCartItem(pageSize: Int) {
        viewModelScope.launch {
            cartRepository.load(0, pageSize)
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
                    _moveEvent.value = Event(FromDetailToScreen.ShoppingWithUpdated(productId, product.quantity))
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
                .onSuccess { _moveEvent.value = Event(FromDetailToScreen.ShoppingWithUpdated(productId, newQuantity)) }
                .onFailure { _error.value = Event(DetailError.CartItemNotFound) }
        }
    }

    override fun onLastProductClick(productId: Long) {
        _moveEvent.value = Event(FromDetailToScreen.ProductDetail(productId))
    }

    override fun onCloseClick() {
        _moveEvent.value = Event(FromDetailToScreen.Shopping)
    }

    override fun onIncreaseQuantity(productId: Long) {
        val updatedQuantity = product.value?.quantity?.plus(1) ?: 1
        _product.value = product.value?.copy(quantity = updatedQuantity)
    }

    override fun onDecreaseQuantity(productId: Long) {
        val updatedQuantity = product.value?.quantity?.minus(1) ?: 1
        if (updatedQuantity < 1) return
        _product.value = product.value?.copy(quantity = updatedQuantity)
    }

    companion object {
        const val DEFAULT_PRODUCT_COUNT = 1

        class Factory(private val productId: Long, private val isLastViewedItem: Boolean) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                return ProductDetailViewModel(
                    ProductRepositoryImpl(RemoteProductDataSourceImpl()),
                    CartRepositoryImpl(remoteCartDataSource = RemoteCartDataSourceImpl()),
                    RecentProductRepositoryImpl(LocalRecentViewedDataSourceImpl(recentDao)),
                    productId,
                    isLastViewedItem,
                ) as T
            }
        }
    }
}
