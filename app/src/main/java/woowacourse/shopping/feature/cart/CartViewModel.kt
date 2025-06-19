package woowacourse.shopping.feature.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.data.remote.product.ProductResponse
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.feature.cart.adapter.CartGoodsItem
import woowacourse.shopping.util.toDomain
import woowacourse.shopping.util.updateQuantity

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    private var currentPage: Int = 0

    private val _showPageButton = MutableLiveData(false)
    val showPageButton: LiveData<Boolean> get() = _showPageButton

    private val _isLeftPageEnable = MutableLiveData(false)
    val isLeftPageEnable: LiveData<Boolean> get() = _isLeftPageEnable

    private val _isRightPageEnable = MutableLiveData(false)
    val isRightPageEnable: LiveData<Boolean> get() = _isRightPageEnable

    private val _page = MutableLiveData(currentPage)
    val page: LiveData<Int> get() = _page

    private val _carts = MutableLiveData<List<CartGoodsItem>>()
    val carts: LiveData<List<CartGoodsItem>> get() = _carts

    private val _totalCheckedItemsCount = MutableLiveData(0)
    val totalCheckedItemsCount: LiveData<Int> get() = _totalCheckedItemsCount

    private val _checkedItemsPrice = MutableLiveData(0)
    val checkedItemsPrice: LiveData<Int> get() = _checkedItemsPrice

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _recommendItems = MutableLiveData<List<Cart>>()
    val recommendItems: LiveData<List<Cart>> get() = _recommendItems

    private val selectedItems = MutableLiveData<List<Cart>>(emptyList())
    private val totalItemsCount = MutableLiveData(0)

    init {
        fetchTotalItemsCount()
        loadCarts()
        loadProductsByCategory()
    }

    fun plusPage() {
        currentPage++
        _page.value = currentPage
        loadCarts()
    }

    fun minusPage() {
        currentPage--
        _page.value = currentPage
        loadCarts()
    }

    fun toggleCheck(cart: Cart) {
        val currentList = selectedItems.value ?: emptyList()
        val isCurrentlySelected = currentList.contains(cart)

        selectedItems.value =
            if (isCurrentlySelected) {
                currentList - cart
            } else {
                currentList + cart
            }

        updateCheckedItemsInfo(cart, !isCurrentlySelected)
    }

    private fun updateCheckedItemsInfo(
        cart: Cart,
        isAdding: Boolean,
    ) {
        val multiplier = if (isAdding) 1 else -1
        val quantityChange = cart.quantity * multiplier
        val priceChange = cart.product.price * cart.quantity * multiplier

        _totalCheckedItemsCount.value = (_totalCheckedItemsCount.value ?: 0) + quantityChange
        _checkedItemsPrice.value = (_checkedItemsPrice.value ?: 0) + priceChange
    }

    fun toggleAllItemsChecked() {
        val currentList = carts.value ?: emptyList()
        val newState = currentList.map { it.copy(isChecked = !it.isChecked) }
        _carts.value = newState
    }

    fun updatePageButtonStates(
        first: Boolean,
        last: Boolean,
        total: Int,
    ) {
        _isLeftPageEnable.value = !first
        _isRightPageEnable.value = !last
        _showPageButton.value = total > PAGE_SIZE
    }

    fun delete(cart: Cart) {
        adjustPageIfNeeded()
        viewModelScope.launch {
            cartRepository
                .deleteCart(cart.id)
                .onSuccess {
                    removeCartFromList(cart)
                    fetchTotalItemsCount()
                }.onFailure {
                    Log.e("CartViewModel", "장바구니 삭제 실패")
                }
        }
    }

    private fun adjustPageIfNeeded() {
        val total = totalItemsCount.value ?: 0
        val endPage = ((total - 1) / PAGE_SIZE) + 1

        if (currentPage == endPage && (total - 1) == ((currentPage - 1) * PAGE_SIZE)) {
            currentPage--
            _page.value = currentPage
        }
    }

    private fun removeCartFromList(cart: Cart) {
        val updatedList = _carts.value?.filter { it.cart.id != cart.id } ?: emptyList()
        _carts.value = updatedList
    }

    fun addToCart(cart: Cart) = updateCartQuantity(cart, cart.quantity + 1)

    fun removeFromCart(cart: Cart) {
        if (cart.quantity == 1) {
            delete(cart)
        } else {
            updateCartQuantity(cart, cart.quantity - 1)
        }
    }

    private fun updateCartQuantity(
        cart: Cart,
        newQuantity: Int,
    ) {
        viewModelScope.launch {
            cartRepository
                .updateCart(cart.id, CartQuantity(newQuantity))
                .onSuccess {
                    updateCartInList(cart, newQuantity)
                    fetchTotalItemsCount()
                }.onFailure {
                    Log.e("CartViewModel", "장바구니 수량 업데이트 실패")
                }
        }
    }

    private fun updateCartInList(
        cart: Cart,
        newQuantity: Int,
    ) {
        val updatedList =
            _carts.value?.map { cartItem ->
                if (cartItem.cart.product.id == cart.product.id) {
                    val updatedCart = cartItem.cart.updateQuantity(newQuantity)
                    cartItem.copy(cart = updatedCart)
                } else {
                    cartItem
                }
            } ?: emptyList()
        _carts.value = updatedList
    }

    private fun fetchTotalItemsCount() {
        viewModelScope.launch {
            cartRepository
                .getCartCounts()
                .onSuccess { count ->
                    totalItemsCount.value = count.quantity.toInt()
                }.onFailure {
                    Log.e("loadProductsInRange", "API 요청 실패", it)
                }
        }
    }

    fun loadCarts() {
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository
                .fetchCart(currentPage, PAGE_SIZE)
                .onSuccess { response ->
                    response?.let {
                        val cartList =
                            it.content.map { cartItem ->
                                CartGoodsItem(
                                    Cart(
                                        id = cartItem.id,
                                        product = cartItem.product.toDomain(),
                                        quantity = cartItem.quantity,
                                    ),
                                )
                            }
                        _carts.value = cartList
                        _page.value = it.number
                        updatePageButtonStates(it.first, it.last, it.totalElements.toInt())
                    }
                }.onFailure { e ->
                    Log.e("CartViewModel", "장바구니 로딩 실패", e)
                }
            _isLoading.value = false
        }
    }

    fun loadProductsByCategory() {
        viewModelScope.launch {
            val latestProductId = historyRepository.findLatest()?.id ?: 0

            val cartResult = cartRepository.fetchAllCart()
            val productResult = productRepository.fetchAllProducts()

            if (cartResult.isSuccess && productResult.isSuccess) {
                val cartProducts = cartResult.getOrNull()?.content ?: emptyList()
                val cartProductIds = cartProducts.map { it.product.id }
                val allProducts = productResult.getOrNull()?.content ?: emptyList()

                generateRecommendProducts(
                    lastProductId = latestProductId,
                    allProducts = allProducts,
                    cartProductIds = cartProductIds,
                )
            } else {
                cartResult.exceptionOrNull()?.let { Log.e("CartViewModel", "장바구니 전체 조회 실패", it) }
                productResult.exceptionOrNull()?.let { Log.e("CartViewModel", "전체 상품 조회 실패", it) }
            }
        }
    }

    private fun generateRecommendProducts(
        lastProductId: Int,
        allProducts: List<ProductResponse.Content>,
        cartProductIds: List<Long>,
    ) {
        val matchedProduct = allProducts.find { it.id.toInt() == lastProductId }
        val category = matchedProduct?.category

        val recommendProducts =
            allProducts
                .filter {
                    it.category == category &&
                        it.id.toInt() != lastProductId &&
                        !cartProductIds.contains(it.id)
                }.take(10)

        _recommendItems.value =
            recommendProducts.map {
                Cart(id = 0, product = it.toDomain(), quantity = 0)
            }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
