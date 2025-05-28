package woowacourse.shopping.feature.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.local.cart.repository.LocalCartRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.util.toDomain
import woowacourse.shopping.util.updateQuantity

class CartViewModel(
    private val localCartRepository: LocalCartRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _showPageButton = MutableLiveData(false)
    val showPageButton: LiveData<Boolean> get() = _showPageButton
    private var currentPage: Int = 1
    private val _page = MutableLiveData(currentPage)
    val page: LiveData<Int> get() = _page
    private val _carts = MutableLiveData<List<Cart>>()
    val carts: LiveData<List<Cart>> get() = _carts
    private val _totalItemsCount = MutableLiveData(0)
    val totalItemsCount: LiveData<Int> get() = _totalItemsCount
    private val _isLeftPageEnable = MutableLiveData(false)
    val isLeftPageEnable: LiveData<Boolean> get() = _isLeftPageEnable
    private val _isRightPageEnable = MutableLiveData(false)
    val isRightPageEnable: LiveData<Boolean> get() = _isRightPageEnable

    init {
        fetchTotalItemsCount()
        loadCarts()
    }

    fun plusPage() {
        currentPage++
        _page.value = currentPage
    }

    fun minusPage() {
        currentPage--
        _page.value = currentPage
    }

    fun updatePageButtonStates() {
        val total = totalItemsCount.value ?: 0
        val endPage = ((total - 1) / PAGE_SIZE) + 1

        val leftEnabled = currentPage > 1
        val rightEnabled = currentPage < endPage
        val showButtons = total > PAGE_SIZE

        _isLeftPageEnable.value = leftEnabled
        _isRightPageEnable.value = rightEnabled
        _showPageButton.value = showButtons
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
                    updatePageButtonStates()
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
                    updatePageButtonStates()
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
                        updatePageButtonStates()
                    }.onFailure { error ->
                        Log.e("123451", "$error")
                    }
            }
        }
    }

    private fun fetchTotalItemsCount() {
        localCartRepository.getAllItemsSize { count ->
            _totalItemsCount.postValue(count)
        }
    }

    private fun loadCarts() {
        cartRepository.fetchCart(
            onSuccess = { productList ->
                val cartList =
                    productList.map {
                        Cart(id = it.id, product = it.product.toDomain(), quantity = it.quantity)
                    }
                _carts.postValue(cartList)
            },
            onError = { Log.e("loadProductsInRange", "API 요청 실패", it) },
        )
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
