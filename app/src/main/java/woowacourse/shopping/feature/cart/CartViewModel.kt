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
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.toDomain

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

    private val _carts = MutableLiveData<List<CartProduct>>()
    val carts: LiveData<List<CartProduct>> get() = _carts

    private val _totalCheckedItemsCount = MutableLiveData(0)
    val totalCheckedItemsCount: LiveData<Int> get() = _totalCheckedItemsCount

    private val _checkedItemsPrice = MutableLiveData(0)
    val checkedItemsPrice: LiveData<Int> get() = _checkedItemsPrice

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _recommendItems = MutableLiveData<List<CartProduct>>()
    val recommendItems: LiveData<List<CartProduct>> get() = _recommendItems

    private val totalItemsCount = MutableLiveData(0)

    private val _orderItems = MutableSingleLiveData<List<Long>>()
    val orderItems: SingleLiveData<List<Long>> get() = _orderItems

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

    fun toggleCheck(
        cart: CartProduct,
        isRecommend: Boolean = false,
    ) {
        val targetList = if (isRecommend) _recommendItems else _carts
        val updatedList =
            targetList.value?.map {
                if (it.id == cart.id) it.copy(isChecked = !it.isChecked) else it
            } ?: return
        targetList.value = updatedList
        updateCheckedSummary(updatedList)
    }

    fun selectAllCarts() = setCartChecked(true)

    fun unselectAllCarts() = setCartChecked(false)

    fun updatePageButtonStates(
        first: Boolean,
        last: Boolean,
        total: Int,
    ) {
        _isLeftPageEnable.value = !first
        _isRightPageEnable.value = !last
        _showPageButton.value = total > PAGE_SIZE
    }

    fun deleteFromCart(cart: CartProduct) {
        val total = totalItemsCount.value ?: 0
        val endPage = ((total - 1) / PAGE_SIZE) + 1

        if (currentPage == endPage && (total - 1) == ((currentPage - 1) * PAGE_SIZE)) {
            currentPage--
            _page.value = currentPage
        }

        delete(cart)
    }

    fun addToCart(cart: CartProduct) = changeCartQuantity(cart, 1)

    fun removeFromCart(cart: CartProduct) = changeCartQuantity(cart, -1)

    fun addToRecommendCart(cart: CartProduct) {
        viewModelScope.launch {
            if (cart.quantity == 0) {
                val cartRequest = CartRequest(cart.product.id, 1)
                cartRepository
                    .addToCart(cartRequest)
                    .onSuccess { newId ->
                        val updatedCart = cart.copy(id = newId, quantity = 1)
                        updateCartItemList(_recommendItems, updatedCart)
                        toggleCheck(updatedCart, isRecommend = true)
                        fetchTotalItemsCount()
                    }.onFailure {
                        Log.e("addRecommend", "추천 상품 추가 실패", it)
                    }
            } else {
                changeCartQuantity(cart, 1, isRecommend = true)
                toggleCheck(cart)
            }
        }
    }

    fun removeFromRecommendCart(cart: CartProduct) {
        if (cart.quantity == 1) {
            delete(cart)
        } else {
            changeCartQuantity(cart, -1, isRecommend = true)
        }
    }

    fun loadProductsByCategory() {
        viewModelScope.launch {
            val latestHistory = historyRepository.findLatest()
            val response = cartRepository.fetchAllCart()
            val cartProductIds = response.content.map { it.product.id }
            val recommendedList = productRepository.fetchRecommendProducts(latestHistory.id, cartProductIds)
            _recommendItems.value = recommendedList
        }
    }

    fun orderItems() {
        val orderFromCarts = carts.value?.filter { it.isChecked }?.map { it.id } ?: emptyList()
        val orderFromRecommend = recommendItems.value?.filter { it.isChecked }?.map { it.id } ?: emptyList()
        _orderItems.postValue(orderFromCarts + orderFromRecommend)
    }

    private fun changeCartQuantity(
        cart: CartProduct,
        quantityChange: Int,
        isRecommend: Boolean = false,
    ) {
        val newQuantity = cart.quantity + quantityChange
        if (newQuantity <= 0) {
            delete(cart)
            return
        }

        viewModelScope.launch {
            cartRepository
                .updateCart(cart.id, CartQuantity(newQuantity))
                .onSuccess {
                    val updatedCart = cart.copy(quantity = newQuantity)
                    val targetList = if (isRecommend) _recommendItems else _carts
                    updateCartItemList(targetList, updatedCart)
                    fetchTotalItemsCount()
                }.onFailure {
                    Log.e("CartUpdate", "수량 변경 실패", it)
                }
        }
    }

    private fun delete(cart: CartProduct) {
        viewModelScope.launch {
            cartRepository
                .deleteCart(cart.id)
                .onSuccess {
                    _carts.value = _carts.value?.filter { it.id != cart.id }
                    fetchTotalItemsCount()
                }.onFailure {
                    Log.e("deleteCart", "장바구니 삭제 실패", it)
                }
        }
    }

    private fun updateCartItemList(
        list: MutableLiveData<List<CartProduct>>,
        updatedCart: CartProduct,
    ) {
        val currentList = list.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.product.id == updatedCart.product.id }
        if (index != -1) {
            currentList[index] = updatedCart
            list.value = currentList
        }
    }

    private fun updateCheckedSummary(updatedList: List<CartProduct>) {
        val checked = updatedList.filter { it.isChecked }
        _totalCheckedItemsCount.value = checked.sumOf { it.quantity }
        _checkedItemsPrice.value = checked.sumOf { it.product.price * it.quantity }
    }

    private fun fetchTotalItemsCount() {
        viewModelScope.launch {
            cartRepository
                .getCartCounts()
                .onSuccess { totalCount ->
                    totalItemsCount.postValue(totalCount.toInt())
                }.onFailure {
                    Log.e("TotalItemsCount", "API 요청 실패", it)
                }
        }
    }

    private fun loadCarts() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val response = cartRepository.fetchCart(page = currentPage)
            val cartList = response.content.map { it.toDomain() }
            _carts.postValue(cartList)
            _page.postValue(response.number)
            updatePageButtonStates(response.first, response.last, response.totalElements.toInt())
            _isLoading.postValue(false)
        }
    }

    private fun setCartChecked(checked: Boolean) {
        val updatedList = _carts.value?.map { it.copy(isChecked = checked) } ?: return
        _carts.value = updatedList
        updateCheckedSummary(updatedList)
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
