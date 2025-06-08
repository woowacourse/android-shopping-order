package woowacourse.shopping.feature.goods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.Authorization
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

sealed class GoodsUiEvent {
    data class ShowToast(val messageKey: ToastMessageKey) : GoodsUiEvent()
    object NavigateToCart : GoodsUiEvent()
    object NavigateToLogin : GoodsUiEvent()
}

enum class ToastMessageKey {
    FAIL_LOGIN,
    FAIL_CART_LOAD,
    FAIL_CART_ADD,
    FAIL_CART_UPDATE,
    FAIL_CART_DELETE
}

class GoodsViewModel(
    private val cartRepository: CartRepository,
    private val goodsRepository: GoodsRepository,
) : ViewModel() {

    private val goods = mutableListOf<Goods>()
    private var page: Int = 1

    private val _isFullLoaded = MutableLiveData(false)
    val isFullLoaded: LiveData<Boolean> get() = _isFullLoaded

    private val _goodsWithCartQuantity = MutableLiveData<List<CartItem>>()
    val goodsWithCartQuantity: LiveData<List<CartItem>> get() = _goodsWithCartQuantity

    private val _totalCartItemSize = MutableLiveData(CART_EMPTY)
    val totalCartItemSize: LiveData<String> get() = _totalCartItemSize

    private val _recentlyViewedGoods = MutableLiveData<List<Goods>>()
    val recentlyViewedGoods: LiveData<List<Goods>> get() = _recentlyViewedGoods

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _uiEvent = MutableSingleLiveData<GoodsUiEvent>()
    val uiEvent: SingleLiveData<GoodsUiEvent> get() = _uiEvent

    private var cachedCartItems = mutableMapOf<Int, CartItem>()

    init {
        viewModelScope.launch {
            appendCartItemsWithZeroQuantity()
            updateRecentlyViewedGoods()
        }
    }

    fun findCart(goods: Goods): CartItem? = cachedCartItems[goods.id]

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> =
        cartResponse.toCartItems()

    fun login(basicKey: String) {
        Authorization.setBasicKey(basicKey)
        viewModelScope.launch {
            try {
                cartRepository.checkValidBasicKey(basicKey)
                Authorization.setLoginStatus(true)
            } catch (e: Exception) {
                _uiEvent.postValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_LOGIN))
                Authorization.setLoginStatus(false)

            }
        }
    }

    fun onCartClicked() {
        if (!Authorization.isLogin) {
            _uiEvent.setValue(GoodsUiEvent.NavigateToLogin)
        } else {
            _uiEvent.setValue(GoodsUiEvent.NavigateToCart)
        }
    }

    private suspend fun appendCartItemsWithZeroQuantity() {
        try {
            val offset = (page - 1) * PAGE_SIZE
            val fetchedGoods = goodsRepository.fetchPageGoods(PAGE_SIZE, offset)
            goods.addAll(fetchedGoods)
            _isFullLoaded.postValue(fetchedGoods.size < PAGE_SIZE)
            _goodsWithCartQuantity.postValue(goods.map { CartItem(it, 0) })
        } catch (e: Exception) {
            _uiEvent.postValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_LOAD))
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun updateRecentlyViewedGoods() {
        viewModelScope.launch {
            val result = goodsRepository.fetchRecentGoods()
            _recentlyViewedGoods.postValue(result)
        }
    }

    fun fetchAndSetCartCache() {
        viewModelScope.launch {
            try {
                val cartResponse = cartRepository.fetchAllCartItems()
                val cartItems = getCartItemByCartResponse(cartResponse)
                cachedCartItems = cartItems.associateBy { it.goods.id }.toMutableMap()
                setTotalCartItemSize(cartItems.sumOf { it.quantity })
                bindCartCache()
            } catch (e: Exception) {
                _uiEvent.postValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_LOAD))
            }
        }
    }

    private fun bindCartCache() {
        val newList = goods.map { cachedCartItems[it.id] ?: CartItem(it, 0) }
        _goodsWithCartQuantity.postValue(newList)
    }

    private fun setTotalCartItemSize(total: Int) {
        val text = when {
            total < 1 -> CART_EMPTY
            total in 1..99 -> total.toString()
            else -> CART_MAX
        }
        _totalCartItemSize.postValue(text)
    }

    fun addPage() {
        page++
        viewModelScope.launch { appendCartItemsWithZeroQuantity() }
    }

    fun addCartItemOrIncreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _uiEvent.setValue(GoodsUiEvent.NavigateToLogin)
        } else {
            val cached = cachedCartItems[cartItem.goods.id]
            if (cached == null) {
                viewModelScope.launch { addCartItem(cartItem) }
            } else {
                viewModelScope.launch {
                    updateCartItemQuantity(cached.id, cartItem.copy(quantity = cached.quantity + 1))
                }
            }
        }
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _uiEvent.setValue(GoodsUiEvent.NavigateToLogin)
        } else {
            viewModelScope.launch {
                val existing = cachedCartItems[cartItem.goods.id] ?: return@launch
                if (existing.quantity <= 1) {
                    try {
                        cartRepository.delete(existing.id)
                        cachedCartItems.remove(existing.goods.id)
                        bindCartCache()
                    } catch (e: Exception) {
                        _uiEvent.setValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_DELETE))
                    }
                } else {
                    updateCartItemQuantity(
                        existing.id,
                        cartItem.copy(quantity = existing.quantity - 1)
                    )
                }
            }
        }
    }

    private suspend fun addCartItem(cartItem: CartItem) {
        try {
            cartRepository.addCartItem(cartItem.goods, 1)
            cachedCartItems[cartItem.goods.id] = cartItem.copy(quantity = 1)
            bindCartCache()
        } catch (e: Exception) {
            _uiEvent.setValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_ADD))
        }
    }

    private suspend fun updateCartItemQuantity(cartId: Int, cartItem: CartItem) {
        try {
            cartRepository.updateQuantity(cartId, CartQuantity(cartItem.quantity))
            cachedCartItems[cartItem.goods.id] = cartItem
            bindCartCache()
        } catch (e: Exception) {
            _uiEvent.setValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_UPDATE))
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val CART_EMPTY = "0"
        private const val CART_MAX = "99+"
    }
}