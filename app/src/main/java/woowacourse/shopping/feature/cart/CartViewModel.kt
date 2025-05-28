package woowacourse.shopping.feature.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import kotlin.math.max

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _isMultiplePages = MutableLiveData(false)
    val isMultiplePages: LiveData<Boolean> get() = _isMultiplePages

    private var currentPage: Int = MINIMUM_PAGE
        set(value) {
            field = value
            _page.postValue(value)
        }
    private val _page = MutableLiveData(currentPage)
    val page: LiveData<Int> get() = _page

    private val _cart = MutableLiveData<List<CartItem>>()
    val cart: LiveData<List<CartItem>> get() = _cart

    private var totalCartSizeData: Int = 0

    private val _isLeftPageEnable = MutableLiveData(false)
    val isLeftPageEnable: LiveData<Boolean> get() = _isLeftPageEnable

    private val _isRightPageEnable = MutableLiveData(false)
    val isRightPageEnable: LiveData<Boolean> get() = _isRightPageEnable

    private val endPage: Int get() = max(1, (totalCartSizeData + PAGE_SIZE - 1) / PAGE_SIZE)

    private val _loginErrorEvent: MutableSingleLiveData<CartFetchError> = MutableSingleLiveData()
    val loginErrorEvent: SingleLiveData<CartFetchError> get() = _loginErrorEvent

    private val _removeItemEvent: MutableSingleLiveData<CartItem> = MutableSingleLiveData()
    val removeItemEvent: SingleLiveData<CartItem> get() = _removeItemEvent

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        updateCartQuantity()
    }

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> = cartResponse.toCartItems()

    fun getPosition(cartItem: CartItem): Int? {
        val idx = cart.value?.indexOf(cartItem) ?: return null
        return if (idx >= 0) idx else null
    }

    fun increaseQuantity(cartItem: CartItem) {
        cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity + 1), {
            updateCartQuantity()
        }) {
        }
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (cartItem.quantity - 1 <= 0) {
//            cartRepository.delete(cartItem.id) { updateCartQuantity() }
            _removeItemEvent.setValue(cartItem)
        } else {
            cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity + 1), {
                updateCartQuantity()
            }, {})
        }
    }

    fun updateCartQuantity() {
        cartRepository.fetchCartItemsByPage(
            currentPage - 1,
            PAGE_SIZE,
            { cartResponse ->

                _cart.value = getCartItemByCartResponse(cartResponse)
                updatePageMoveAvailability(cartResponse)
                updateCartDataSize(cartResponse)
                if (cartResponse.totalPages in MINIMUM_PAGE..<currentPage) {
                    currentPage = cartResponse.totalPages
                    updateCartQuantity()
                }
                _isLoading.value = false
            },
            { cartFetchError ->
                _loginErrorEvent.setValue(cartFetchError)
            },
        )
    }

    private fun updateCartDataSize(response: CartResponse) {
        totalCartSizeData = response.totalElements
        _isMultiplePages.postValue(totalCartSizeData > PAGE_SIZE)
    }

    fun delete(cartItem: CartItem) {
        cartRepository.delete(cartItem.id) {
            updateCartQuantity()
        }
    }

    private fun updatePage() {
        cartRepository.getAllItemsSize { totalCartSize ->
            totalCartSizeData = totalCartSize
            if (currentPage > endPage) {
                currentPage = endPage
            }
            _isMultiplePages.postValue(totalCartSize > PAGE_SIZE)
            updateCartQuantity()
        }
        cartRepository.fetchCartItemsByPage(currentPage, PAGE_SIZE, {}, {})
    }

    fun plusPage() {
        currentPage++
        updateCartQuantity()
    }

    fun minusPage() {
        currentPage--
        updateCartQuantity()
    }

    private fun updatePageMoveAvailability(cartResponse: CartResponse) {
        _isLeftPageEnable.postValue(!cartResponse.first)
        _isRightPageEnable.postValue(!cartResponse.last)
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val PAGE_SIZE = 5
    }
}
