package woowacourse.shopping.feature.goods

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.replaceCartByProductId
import woowacourse.shopping.util.toDomain

class GoodsViewModel(
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _items = MutableLiveData<List<Any>>()
    val items: LiveData<List<Any>> get() = _items
    private val products = MutableLiveData<List<Cart>>()
    private val histories = MutableLiveData<List<Cart>>()
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
        getCartCounts()
    }

    fun addPage() {
        page++
        loadProducts()
    }

    fun addToCart(cart: Cart) {
        val newQuantity = cart.quantity + 1

        if (cart.quantity == 0) {
            val cartRequest =
                CartRequest(
                    productId = cart.product.id,
                    quantity = newQuantity,
                )

            cartRepository.addToCart(cartRequest) { result ->
                result
                    .onSuccess { response ->
                        val locationHeader = response.headers()["Location"]
                        val newId = locationHeader?.substringAfterLast("/")?.toLongOrNull() ?: 0
                        val updatedCart = cart.copy(id = newId, quantity = newQuantity)

                        updateItems(updatedCart)
                        getCartCounts()
                        _isSuccess.setValue(Unit)
                    }.onFailure {
                        _isFail.setValue(Unit)
                    }
            }
        } else {
            cartRepository.updateCart(
                id = cart.id,
                cartQuantity = CartQuantity(newQuantity),
            ) { result ->
                result
                    .onSuccess {
                        val updatedCart = cart.copy(quantity = newQuantity)
                        updateItems(updatedCart)
                        getCartCounts()
                        _isSuccess.setValue(Unit)
                    }.onFailure { error ->
                        _isFail.setValue(Unit)
                    }
            }
        }
    }

    fun removeFromCart(cart: Cart) {
        if (cart.quantity == 1) {
            cartRepository.deleteCart(cart.id) { result ->
                result
                    .onSuccess {
                        val updatedCart = cart.copy(id = 0, quantity = cart.quantity - 1)
                        updateItems(updatedCart)
                        getCartCounts()
                    }.onFailure { error ->
                        _isFail.setValue(Unit)
                    }
            }
        } else {
            cartRepository.updateCart(
                id = cart.id,
                cartQuantity = CartQuantity(cart.quantity - 1),
            ) { result ->
                result
                    .onSuccess {
                        val updatedCart = cart.copy(quantity = cart.quantity - 1)
                        updateItems(updatedCart)
                        getCartCounts()
                    }.onFailure { error ->
                        _isFail.setValue(Unit)
                    }
            }
        }
        getCartCounts()
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

    private fun updateItems(updatedCart: Cart) {
        val updatedItems =
            _items.value?.replaceCartByProductId(updatedCart) ?: listOf(updatedCart)
        _items.value = updatedItems
    }

    private fun loadProducts() {
        cartRepository.fetchAllCart(
            onSuccess = { cartList ->
                val cartByProductId = cartList.content.associateBy { it.product.id }

                productRepository.fetchProducts(
                    onSuccess = { response ->
                        val newCarts =
                            response.content.map { product ->
                                val matchedCart = cartByProductId[product.id]
                                Cart(
                                    id = matchedCart?.id?.toLong() ?: 0,
                                    product = product.toDomain(),
                                    quantity = matchedCart?.quantity ?: 0,
                                )
                            }

                        val currentProducts = products.value.orEmpty()
                        val combinedCarts = currentProducts + newCarts
                        products.value = combinedCarts

                        _hasNextPage.value = !response.last

                        refreshItems()
                    },
                    onError = { Log.e("loadProductsInRange", "상품 API 실패", it) },
                    page = page,
                )
            },
            onError = { Log.e("loadProductsInRange", "장바구니 API 실패", it) },
        )
    }

    private fun loadHistories() {
        historyRepository.getAll { allHistories ->
            histories.postValue(allHistories)
            refreshItems()
        }
    }

    private fun refreshItems() {
        val historyList = histories.value.orEmpty()
        val productList = products.value.orEmpty()

        val combined: MutableList<Any> = mutableListOf()
        if (historyList.isNotEmpty()) combined.add(historyList)
        combined.addAll(productList)

        _items.postValue(combined)
    }

    private fun getCartCounts() {
        cartRepository.getCartCounts(
            onSuccess = { totalCount ->
                _totalQuantity.value = totalCount.toInt()
            },
            onError = { Log.e("GoodsViewModel", "장바구니 개수 조회 실패", it) },
        )
    }

    companion object {
        private const val INITIAL_PAGE = 0
    }
}
