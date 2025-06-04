package woowacourse.shopping.feature.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.data.util.mapper.toDomain
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import kotlin.math.max
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
    val leftPageEnable: LiveData<Boolean> get() = _isLeftPageEnable

    private val _isRightPageEnable = MutableLiveData(false)
    val isRightPageEnable: LiveData<Boolean> get() = _isRightPageEnable
    val rightPageEnable: LiveData<Boolean> get() = _isRightPageEnable

    private val endPage: Int get() = max(1, (totalCartSizeData + PAGE_SIZE - 1) / PAGE_SIZE)

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

    init {
        updateCartQuantity()
    }

    fun loadRecommendedGoods() {
        goodsRepository.fetchMostRecentGoods { goods ->
            if (goods != null) {
                goodsRepository.fetchCategoryGoods(
                    10,
                    goods.category,
                    { goodsResponse ->
                        val allGoodsList = goodsResponse.content.map { CartItem(it.toDomain(), 0) }
                        filterRecommendedGoods(allGoodsList)
                    },
                    {
                        loadDefaultRecommendedGoods()
                    },
                )
            } else {
                loadDefaultRecommendedGoods()
            }
        }
    }

    private fun filterRecommendedGoods(allGoodsList: List<CartItem>) {
        cartRepository.fetchAllCartItems(
            { cartResponse: CartResponse ->
                val cartItems = cartResponse.toCartItems()
                val cartGoodsIds = cartItems.map { it.goods.id }.toSet()
                val filteredGoodsList = allGoodsList.filter { !cartGoodsIds.contains(it.goods.id) }
                _recommendedGoods.value = filteredGoodsList
            },
            {
                _recommendedGoods.value = allGoodsList
            },
        )
    }

    private fun loadDefaultRecommendedGoods() {
        goodsRepository.fetchPageGoods(
            10,
            1,
            { response ->
                val allGoodsList = response.content.map { CartItem(it.toDomain(), 0) }
                filterRecommendedGoods(allGoodsList)
            },
            {
                _recommendedGoods.value = emptyList()
            },
        )
    }

    fun addCartItemOrIncreaseQuantity(cartItem: CartItem) {
        val existing = selectedCartMap[cartItem.id]
        if (existing != null) {
            existing.quantity += 1
        } else {
            val toAdd = cartItem.copy(quantity = 1)
            selectedCartMap[toAdd.id] = toAdd
        }
        updateAllSelected()
        updateTotalPriceAndCount()
    }

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> =
        cartResponse.toCartItems()

    fun getPosition(cartItem: CartItem): Int? =
        _visibleCart.value?.indexOf(cartItem)?.takeIf { it >= 0 }

    fun increaseQuantity(cartItem: CartItem) {
        cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity + 1), {
            updateCartQuantity()
            selectedCartMap[cartItem.id]?.quantity = (selectedCartMap[cartItem.id]?.quantity ?: 0) + 1
        }, {
            _toastMessage.postValue(TOAST_FAIL_INCREASE)
        })
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (cartItem.quantity <= 1) {
            _removeItemEvent.setValue(cartItem)
        } else {
            cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity - 1), {
                updateCartQuantity()
                selectedCartMap[cartItem.id]?.quantity = (selectedCartMap[cartItem.id]?.quantity ?: 0) - 1
            }, {
                _toastMessage.postValue(TOAST_FAIL_DECREASE)
            })
        }
    }

    fun delete(cartItem: CartItem) {
        selectedCartMap.remove(cartItem.id)
        cartRepository.delete(cartItem.id, {
            updateCartQuantity()
        }, {
            _toastMessage.postValue(TOAST_FAIL_DELETE)
        })
    }

    fun updateCartQuantity() {
        cartRepository.fetchCartItemsByPage(
            currentPage - 1,
            PAGE_SIZE,
            { cartResponse ->
                updateCartDataSize(cartResponse)
                val pageItems = getCartItemByCartResponse(cartResponse)
                _visibleCart.value = pageItems
                updatePageMoveAvailability(cartResponse)
                _isLoading.value = false
                updateTotalPriceAndCount()
                if (cartResponse.totalPages >= MINIMUM_PAGE && cartResponse.totalPages < currentPage) {
                    currentPage = cartResponse.totalPages
                    updateCartQuantity()
                }
            },
            { _toastMessage.postValue(TOAST_FAIL_LOGIN) }
        )
    }

    private fun updateCartDataSize(response: CartResponse) {
        totalCartSizeData = response.totalElements
        _isMultiplePages.postValue(totalCartSizeData > PAGE_SIZE)
    }

    fun plusPage() {
        currentPage++
        updateCartQuantity()
    }

    fun minusPage() {
        currentPage--
        updateCartQuantity()
    }

    private fun updatePageMoveAvailability(response: CartResponse) {
        _isLeftPageEnable.value = !response.first
        _isRightPageEnable.value = !response.last
    }

    fun setItemSelection(cartItem: CartItem, isSelected: Boolean) {
        val foundItem = _visibleCart.value?.find { it.id == cartItem.id }
        foundItem?.let {
            if (isSelected) {
                selectedCartMap[cartItem.id] = it
            } else {
                selectedCartMap.remove(cartItem.id)
            }
            updateAllSelected()
            updateTotalPriceAndCount()
        }
    }

    fun isItemSelected(cartItem: CartItem): Boolean = selectedCartMap.containsKey(cartItem.id)

    fun selectAllItemsFromServer() {
        cartRepository.fetchAllCartItems({ allItems ->
            selectedCartMap.clear()
            allItems.toCartItems().forEach { selectedCartMap[it.id] = it }
            _isAllSelected.value = true
            updateTotalPriceAndCount()
        }, {
            _toastMessage.postValue(TOAST_FAIL_SELECT_ALL)
        })
    }

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

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val PAGE_SIZE = 5

        private const val TOAST_FAIL_INCREASE = "수량 증가에 실패했습니다."
        private const val TOAST_FAIL_DECREASE = "수량 감소에 실패했습니다."
        private const val TOAST_FAIL_DELETE = "카트 아이템 삭제에 실패했습니다."
        private const val TOAST_FAIL_SELECT_ALL = "전체 선택에 실패했습니다."
        private const val TOAST_FAIL_LOGIN = "로그인에 실패했습니다."
    }
}