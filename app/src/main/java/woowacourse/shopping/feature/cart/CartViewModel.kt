package woowacourse.shopping.feature.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import woowacourse.shopping.data.local.cart.repository.LocalCartRepository
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Carts
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
    private val _cart = MutableLiveData<Cart>()
    val cart: LiveData<Cart> get() = _cart
    val carts: LiveData<Carts> =
        _page.switchMap { pageNum ->
            localCartRepository.getPage(PAGE_SIZE, (pageNum - 1) * PAGE_SIZE)
        }
    private val _totalItemsCount = MutableLiveData(0)
    val totalItemsCount: LiveData<Int> get() = _totalItemsCount
    private val _isLeftPageEnable = MutableLiveData(false)
    val isLeftPageEnable: LiveData<Boolean> get() = _isLeftPageEnable
    private val _isRightPageEnable = MutableLiveData(false)
    val isRightPageEnable: LiveData<Boolean> get() = _isRightPageEnable

    init {
        fetchTotalItemsCount()
        test()
    }

    fun delete(cart: Cart) {
        val total = totalItemsCount.value ?: 0
        val endPage = ((total - 1) / PAGE_SIZE) + 1

        if (currentPage == endPage && (total - 1) == ((currentPage - 1) * PAGE_SIZE)) {
            currentPage--
            _page.value = currentPage
        }

        localCartRepository.deleteAll(cart)
        updatePageButtonStates()
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

    fun insertToCart(cart: Cart) {
        _cart.value = cart
        localCartRepository.insert(cart)
        val current = _cart.value
        if (current != null) {
            val updated = current.updateQuantity(current.quantity + 1)
            _cart.value = updated
        }
    }

    fun removeFromCart(cart: Cart) {
        _cart.value = cart
        localCartRepository.delete(cart)
        val current = _cart.value
        if (current != null) {
            val updated = current.updateQuantity(current.quantity - 1)
            _cart.value = updated
        }
    }

    private fun fetchTotalItemsCount() {
        localCartRepository.getAllItemsSize { count ->
            _totalItemsCount.postValue(count)
        }
    }

    fun test() {
        cartRepository.fetchCart(
            onSuccess = { productList ->
                val carts = productList.map { product ->
                    Cart(product = product, quantity = 0)
                }
                Log.d("loadProductsInRange", "$carts")
            },
            onError = { Log.e("loadProductsInRange", "API 요청 실패", it) }
        )
    }

    fun addCartTest(cart: Cart) {
        _cart.value = cart
        val current = _cart.value
        if (current != null) {
            val cartRequest = CartRequest(
                productId = current.product.id,
                quantity = current.quantity
            )

            cartRepository.addToCart(cartRequest) { result ->
                result.onSuccess {
                    Log.d("addCartTest", "장바구니에 추가 성공")
                }.onFailure { error ->
                    Log.e("addCartTest", "장바구니 추가 실패", error)
                }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
