package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.local.LocalCartDataSourceImpl
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.data.local.AppDatabase
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recent.RecentProductRepositoryImpl
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.ui.model.toUiModel
import woowacourse.shopping.presentation.util.Event
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

    init {
        fetchInitialData()
        if (!isLastViewedItem) loadLastProduct()
    }

    private fun fetchInitialData() {
        productRepository.loadById(
            productId,
            onSuccess = { product ->
                _product.value = product.toUiModel(DEFAULT_PRODUCT_COUNT)
                saveRecentProduct(product)
            },
            onFailure = {
                _error.value = Event(DetailError.ProductItemsNotFound)
            },
        )
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
        cartRepository.getCount(
            onSuccess = { count -> findCartItem(count) },
            onFailure = { _error.value = Event(DetailError.CartItemNotFound) },
        )
    }

    private fun findCartItem(pageSize: Int) {
        cartRepository.load(
            0,
            pageSize,
            onSuccess = { carts, _ ->
                val cartItem = carts.find { it.product.id == productId }
                saveCartItem(cartItem)
            },
            onFailure = {
                _error.value = Event(DetailError.CartItemNotFound)
            },
        )
    }

    private fun saveCartItem(cartItem: Cart?) {
        val product = product.value ?: return

        if (cartItem == null) {
            cartRepository.saveNewCartItem(
                productId,
                product.quantity,
                onSuccess = { _, newQuantity ->
                    _moveEvent.value =
                        Event(FromDetailToScreen.ShoppingWithUpdated(productId, newQuantity))
                },
                onFailure = {
                    _error.postValue(Event(DetailError.CartItemNotFound))
                },
            )
        } else {
            val newQuantity = cartItem.quantity + product.quantity
            cartRepository.updateCartItemQuantity(
                cartItem.cartId,
                newQuantity,
                onSuccess = { _, savedQuantity ->
                    _moveEvent.value =
                        Event(FromDetailToScreen.ShoppingWithUpdated(productId, savedQuantity))
                },
                onFailure = {
                    _error.postValue(Event(DetailError.CartItemNotFound))
                },
            )
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
                        localCartDataSource = LocalCartDataSourceImpl(cartDao),
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
