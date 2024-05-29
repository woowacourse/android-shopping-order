package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private lateinit var shoppingProductItem: ProductListItem.ShoppingProductItem

    private val _error = MutableLiveData<Event<DetailError>>()
    val error: LiveData<Event<DetailError>> get() = _error

    private val _moveEvent = MutableLiveData<Event<FromDetailToScreen>>()
    val moveEvent: LiveData<Event<FromDetailToScreen>> get() = _moveEvent

    private var cartId: Long = -1

    fun fetchInitialData(
        initialCartId: Long,
        initialQuantity: Int,
        productId: Long,
    ) {
        cartId = initialCartId
        productRepository.loadById(
            productId,
            onSuccess = {
                _product.value =
                    ProductListItem.ShoppingProductItem(
                        cartId = initialCartId,
                        id = productId,
                        it.name,
                        it.imgUrl,
                        it.price,
                        initialQuantity,
                    )
                addRecentProduct(it)
            },
            onFailure = {
                _error.value =
                    Event(DetailError.ProductItemsNotFound)
            },
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
                ),
            )
        }.join()
    }

    override fun onAddCartClick() {
        cartRepository.updateIncrementQuantity(
            cartId = cartId,
            productId = product.value!!.id,
            incrementAmount = product.value!!.quantity,
            quantity = 0,
            onSuccess = { _, quantity ->
                _moveEvent.value =
                    Event(
                        FromDetailToScreen.Shopping(
                            shoppingProductItem.id,
                            quantity,
                        ),
                    )
            },
            onFailure = {
                _error.postValue(Event(DetailError.CartItemNotFound))
            },
        )
    }

    override fun onLastProductClick(product: RecentProductItem) {
        _moveEvent.value = Event(FromDetailToScreen.ProductDetail(product.productId))
    }

    override fun onDecreaseQuantity(item: ProductListItem.ShoppingProductItem?) {
        val updatedQuantity = item?.let { it.quantity - 1 } ?: 1
        if (updatedQuantity > 0) {
            val updatedItem = item?.copy(quantity = updatedQuantity)
            _product.value = updatedItem
        }
    }

    override fun onIncreaseQuantity(item: ProductListItem.ShoppingProductItem?) {
        val updatedItem =
            item?.let {
                it.copy(quantity = it.quantity + 1)
            }
        _product.value = updatedItem
    }
}
