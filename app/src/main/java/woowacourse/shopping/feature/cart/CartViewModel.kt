package woowacourse.shopping.feature.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.Authorization
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import kotlin.math.max

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _isMultiplePages = MutableLiveData(false)
    val isMultiplePages: LiveData<Boolean> get() = _isMultiplePages

    private var currentPage: Int = 1
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

    init {
        updateCartQuantity()
    }

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> =
        cartResponse.toCartItems()

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
            cartRepository.delete(cartItem.id) { updateCartQuantity() }
        }
        else{
            cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity + 1), {
                updateCartQuantity()
            }, {})
        }
    }

    fun updateCartQuantity() {
        cartRepository.fetchCartItemsByPage(
            PAGE_SIZE,
            (currentPage - 1) * PAGE_SIZE,
            { cartResponse ->
                _cart.value = getCartItemByCartResponse(cartResponse)
                updatePageMoveAvailability(cartResponse)
                updateCartDataSize(cartResponse)
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
        private const val PAGE_SIZE = 5
    }
}
