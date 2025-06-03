package woowacourse.shopping.feature.cart

import android.os.Handler
import android.os.Looper.getMainLooper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.Cart
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
    private val _carts = MutableLiveData<List<Cart>>()
    val carts: LiveData<List<Cart>> get() = _carts
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
            _checkedItemsPrice.value = _checkedItemsPrice.value?.minus(cart.product.price * cart.quantity)
        } else {
            selectedItems.value = currentList.plus(cart)
            _totalCheckedItemsCount.value = _totalCheckedItemsCount.value?.plus(cart.quantity)
            _checkedItemsPrice.value = _checkedItemsPrice.value?.plus(cart.product.price * cart.quantity)
        }
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

        cartRepository.deleteCart(cart.id) { result ->
            result
                .onSuccess {
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
                    Log.e("123451", "$error")
                }
        }
    }

    fun addToCart(cart: Cart) {
        cartRepository.updateCart(cart.id, CartQuantity(cart.quantity + 1)) { result ->
            result
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
                    Log.e("addCartTest", "장바구니 추가 실패", error)
                }
        }
    }

    fun removeFromCart(cart: Cart) {
        if (cart.quantity == 1) {
            delete(cart)
        } else {
            cartRepository.updateCart(
                id = cart.id,
                cartQuantity = CartQuantity(cart.quantity - 1),
            ) { result ->
                result
                    .onSuccess {
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
                        Log.e("123451", "$error")
                    }
            }
        }
    }

    private fun fetchTotalItemsCount() {
        cartRepository.getCartCounts(
            onSuccess = { count ->
                totalItemsCount.postValue(count.toInt())
            },
            onError = { Log.e("loadProductsInRange", "API 요청 실패", it) },
        )
    }

    private fun loadCarts() {
        _isLoading.postValue(true)
        Handler(getMainLooper()).postDelayed({
            cartRepository.fetchCart(
                onSuccess = { response ->
                    val cartList =
                        response.content.map {
                            Cart(
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
                },
                onError = { Log.e("loadProductsInRange", "API 요청 실패", it) },
                page = currentPage,
            )
            _isLoading.postValue(false)
        }, 1000) // 스켈레톤 UI 테스트를 위한 딜레이입니다.
    }

    fun loadProductsByCategory() {
        historyRepository.findLatest { latestProduct ->
            cartRepository.fetchAllCart(
                onSuccess = { cartResponse ->
                    val cartProductIds = cartResponse.content.map { it.product.id }

                    productRepository.fetchAllProducts(
                        onSuccess = { response ->
                            val matchedProduct =
                                response.content.find { it.id == latestProduct.id }
                            val category = matchedProduct?.category

                            val recommendProducts =
                                response.content
                                    .filter {
                                        it.category == category &&
                                            it.id != latestProduct.id &&
                                            it.id !in cartProductIds
                                    }.take(10)

                            _recommendItems.value =
                                recommendProducts.map {
                                    Cart(id = 0, product = it.toDomain(), quantity = 0)
                                }
                        },
                        onError = {
                            Log.e("loadProductsInRange", "상품 요청 실패", it)
                        },
                    )
                },
                onError = {
                    Log.e("loadProductsInRange", "장바구니 요청 실패", it)
                },
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
