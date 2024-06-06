package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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
    private val _shoppingProduct =
        MutableLiveData<ProductListItem.ShoppingProductItem>()
    val shoppingProduct: LiveData<ProductListItem.ShoppingProductItem> get() = _shoppingProduct

    private val _lastProduct = MutableLiveData<UiState<RecentProductItem>>()
    val lastProduct: LiveData<UiState<RecentProductItem>> get() = _lastProduct

    private val _error = MutableLiveData<Event<DetailError>>()
    val error: LiveData<Event<DetailError>> get() = _error

    private val _moveEvent = MutableLiveData<Event<FromDetailToScreen>>()
    val moveEvent: LiveData<Event<FromDetailToScreen>> get() = _moveEvent

    fun fetchInitialData(productId: Long) {
        viewModelScope.launch {
            productRepository.loadById(productId).onSuccess { product ->
                addRecentProduct(product)
                _shoppingProduct.value =
                    ProductListItem.ShoppingProductItem(
                        id = product.id,
                        name = product.name,
                        imgUrl = product.imgUrl,
                        price = product.price,
                        category = product.category,
                        quantity = 0,
                    )
                fetchCartQuantity(product.id)
            }.onFailure {
                _error.value = Event(DetailError.ProductItemsNotFound)
            }
        }
    }

    private fun fetchCartQuantity(productId: Long) {
        viewModelScope.launch {
            cartRepository.loadById(productId).onSuccess { cart ->
                _shoppingProduct.value = shoppingProduct.value?.copy(quantity = cart.quantity)
            }
        }
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
        shoppingProduct.value ?: return
        viewModelScope.launch {
            val quantity = shoppingProduct.value!!.quantity
            cartRepository.setNewCartQuantity(
                productId = shoppingProduct.value!!.id,
                newQuantity = quantity,
            ).onSuccess {
                setEventToShoppingScreen(shoppingProduct.value!!.id, quantity)
            }.onFailure {
                _error.value = Event(DetailError.CartItemNotFound)
            }
        }
    }

    private fun setEventToShoppingScreen(
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
        val resultQuantity = product.quantity - 1
        if (resultQuantity > 0) {
            _shoppingProduct.value = product.copy(quantity = resultQuantity)
        }
    }

    override fun onIncreaseQuantity(product: ProductListItem.ShoppingProductItem?) {
        product ?: return
        val updatedProduct =
            product.let {
                it.copy(quantity = it.quantity + 1)
            }
        _shoppingProduct.value = updatedProduct
    }
}
