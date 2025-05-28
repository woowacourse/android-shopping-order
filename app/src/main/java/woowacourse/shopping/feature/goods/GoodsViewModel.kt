package woowacourse.shopping.feature.goods

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.cart.repository.LocalCartRepository
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.toDomain
import woowacourse.shopping.util.updateCartQuantity

class GoodsViewModel(
    private val localCartRepository: LocalCartRepository,
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _items = MutableLiveData<List<Any>>()
    val items: LiveData<List<Any>> get() = _items
    private val _products = MutableLiveData<List<Cart>>()
    private val _histories = MutableLiveData<List<Cart>>()
    private val _totalQuantity = MutableLiveData(0)
    val totalQuantity: LiveData<Int> get() = _totalQuantity
    private val _hasNextPage = MutableLiveData(true)
    val hasNextPage: LiveData<Boolean> get() = _hasNextPage
    private val _navigateToCart = MutableSingleLiveData<Cart>()
    val navigateToCart: SingleLiveData<Cart> get() = _navigateToCart
    private val _isSuccess = MutableSingleLiveData<Unit>()
    val isSuccess: SingleLiveData<Unit> get() = _isSuccess
    private val _isFail = MutableSingleLiveData<Unit>()
    val isFail: SingleLiveData<Unit> get() = _isFail
    private var page: Int = INITIAL_PAGE

    init {
        loadProducts()
        loadHistories()
    }

    fun addPage() {
        page++
    }

    fun addToCart(cart: Cart) {
        val cartRequest = CartRequest(
            productId = cart.product.id,
            quantity = cart.quantity + 1
        )

        cartRepository.addToCart(cartRequest) { result ->
            result.onSuccess {
                _isSuccess.setValue(Unit)
            }.onFailure { error ->
                _isFail.setValue(Unit)
            }
        }
    }

    fun removeFromCart(cart: Cart) {
        viewModelScope.launch {
            localCartRepository.delete(cart)
            updateItemsAndTotalQuantity(cart, cart.quantity - 1)
        }
    }

    fun refreshHistoryOnly() {
        historyRepository.getAll { histories ->
            val currentItems = _items.value.orEmpty().toMutableList()

            val updatedItems =
                if (currentItems.firstOrNull() is List<*>) {
                    val cartsOnly = currentItems.drop(1)
                    listOf(histories) + cartsOnly
                } else {
                    listOf(histories) + currentItems
                }

            _items.postValue(updatedItems)
        }
    }

    fun findCartFromHistory(cart: Cart) {
        val cart =
            _items.value
                ?.filterIsInstance<Cart>()
                ?.find { it.product.id == cart.product.id }
        if (cart != null) {
            _navigateToCart.setValue(cart)
        }
    }

    private fun updateItemsAndTotalQuantity(
        updatedCart: Cart,
        newQuantity: Int,
    ) {
        val updatedItems =
            _items.value?.updateCartQuantity(updatedCart.product.id, newQuantity) ?: listOf(
                updatedCart
            )
        _items.value = updatedItems
        val total = updatedItems.filterIsInstance<Cart>().sumOf { it.quantity }
        _totalQuantity.value = total
    }

//    private fun loadItems() {
//        val currentItems = _items.value.orEmpty()
//
//        historyRepository.getAll { histories ->
//            cartRepository.getAll { cartsResult ->
//                val newGoods = getProducts(page)
//
//                val hasMore = (page + 1) * PAGE_SIZE < dummyGoods.size
//                _hasNextPage.postValue(hasMore)
//
//                val updatedCarts =
//                    newGoods.map { goods ->
//                        val quantity = cartsResult.carts.find { it.product.id == goods.id }?.quantity ?: 0
//                        Cart(goods = goods, quantity = quantity)
//                    }
//
//                val combinedItems: MutableList<Any> = mutableListOf()
//                if (page == INITIAL_PAGE) combinedItems.add(histories)
//                combinedItems.addAll(updatedCarts)
//
//                _items.postValue(currentItems + combinedItems)
//
//                val total = combinedItems.filterIsInstance<Cart>().sumOf { it.quantity }
//                _totalQuantity.postValue(total)
//            }
//        }
//    }

    fun updateItemQuantity(
        id: Int,
        quantity: Int,
    ) {
        val currentItems = _items.value.orEmpty().toMutableList()

        val index = currentItems.indexOfFirst { it is Cart && it.product.id == id }

        if (index != -1) {
            val oldItem = currentItems[index] as Cart
            val updatedItem = oldItem.copy(quantity = quantity)

            currentItems[index] = updatedItem
            _items.value = currentItems

            val total = currentItems.filterIsInstance<Cart>().sumOf { it.quantity }
            _totalQuantity.value = total
        }
    }

    private fun loadProducts() {
        productRepository.fetchProducts(
            onSuccess = { productList ->
                val carts = productList.map { product ->
                    Cart(product = product.toDomain(), quantity = 0)
                }
                _products.value = carts
                Log.d("loadProductsInRange", "$carts")
                refreshItems()
            },
            onError = { Log.e("loadProductsInRange", "API 요청 실패", it) }
        )
    }

    private fun loadHistories() {
        historyRepository.getAll { histories ->
            _histories.postValue(histories)
            refreshItems()
        }
    }

    private fun refreshItems() {
        val historyList = _histories.value.orEmpty()
        val productList = _products.value.orEmpty()

        val combined: MutableList<Any> = mutableListOf()
        if (historyList.isNotEmpty()) combined.add(historyList)
        combined.addAll(productList)

        _items.postValue(combined)
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val INITIAL_PAGE = 0
    }
}
