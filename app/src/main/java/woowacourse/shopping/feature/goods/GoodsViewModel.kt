package woowacourse.shopping.feature.goods

import android.os.Handler
import android.os.Looper.getMainLooper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.feature.model.GoodsItem
import woowacourse.shopping.feature.model.State
import woowacourse.shopping.util.Event
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.toDomain

class GoodsViewModel(
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _items = MutableLiveData<List<GoodsItem>>()
    val items: LiveData<List<GoodsItem>> get() = _items

    private val _totalQuantity = MutableLiveData(0)
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private val _hasNextPage = MutableLiveData(true)
    val hasNextPage: LiveData<Boolean> get() = _hasNextPage

    private val _navigateToCartProduct = MutableSingleLiveData<CartProduct>()
    val navigateToCartProduct: SingleLiveData<CartProduct> get() = _navigateToCartProduct

    private val _insertState = MutableLiveData<Event<State>>()
    val insertState: LiveData<Event<State>> get() = _insertState

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var page: Int = INITIAL_PAGE
    private val products: MutableList<CartProduct> = mutableListOf()
    private val histories: MutableList<History> = mutableListOf()

    init {
        loadProducts()
        loadHistories()
        getCartCounts()
    }

    fun addPage() {
        page++
        loadProducts()
    }

    fun addToCart(cart: CartProduct) {
        val newQuantity = cart.quantity + 1

        if (cart.quantity == 0) {
            val cartRequest =
                CartRequest(
                    productId = cart.product.id,
                    quantity = newQuantity,
                )

            cartRepository.addToCart(cartRequest) { result ->
                result
                    .onSuccess { newCartId ->
                        val updatedCart = cart.copy(id = newCartId, quantity = newQuantity)

                        updateItems(updatedCart)
                        getCartCounts()
                        _insertState.value = Event(State.Success)
                    }.onFailure {
                        _insertState.value = Event(State.Failure)
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
                        _insertState.value = Event(State.Success)
                    }.onFailure { error ->
                        _insertState.value = Event(State.Failure)
                    }
            }
        }
    }

    fun removeFromCart(cart: CartProduct) {
        if (cart.quantity == 1) {
            cartRepository.deleteCart(cart.id) { result ->
                result
                    .onSuccess {
                        val updatedCart = cart.copy(id = 0, quantity = cart.quantity - 1)
                        updateItems(updatedCart)
                        getCartCounts()
                    }.onFailure { error ->
                        _insertState.value = Event(State.Failure)
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
                        _insertState.value = Event(State.Failure)
                    }
            }
        }
        getCartCounts()
    }

    fun refreshHistoryOnly() {
        historyRepository.getAll { historiesList ->
            val currentItems = _items.value.orEmpty().toMutableList()
            val cartsOnly = currentItems.filterIsInstance<GoodsItem.Product>()
            val updatedItems = mutableListOf<GoodsItem>()
            if (historiesList.isNotEmpty()) {
                updatedItems.add(GoodsItem.Recent(historiesList))
            }
            updatedItems.addAll(cartsOnly)
            _items.postValue(updatedItems)
        }
    }

    fun findCartFromHistory(history: History) {
        val product = _items.value?.filterIsInstance<GoodsItem.Product>()?.find { it.cart.product.id == history.id }
        if (product != null) {
            _navigateToCartProduct.setValue(product.cart)
        }
    }

    fun updateItem(
        cartId: Long,
        goodsId: Long,
        quantity: Int,
    ) {
        val currentItems = _items.value.orEmpty().toMutableList()

        val index = currentItems.indexOfFirst { it is GoodsItem.Product && it.cart.product.id == goodsId }

        if (index != -1) {
            val oldItem = currentItems[index] as GoodsItem.Product
            val updatedItem = oldItem.copy(cart = oldItem.cart.copy(id = cartId, quantity = quantity))

            currentItems[index] = updatedItem
            _items.value = currentItems

            val total = currentItems.filterIsInstance<GoodsItem.Product>().sumOf { it.cart.quantity }
            _totalQuantity.value = total
        }
    }

    private fun updateItems(updatedCart: CartProduct) {
        val currentItems = _items.value.orEmpty().toMutableList()
        val index =
            currentItems.indexOfFirst {
                it is GoodsItem.Product && it.cart.product.id == updatedCart.product.id
            }

        if (index != -1) {
            val updatedItem = GoodsItem.Product(updatedCart)
            currentItems[index] = updatedItem
            _items.value = currentItems
        }
    }

    private fun loadProducts() {
        _isLoading.postValue(true)
        Handler(getMainLooper()).postDelayed({
            cartRepository.fetchAllCart(
                onSuccess = { cartList ->
                    val cartByProductId = cartList.content.associateBy { it.product.id }

                    viewModelScope.launch {
                        val response = productRepository.fetchProducts(page)

                        val newCarts =
                            response.content.map { product ->
                                val matchedCart = cartByProductId[product.id]
                                CartProduct(
                                    id = matchedCart?.id?.toLong() ?: 0,
                                    product = product.toDomain(),
                                    quantity = matchedCart?.quantity ?: 0,
                                )
                            }

                        val currentProducts = products
                        val combinedCarts = currentProducts + newCarts
                        products.clear()
                        products.addAll(combinedCarts)

                        _hasNextPage.value = !response.last

                        refreshItems()
                        _isLoading.postValue(false)
                    }
                },
                onError = {
                    Log.e("loadProductsInRange", "장바구니 API 실패", it)
                    _isLoading.postValue(false)
                },
            )
        }, 1000) // 스켈레톤 UI 테스트를 위한 딜레이입니다.
    }

    private fun loadHistories() {
        historyRepository.getAll { allHistories ->
            histories.clear()
            histories.addAll(allHistories)
            refreshItems()
        }
    }

    private fun refreshItems() {
        val historyList = histories
        val productList = products

        val combined: MutableList<GoodsItem> = mutableListOf()
        if (historyList.isNotEmpty()) {
            combined.add(GoodsItem.Recent(historyList))
        }
        combined.addAll(productList.map { GoodsItem.Product(it) })

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
