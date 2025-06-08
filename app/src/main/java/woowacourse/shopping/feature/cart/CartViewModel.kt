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
        if (currentList.contains(cart)) {
            selectedItems.value = currentList.minus(cart)
            _totalCheckedItemsCount.value = _totalCheckedItemsCount.value?.minus(cart.quantity)
            _checkedItemsPrice.value =
                _checkedItemsPrice.value?.minus(cart.product.price * cart.quantity)
        } else {
            selectedItems.value = currentList.plus(cart)
            _totalCheckedItemsCount.value = _totalCheckedItemsCount.value?.plus(cart.quantity)
            _checkedItemsPrice.value =
                _checkedItemsPrice.value?.plus(cart.product.price * cart.quantity)
        }
    }

    fun changeAllChecked() {
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
                        _carts.value?.filter { it.cart.id != cart.id } ?: emptyList()
                    _carts.value = updatedList
                    fetchTotalItemsCount()
                }.onFailure {
                    Log.e("DeleteCartTest", "장바구니 삭제 실패")
                }
        }
    }

    fun addToCart(cart: Cart) {
        viewModelScope.launch {
            cartRepository
                .updateCart(
                    id = cart.id,
                    cartQuantity = CartQuantity(cart.quantity + 1),
                ).onSuccess {
                    val updatedList =
                        _carts.value?.map {
                            val updatedCart = it.cart
                            if (updatedCart.product.id == cart.product.id) {
                                val updated = updatedCart.updateQuantity(updatedCart.quantity + 1)
                                it.copy(cart = updated)
                            } else {
                                it
                            }
                        } ?: emptyList()
                    _carts.postValue(updatedList)
                    fetchTotalItemsCount()
                }.onFailure {
                    Log.e("addCartTest", "장바구니 추가 실패")
                }
        }
    }

    fun removeFromCart(cart: Cart) {
        viewModelScope.launch {
            if (cart.quantity == 1) {
                delete(cart)
            } else {
                cartRepository
                    .updateCart(
                        id = cart.id,
                        cartQuantity = CartQuantity(cart.quantity - 1),
                    ).onSuccess {
                        val updatedList =
                            _carts.value?.map {
                                val updatedCart = it.cart
                                if (updatedCart.product.id == cart.product.id) {
                                    val updated =
                                        updatedCart.updateQuantity(updatedCart.quantity - 1)
                                    it.copy(cart = updated)
                                } else {
                                    it
                                }
                            } ?: emptyList()
                        _carts.value = updatedList
                        fetchTotalItemsCount()
                    }.onFailure {
                        Log.e("RemoveCartTest", "장바구니 삭제 실패")
                    }
            }
        }
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

    private fun loadCarts() {
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository
                .fetchCart(page = currentPage)
                .onSuccess { response ->
                    response?.let {
                        val cartList =
                            response.content.map {
                                CartGoodsItem(
                                    Cart(
                                        id = it.id,
                                        product = it.product.toDomain(),
                                        quantity = it.quantity,
                                    ),
                                )
                            }

                        _carts.value = cartList

                        _page.value = response.number
                        updatePageButtonStates(
                            response.first,
                            response.last,
                            response.totalElements.toInt(),
                        )
                        _isLoading.value = false
                    }
                }.onFailure {
                    Log.e("loadProductsInRange", "API 요청 실패", it)
                    _isLoading.value = false
                }
            _page.value = currentPage
        }
    }

    fun loadProductsByCategory() {
        historyRepository.findLatest { latestProduct ->
//            cartRepository.fetchAllCart(
//                onSuccess = { cartResponse ->
//                    val cartProductIds = cartResponse.content.map { it.product.id }
//
//                    productRepository.fetchAllProducts(
//                        onSuccess = { response ->
//                            val matchedProduct =
//                                response.content.find { it.id.toInt() == latestProduct.product.id }
//                            val category = matchedProduct?.category
//
//                            val recommendProducts =
//                                response.content
//                                    .filter {
//                                        it.category == category &&
//                                            it.id.toInt() != latestProduct.product.id &&
//                                            it.id !in cartProductIds
//                                    }.take(10)
//
//                            _recommendItems.value =
//                                recommendProducts.map {
//                                    Cart(id = 0, product = it.toDomain(), quantity = 0)
//                                }
//                        },
//                        onError = {
//                            Log.e("loadProductsInRange", "상품 요청 실패", it)
//                        },
//                    )
//                },
//                onError = {
//                    Log.e("loadProductsInRange", "장바구니 요청 실패", it)
//                },
//            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
