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
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.toDomain

class GoodsViewModel(
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _goodsItems = MutableLiveData(GoodsItems())
    val goodsItems: LiveData<GoodsItems> get() = _goodsItems

    private val _totalQuantity = MutableLiveData(0)
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private val _navigateToCart = MutableSingleLiveData<Int>()
    val navigateToCart: SingleLiveData<Int> get() = _navigateToCart

    private val _isSuccess = MutableSingleLiveData<Unit>()
    val isSuccess: SingleLiveData<Unit> get() = _isSuccess

    private val _isFail = MutableSingleLiveData<Unit>()
    val isFail: SingleLiveData<Unit> get() = _isFail

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var page: Int = INITIAL_PAGE

    init {
        viewModelScope.launch {
            loadHistory()
            loadProducts()
            fetchCartCounts()
        }
    }

    private suspend fun loadHistory() {
        val history =
            withContext(Dispatchers.IO) {
                historyRepository.getAll()
            }
        val currentGoodsState = _goodsItems.value ?: GoodsItems()
        _goodsItems.value = currentGoodsState.copy(historyItems = history)
    }

    private suspend fun loadProducts() {
        _isLoading.value = true
        val currentList = _goodsItems.value

        val cartsResult = cartRepository.fetchAllCart()
        val productsResult = productRepository.fetchProducts(page++)

        if (productsResult.isSuccess && cartsResult.isSuccess) {
            val products = productsResult.getOrNull() ?: return
            val carts = cartsResult.getOrNull() ?: return

            val goodsItems = matchCartQuantity(products.toDomain(), carts.toDomain())
            val newGoodsItems = currentList?.goodsItems.orEmpty() + goodsItems

            _goodsItems.value =
                currentList?.copy(
                    goodsItems = newGoodsItems,
                    hasNextPage = products.last.not(),
                )
        } else {
            if (productsResult.isFailure) {
                Log.e("loadProducts", "상품 API 실패", productsResult.exceptionOrNull())
            }
            if (cartsResult.isFailure) {
                Log.e("loadProducts", "장바구니 API 실패", cartsResult.exceptionOrNull())
            }
        }
        _isLoading.value = false
    }

    fun addPage() {
        page++
        viewModelScope.launch { loadProducts() }
    }

    fun addToCart(productId: Int) {
        viewModelScope.launch {
            val currentItems = _goodsItems.value?.goodsItems ?: return@launch

            cartRepository
                .addToCart(CartRequest(productId, 1))
                .onSuccess { newCartId ->
                    val modifiedIndex = currentItems.indexOfFirst { it.product.id == productId }
                    val modifiedItem = currentItems[modifiedIndex]
                    val newList = currentItems.toMutableList()
                    newList[modifiedIndex] = modifiedItem.copy(cartId = newCartId, quantity = 1)

                    _goodsItems.value = _goodsItems.value?.copy(goodsItems = newList)

                    fetchCartCounts()
                }.onFailure {
                    _isFail.setValue(Unit)
                }
        }
    }

    fun increaseQuantity(item: GoodsProduct) {
        val cartId = item.cartId ?: 0L
        val newQuantity = item.quantity + 1

        viewModelScope.launch {
            updateCartQuantity(cartId, newQuantity, item)
        }
    }

    fun decreaseQuantity(item: GoodsProduct) {
        val cartId = item.cartId ?: 0L
        val newQuantity = item.quantity - 1

        viewModelScope.launch {
            if (newQuantity == 0) {
                deleteCart(cartId, item)
            } else {
                updateCartQuantity(cartId, newQuantity, item)
            }
            fetchCartCounts()
        }
    }

    private fun deleteCart(
        cartId: Long,
        item: GoodsProduct,
    ) {
        val newItems = _goodsItems.value?.goodsItems?.toMutableList() ?: mutableListOf()

        viewModelScope.launch {
            cartRepository
                .deleteCart(cartId)
                .onSuccess {
                    val updatedCart = item.copy(cartId = null, quantity = 0)
                    val modifiedIndex = newItems.indexOfFirst { it.cartId == cartId }
                    newItems[modifiedIndex] = updatedCart

                    _goodsItems.value = _goodsItems.value?.copy(goodsItems = newItems)
                }.onFailure { error ->
                    _isFail.setValue(Unit)
                }
        }
    }

    private suspend fun updateCartQuantity(
        cartId: Long,
        newQuantity: Int,
        item: GoodsProduct,
    ) {
        val newItems = _goodsItems.value?.goodsItems?.toMutableList() ?: mutableListOf()

        cartRepository
            .updateCart(
                id = cartId,
                cartQuantity = CartQuantity(newQuantity),
            ).onSuccess {
                val updatedCart = item.copy(quantity = newQuantity)
                val modifiedIndex = newItems.indexOfFirst { it.cartId == cartId }
                newItems[modifiedIndex] = updatedCart

                _goodsItems.value = _goodsItems.value?.copy(goodsItems = newItems)
            }.onFailure {
                _isFail.setValue(Unit)
            }
        fetchCartCounts()
    }

    private fun matchCartQuantity(
        products: List<Product>,
        carts: List<Cart>,
    ): List<GoodsProduct> =
        products.map {
            val cart = carts.find { cart -> cart.product.id == it.id }
            val result =
                if (cart == null) {
                    GoodsProduct(product = it)
                } else {
                    GoodsProduct(cart.id, it, cart.quantity)
                }
            result
        }

    private suspend fun fetchQuantity() {
        val currentItems = _goodsItems.value?.goodsItems ?: return

        cartRepository
            .fetchAllCart()
            .onSuccess { response ->
                val carts = response?.toDomain() ?: return
                val result =
                    currentItems
                        .map {
                            val cart = carts.find { cart -> cart.id == it.cartId }
                            if (cart != null) {
                                it.copy(quantity = cart.quantity)
                            } else {
                                it.copy(cartId = null, quantity = 0)
                            }
                        }

                _goodsItems.value = _goodsItems.value?.copy(goodsItems = result)
            }.onFailure {}
    }

    fun findCartFromHistory(productId: Int) {
        _navigateToCart.setValue(productId)
    }

    private fun fetchCartCounts() {
        viewModelScope.launch {
            cartRepository
                .getCartCounts()
                .onSuccess { totalCount ->
                    _totalQuantity.value = totalCount.quantity
                }.onFailure {
                    Log.e("GoodsViewModel", "장바구니 개수 조회 실패", it)
                }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            fetchQuantity()
            loadHistory()
            fetchCartCounts()
        }
    }

    companion object {
        private const val INITIAL_PAGE = 0
    }
}
