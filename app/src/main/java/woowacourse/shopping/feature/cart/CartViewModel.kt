package woowacourse.shopping.feature.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

sealed class CartUiEvent {
    data class ShowToast(val key: ToastMessageKey) : CartUiEvent()
}

enum class ToastMessageKey {
    FAIL_INCREASE,
    FAIL_DECREASE,
    FAIL_DELETE,
    FAIL_SELECT_ALL,
    FAIL_LOGIN
}

class CartViewModel(
    private val cartRepository: CartRepository,
    private val goodsRepository: GoodsRepository,
) : ViewModel() {

    private val _toastMessage = MutableSingleLiveData<String>()
    val toastMessage: SingleLiveData<String> get() = _toastMessage

    private val _isMultiplePages = MutableLiveData(false)
    val isMultiplePages: LiveData<Boolean> get() = _isMultiplePages

    private var currentPage: Int = MINIMUM_PAGE
        set(value) {
            field = value
            _page.postValue(value)
        }
    private val _page = MutableLiveData(currentPage)
    val page: LiveData<Int> get() = _page

    private val _visibleCart = MutableLiveData<List<CartItem>>()
    val cart: LiveData<List<CartItem>> get() = _visibleCart

    private var totalCartSizeData: Int = 0

    private val _isLeftPageEnable = MutableLiveData(false)
    val isLeftPageEnable: LiveData<Boolean> get() = _isLeftPageEnable

    private val _isRightPageEnable = MutableLiveData(false)
    val isRightPageEnable: LiveData<Boolean> get() = _isRightPageEnable

    private val _removeItemEvent = MutableSingleLiveData<CartItem>()
    val removeItemEvent: SingleLiveData<CartItem> get() = _removeItemEvent

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val selectedCartMap = mutableMapOf<Int, CartItem>()

    private val _isAllSelected = MutableLiveData(false)
    val isAllSelected: LiveData<Boolean> get() = _isAllSelected

    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _selectedItemCount = MutableLiveData(0)
    val selectedItemCount: LiveData<Int> get() = _selectedItemCount

    private val _recommendedGoods = MutableLiveData<List<CartItem>>()
    val recommendedGoods: LiveData<List<CartItem>> = _recommendedGoods

    private val _event = MutableSingleLiveData<CartUiEvent>()
    val event: SingleLiveData<CartUiEvent> = _event

    init {
        updateCartQuantity()
    }

    fun loadRecommendedGoods() {
        viewModelScope.launch {
            val goods = goodsRepository.fetchMostRecentGoods()
            val allGoods = try {
                if (goods != null) {
                    goodsRepository.fetchCategoryGoods(10, goods.category)
                } else {
                    goodsRepository.fetchPageGoods(10, 1)
                }
            } catch (e: Exception) {
                _recommendedGoods.value = emptyList()
                return@launch
            }.map { CartItem(it, 0) }

            try {
                val cartItems = cartRepository.fetchAllCartItems().toCartItems()
                val cartIds = cartItems.map { it.goods.id }.toSet()
                _recommendedGoods.value = allGoods.filter { it.goods.id !in cartIds }
            } catch (e: Exception) {
                _recommendedGoods.value = allGoods
            }
        }
    }

    fun addCartItemOrIncreaseQuantity(cartItem: CartItem) {
        val existing = selectedCartMap[cartItem.id]
        if (existing != null) {
            existing.quantity += 1
        } else {
            selectedCartMap[cartItem.id] = cartItem.copy(quantity = 1)
        }
        updateAllSelected()
        updateTotalPriceAndCount()
    }

    fun getPosition(cartItem: CartItem): Int? =
        _visibleCart.value?.indexOf(cartItem)?.takeIf { it >= 0 }

    fun plusPage() {
        currentPage++
        updateCartQuantity()
    }

    fun minusPage() {
        currentPage--
        updateCartQuantity()
    }

    fun setItemSelection(cartItem: CartItem, isSelected: Boolean) {
        _visibleCart.value?.find { it.id == cartItem.id }?.let {
            if (isSelected) selectedCartMap[cartItem.id] = it else selectedCartMap.remove(cartItem.id)
            updateAllSelected()
            updateTotalPriceAndCount()
        }
    }

    fun isItemSelected(cartItem: CartItem): Boolean = selectedCartMap.containsKey(cartItem.id)

    fun selectAllItems(isSelected: Boolean) {
        if (!isSelected) {
            selectedCartMap.clear()
            _isAllSelected.value = false
            updateTotalPriceAndCount()
        } else {
            selectAllItemsFromServer()
        }
    }

    private fun updateAllSelected() {
        val currentPageItems = _visibleCart.value ?: return
        _isAllSelected.value = currentPageItems.all { selectedCartMap.containsKey(it.id) }
    }

    private fun updateTotalPriceAndCount() {
        val total = selectedCartMap.values.sumOf { it.goods.price * it.quantity }
        _totalPrice.value = total
        _selectedItemCount.value = selectedCartMap.size
    }

    fun increaseQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity + 1))
                updateCartQuantity()
                selectedCartMap[cartItem.id]?.quantity = (selectedCartMap[cartItem.id]?.quantity ?: 0) + 1
            } catch (e: Exception) {
                _event.postValue(CartUiEvent.ShowToast(ToastMessageKey.FAIL_INCREASE))
            }
        }
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (cartItem.quantity <= 1) {
            _removeItemEvent.setValue(cartItem)
        } else {
            viewModelScope.launch {
                try {
                    cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity - 1))
                    updateCartQuantity()
                    selectedCartMap[cartItem.id]?.quantity = (selectedCartMap[cartItem.id]?.quantity ?: 0) - 1
                } catch (e: Exception) {
                    _event.postValue(CartUiEvent.ShowToast(ToastMessageKey.FAIL_DECREASE))
                }
            }
        }
    }

    fun delete(cartItem: CartItem) {
        selectedCartMap.remove(cartItem.id)
        viewModelScope.launch {
            try {
                cartRepository.delete(cartItem.id)
                updateCartQuantity()
            } catch (e: Exception) {
                _event.postValue(CartUiEvent.ShowToast(ToastMessageKey.FAIL_DELETE))
            }
        }
    }

    fun selectAllItemsFromServer() {
        viewModelScope.launch {
            try {
                val allItems = cartRepository.fetchAllCartItems().toCartItems()
                selectedCartMap.clear()
                allItems.forEach { selectedCartMap[it.id] = it }
                _isAllSelected.value = true
                updateTotalPriceAndCount()
            } catch (e: Exception) {
                _event.postValue(CartUiEvent.ShowToast(ToastMessageKey.FAIL_SELECT_ALL))
            }
        }
    }

    fun updateCartQuantity() {
        viewModelScope.launch {
            try {
                val cartResponse = cartRepository.fetchCartItemsByPage(currentPage - 1, PAGE_SIZE)
                totalCartSizeData = cartResponse.totalElements
                _isMultiplePages.postValue(totalCartSizeData > PAGE_SIZE)
                val pageItems = cartResponse.toCartItems()
                _visibleCart.value = pageItems
                _isLeftPageEnable.value = !cartResponse.first
                _isRightPageEnable.value = !cartResponse.last
                _isLoading.value = false
                updateTotalPriceAndCount()
                if (cartResponse.totalPages >= MINIMUM_PAGE && cartResponse.totalPages < currentPage) {
                    currentPage = cartResponse.totalPages
                    updateCartQuantity()
                }
            } catch (e: Exception) {
                _event.postValue(CartUiEvent.ShowToast(ToastMessageKey.FAIL_LOGIN))
            }
        }
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val PAGE_SIZE = 5
    }
}