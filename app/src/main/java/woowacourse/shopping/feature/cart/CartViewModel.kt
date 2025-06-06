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
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.util.toDomain
import woowacourse.shopping.util.updateQuantity
import kotlin.text.toInt

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

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _recommendItems = MutableLiveData<List<CartProduct>>()
    val recommendItems: LiveData<List<CartProduct>> get() = _recommendItems

    private val totalItemsCount = MutableLiveData(0)

    init {
        fetchTotalItemsCount()
        viewModelScope.launch {
            loadCarts()
        }
        loadProductsByCategory()
    }

    fun plusPage() {
        currentPage++
        _page.value = currentPage
        viewModelScope.launch {
            loadCarts()
        }
    }

    fun minusPage() {
        currentPage--
        _page.value = currentPage
        viewModelScope.launch {
            loadCarts()
        }
    }

    fun toggleCheck(cart: CartProduct) {
        val updatedList =
            _carts.value?.map {
                if (it.id == cart.id) it.copy(isChecked = !it.isChecked) else it
            } ?: return
        _carts.value = updatedList
        updateCheckedSummary(updatedList)
    }

    fun selectAllCarts() {
        val updatedList = _carts.value?.map { it.copy(isChecked = true) } ?: return
        _carts.value = updatedList
        updateCheckedSummary(updatedList)
    }

    fun unselectAllCarts() {
        val updatedList = _carts.value?.map { it.copy(isChecked = false) } ?: return
        _carts.value = updatedList
        updateCheckedSummary(updatedList)
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

    fun delete(cart: CartProduct) {
        val total = totalItemsCount.value ?: 0
        val endPage = ((total - 1) / PAGE_SIZE) + 1

        if (currentPage == endPage && (total - 1) == ((currentPage - 1) * PAGE_SIZE)) {
            currentPage--
            _page.value = currentPage
        }

        viewModelScope.launch {
            cartRepository
                .deleteCart(cart.id)
                .onSuccess {
                    val updatedList =
                        _carts.value?.filter { it.id != cart.id } ?: emptyList()
                    _carts.postValue(updatedList)
                    fetchTotalItemsCount()
                }.onFailure { error ->
                    Log.e("deleteCart", "장바구니 삭제 실패", error)
                }
        }
    }

    fun addToCart(cart: CartProduct) {
        viewModelScope.launch {
            cartRepository
                .updateCart(id = cart.id, cartQuantity = CartQuantity(cart.quantity + 1))
                .onSuccess {
                    val updatedList =
                        _carts.value?.map {
                            if (it.product.id == cart.product.id) {
                                it.updateQuantity(it.quantity + 1)
                            } else {
                                it
                            }
                        } ?: emptyList()
                    _carts.postValue(updatedList)
                    fetchTotalItemsCount()
                }.onFailure { error ->
                    Log.e("addCart", "장바구니 추가 실패", error)
                }
        }
    }

    fun removeFromCart(cart: CartProduct) {
        if (cart.quantity == 1) {
            delete(cart)
        } else {
            viewModelScope.launch {
                cartRepository
                    .updateCart(
                        id = cart.id,
                        cartQuantity = CartQuantity(cart.quantity - 1),
                    ).onSuccess {
                        val updatedList =
                            _carts.value?.map {
                                if (it.product.id == cart.product.id) {
                                    it.updateQuantity(it.quantity - 1)
                                } else {
                                    it
                                }
                            } ?: emptyList()
                        _carts.postValue(updatedList)
                        fetchTotalItemsCount()
                    }.onFailure { error ->
                        Log.e("removeCart", "장바구니 삭제 실패", error)
                    }
            }
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

    private suspend fun loadCarts() {
        _isLoading.postValue(true)
        val response = cartRepository.fetchCart(page = currentPage)
        val cartList =
            response.content.map {
                CartProduct(
                    id = it.id,
                    product = it.product.toDomain(),
                    quantity = it.quantity,
                )
            }
        _carts.postValue(cartList)
        _page.postValue(response.number)
        updatePageButtonStates(
            response.first,
            response.last,
            response.totalElements.toInt(),
        )
        _isLoading.postValue(false)
    }

    fun loadProductsByCategory() {
        historyRepository.findLatest { latestProduct ->
            viewModelScope.launch {
                val response = cartRepository.fetchAllCart()
                val cartProductIds = response.content.map { it.product.id }

                val recommendedList = productRepository.fetchRecommendProducts(latestProduct.id, cartProductIds)
                _recommendItems.value = recommendedList
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
