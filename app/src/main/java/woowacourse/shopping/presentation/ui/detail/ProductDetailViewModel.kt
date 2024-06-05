package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recent.RecentProductRepositoryImpl
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.local.database.AppDatabase
import woowacourse.shopping.local.datasource.LocalCartDataSource
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.ui.model.toUiModel
import woowacourse.shopping.presentation.util.Event
import woowacourse.shopping.remote.datasource.RemoteCartDataSource
import kotlin.concurrent.thread

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
        thread { recentRepository.add(product) }.join()
    }

    private fun loadLastProduct() {
        recentRepository.loadMostRecent()
            .onSuccess { product ->
                product?.let {
                    _lastProduct.value = it.toUiModel()
                }
            }.onFailure {
                _error.value = Event(DetailError.RecentItemNotFound)
            }
    }

    override fun onAddCartClick() {
        cartRepository.getCount { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val count = result.data
                    findCartItem(count)
                }
                is NetworkResult.Error -> {
                    _error.value = Event(DetailError.CartItemNotFound)
                }
            }
        }
    }

    private fun findCartItem(pageSize: Int) {
        cartRepository.load(0, pageSize) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val carts = result.data
                    val cartItem = carts.find { it.product.id == productId }
                    addCartItem(cartItem)
                }
                is NetworkResult.Error -> {
                    _error.value = Event(DetailError.CartItemNotFound)
                }
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
        cartRepository.saveNewCartItem(productId, product.quantity) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _moveEvent.value = Event(FromDetailToScreen.ShoppingWithUpdated(productId, product.quantity))
                }
                is NetworkResult.Error -> {
                    _error.value = Event(DetailError.CartItemNotFound)
                }
            }
        }
    }

    private fun updateExistCartItem(
        cartItem: Cart,
        product: ProductModel,
    ) {
        val newQuantity = cartItem.quantity + product.quantity
        cartRepository.updateCartItemQuantity(cartItem.cartId, newQuantity) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _moveEvent.value = Event(FromDetailToScreen.ShoppingWithUpdated(productId, newQuantity))
                }
                is NetworkResult.Error -> {
                    _error.value = Event(DetailError.CartItemNotFound)
                }
            }
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
                val cartDao = AppDatabase.instanceOrNull.cartDao()
                return ProductDetailViewModel(
                    ProductRepositoryImpl(),
                    CartRepositoryImpl(
                        localCartDataSource = LocalCartDataSource(cartDao),
                        remoteCartDataSource = RemoteCartDataSource(),
                    ),
                    RecentProductRepositoryImpl(recentDao),
                    productId,
                    isLastViewedItem,
                ) as T
            }
        }
    }
}
