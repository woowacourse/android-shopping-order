package woowacourse.shopping.feature.goods

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

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

        viewModelScope.launch {
            if (cart.quantity == 0) {
                cartRepository
                    .addToCart(
                        CartRequest(
                            productId = cart.product.id,
                            quantity = newQuantity,
                        ),
                    ).onSuccess { newCartId ->
                        val updatedCart = cart.copy(id = newCartId, quantity = newQuantity)
                        updateItems(updatedCart)
                        getCartCounts()
                        _isSuccess.setValue(Unit)
                    }.onFailure {
                        _isFail.setValue(Unit)
                    }
            } else {
                cartRepository
                    .updateCart(
                        id = cart.id,
                        cartQuantity = CartQuantity(newQuantity),
                    ).onSuccess {
                        val updatedCart = cart.copy(quantity = newQuantity)
                        updateItems(updatedCart)
                        getCartCounts()
                        _isSuccess.setValue(Unit)
                    }.onFailure {
                        _isFail.setValue(Unit)
                    }
            }
        }
    }

    fun removeFromCart(cart: Cart) {
        viewModelScope.launch {
            if (cart.quantity == 1) {
                cartRepository
                    .deleteCart(cart.id)
                    .onSuccess {
                        val updatedCart = cart.copy(id = 0, quantity = cart.quantity - 1)
                        updateItems(updatedCart)
                        getCartCounts()
                    }.onFailure { error ->
                        _isFail.setValue(Unit)
                    }
            } else {
                cartRepository
                    .updateCart(
                        id = cart.id,
                        cartQuantity = CartQuantity(cart.quantity - 1),
                    ).onSuccess {
                        val updatedCart = cart.copy(quantity = cart.quantity - 1)
                        updateItems(updatedCart)
                        getCartCounts()
                    }.onFailure {
                        _isFail.setValue(Unit)
                    }
            }
        }
        getCartCounts()
    }

    // 수정 해야함
    fun refreshHistoryOnly() {
        viewModelScope.launch(Dispatchers.IO) {
            val histories = historyRepository.getAll()

            val currentItems = _items.value.orEmpty().toMutableList()

            val updatedItems =
                if (currentItems.firstOrNull() is List<*>) {
                    val cartsOnly = currentItems.drop(1)
                    listOf(histories) + cartsOnly
                } else {
                    listOf(histories) + currentItems
                }

            withContext(Dispatchers.Main) { _items.value = updatedItems }
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

    fun updateItem(
        cartId: Long,
        goodsId: Int,
        quantity: Int,
    ) {
        val currentItems = _items.value.orEmpty().toMutableList()

        val index = currentItems.indexOfFirst { it is Cart && it.product.id == goodsId }

        if (index != -1) {
            val oldItem = currentItems[index] as Cart
            val updatedItem = oldItem.copy(id = cartId, quantity = quantity)

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
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository
                .fetchAllCart()
                .onSuccess { cartList ->
                    cartList?.let {
                        val cartByProductId = it.content.associateBy { it.product.id }
                        productRepository
                            .fetchProducts(page = page)
                            .onSuccess { response ->

                                val newCarts =
                                    response?.content?.map { product ->
                                        val matchedCart = cartByProductId[product.id]
                                        Cart(
                                            id = matchedCart?.id?.toLong() ?: 0,
                                            product = product.toDomain(),
                                            quantity = matchedCart?.quantity ?: 0,
                                        )
                                    } ?: emptyList()

                                val currentProducts = products.value.orEmpty()
                                val combinedCarts = currentProducts + newCarts
                                products.value = combinedCarts

                                val hasLastPage = response?.last == true
                                _hasNextPage.value = !hasLastPage

                                refreshItems()
                                _isLoading.value = false
                            }.onFailure {
                                Log.e("loadProductsInRange", "상품 API 실패", it)
                                _isLoading.value = false
                            }
                        page = page
                    }
                }.onFailure {
                    Log.e("loadProductsInRange", "장바구니 API 실패", it)
                    _isLoading.value = false
                }
        }
    }

    private fun loadHistories() {
        viewModelScope.launch(Dispatchers.IO) {
            val allHistories = historyRepository.getAll()
            withContext(Dispatchers.Main) {
                histories.value = allHistories
            }
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
        viewModelScope.launch {
            cartRepository
                .getCartCounts()
                .onSuccess { totalCount ->
                    _totalQuantity.value = totalCount.quantity.toInt()
                }.onFailure {
                    Log.e("GoodsViewModel", "장바구니 개수 조회 실패", it)
                }
        }
    }

    companion object {
        private const val INITIAL_PAGE = 0
    }
}
