package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.RecentProductItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.util.Event
import java.time.LocalDateTime
import kotlin.concurrent.thread

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentRepository: RecentRepository,
) : ViewModel(), DetailHandler {
    private val _product =
        MutableLiveData<ProductListItem.ShoppingProductItem>()
    val product: LiveData<ProductListItem.ShoppingProductItem> get() = _product

    private val _lastProduct = MutableLiveData<UiState<RecentProductItem>>()
    val lastProduct: LiveData<UiState<RecentProductItem>> get() = _lastProduct

    private val _error = MutableLiveData<Event<DetailError>>()
    val error: LiveData<Event<DetailError>> get() = _error

    private val _moveEvent = MutableLiveData<Event<FromDetailToScreen>>()
    val moveEvent: LiveData<Event<FromDetailToScreen>> get() = _moveEvent

    fun fetchInitialData(productId: Long) {
        var cart: Cart?
        cartRepository.loadById(
            productId,
            onSuccess = {
                cart = it
                val quantity = cart?.quantity ?: 0
                viewModelScope.launch {
                    productRepository.loadById(productId).onSuccess { product ->
                        _product.value =
                            ProductListItem.ShoppingProductItem(
                                id = productId,
                                name = product.name,
                                imgUrl = product.imgUrl,
                                price = product.price,
                                category = product.category,
                                quantity = quantity,
                            )
                        addRecentProduct(product)
                    }.onFailure {
                        _error.value = Event(DetailError.ProductItemsNotFound)
                    }
                }
            },
            onFailure = {},
        )
    }

    fun loadLastProduct() {
        recentRepository.loadMostRecent().onSuccess {
            it?.let {
                _lastProduct.value = UiState.Success(it)
            }
        }.onFailure {
            _error.value =
                Event(DetailError.RecentItemNotFound)
        }
    }

    private fun addRecentProduct(product: Product) {
        thread {
            recentRepository.add(
                RecentProductItem(
                    productId = product.id,
                    name = product.name,
                    imgUrl = product.imgUrl,
                    dateTime = LocalDateTime.now(),
                    category = product.category,
                ),
            )
        }.join()
    }

    override fun onAddCartClick() {
        product.value ?: run {
            return
        }
        cartRepository.setNewCartQuantity(
            productId = product.value!!.id,
            newQuantity = product.value!!.quantity,
            onSuccess = { _, quantity ->
                saveCartItem(product.value!!.id, quantity)
            },
            onFailure = {
                _error.value = Event(DetailError.CartItemNotFound)
            },
        )
    }

    private fun saveCartItem(
        productId: Long,
        quantity: Int,
    ) {
        _moveEvent.value =
            Event(
                FromDetailToScreen.Shopping(
                    productId,
                    quantity,
                ),
            )
    }

    override fun onLastProductClick(product: RecentProductItem) {
        _moveEvent.value = Event(FromDetailToScreen.ProductDetail(product.productId))
    }

    override fun onDecreaseQuantity(product: ProductListItem.ShoppingProductItem?) {
        product ?: return
        val updatedQuantity = product.quantity - 1
        if (updatedQuantity > 0) {
            _product.value = product.copy(quantity = updatedQuantity)
        }
    }

    override fun onIncreaseQuantity(product: ProductListItem.ShoppingProductItem?) {
        product ?: return
        val updatedItem =
            product.let {
                it.copy(quantity = it.quantity + 1)
            }
        _product.value = updatedItem
    }
}
