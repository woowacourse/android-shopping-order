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
    val leftPageEnable: LiveData<Boolean> get() = _isLeftPageEnable

    private val _isRightPageEnable = MutableLiveData(false)
    val isRightPageEnable: LiveData<Boolean> get() = _isRightPageEnable
    val rightPageEnable: LiveData<Boolean> get() = _isRightPageEnable

    private val endPage: Int get() = max(1, (totalCartSizeData + PAGE_SIZE - 1) / PAGE_SIZE)

    private val _loginErrorEvent: MutableSingleLiveData<CartFetchError> = MutableSingleLiveData()
    val loginErrorEvent: SingleLiveData<CartFetchError> get() = _loginErrorEvent

    private val _removeItemEvent: MutableSingleLiveData<CartItem> = MutableSingleLiveData()
    val removeItemEvent: SingleLiveData<CartItem> get() = _removeItemEvent

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedItems = MutableLiveData<Set<Int>>(emptySet())
    val selectedItems: LiveData<Set<Int>> get() = _selectedItems

    private val _isAllSelected = MutableLiveData(false)
    val isAllSelected: LiveData<Boolean> get() = _isAllSelected

    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _selectedItemCount = MutableLiveData(0)
    val selectedItemCount: LiveData<Int> get() = _selectedItemCount

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
            _removeItemEvent.setValue(cartItem)
        } else {
            cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity - 1), {
                updateCartQuantity()
            }, {})
        }
    }

    fun updateCartQuantity() {
        cartRepository.fetchCartItemsByPage(
            currentPage - 1,
            PAGE_SIZE,
            { cartResponse ->
                updateCartDataSize(response = cartResponse)
                _cart.value = getCartItemByCartResponse(cartResponse)
                updatePageMoveAvailability(cartResponse)
                if (cartResponse.totalPages in MINIMUM_PAGE..<currentPage) {
                    currentPage = cartResponse.totalPages
                    updateCartQuantity()
                }
                _isLoading.value = false
                updateTotalPriceAndCount()
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
        val currentSelected = _selectedItems.value?.toMutableSet() ?: mutableSetOf()
        currentSelected.remove(cartItem.id)
        _selectedItems.value = currentSelected

        cartRepository.delete(cartItem.id) {
            updateCartQuantity()
        }
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

    fun selectAllItems(isSelected: Boolean) {
        val currentCart = _cart.value ?: emptyList()
        if (isSelected) {
            _selectedItems.value = currentCart.map { it.id }.toSet()
        } else {
            _selectedItems.value = emptySet()
        }
        _isAllSelected.value = isSelected
        updateTotalPriceAndCount()
    }

    fun toggleItemSelection(cartItem: CartItem) {
        val currentSelected = _selectedItems.value?.toMutableSet() ?: mutableSetOf()
        if (currentSelected.contains(cartItem.id)) {
            currentSelected.remove(cartItem.id)
        } else {
            currentSelected.add(cartItem.id)
        }
        _selectedItems.value = currentSelected

        val currentCart = _cart.value ?: emptyList()
        _isAllSelected.value = currentSelected.size == currentCart.size && currentCart.isNotEmpty()

        updateTotalPriceAndCount()
    }

    fun isItemSelected(cartItem: CartItem): Boolean = _selectedItems.value?.contains(cartItem.id) == true

    private fun updateTotalPriceAndCount() {
        val currentCart = _cart.value ?: emptyList()
        val selectedIds = _selectedItems.value ?: emptySet()

        val selectedCartItems = currentCart.filter { selectedIds.contains(it.id) }

        val totalPrice =
            selectedCartItems
                .sumOf { item: CartItem ->
                    (item.goods.price * item.quantity).toLong()
                }.toInt()
        val selectedCount = selectedCartItems.size

        _totalPrice.value = totalPrice
        _selectedItemCount.value = selectedCount
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val PAGE_SIZE = 5
    }
}
