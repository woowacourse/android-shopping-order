package woowacourse.shopping.feature.goods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.dto.GoodsResponse
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.data.util.mapper.toDomain
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
        appendCartItemsWithZeroQuantity()
        updateRecentlyViewedGoods()
    }

    fun findCart(goods: Goods): CartItem? = cachedCartItems[goods.id]

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> =
        cartResponse.toCartItems()

    fun login(basicKey: String) {
        Authorization.setBasicKey(basicKey)
        cartRepository.checkValidBasicKey(basicKey, { response ->
            Authorization.setLoginStatus(response == 200)
        }, {
            _uiEvent.postValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_LOGIN))
        })
    }

    fun onCartClicked() {
        if (!Authorization.isLogin) {
            _uiEvent.setValue(GoodsUiEvent.NavigateToLogin)
        } else {
            _uiEvent.setValue(GoodsUiEvent.NavigateToCart)
        }
    }

    private fun appendCartItemsWithZeroQuantity() {
        val offset = (page - 1) * PAGE_SIZE
        goodsRepository.fetchPageGoods(
            limit = PAGE_SIZE,
            offset = offset,
            onComplete = { goodsResponse ->
                val fetchedGoods =
                    goodsResponse.content.map { it.toDomain() }
                goods.addAll(fetchedGoods)
                _isFullLoaded.postValue(goodsResponse.last)
                _goodsWithCartQuantity.postValue(goods.map { CartItem(goods = it, quantity = 0) })
                _isLoading.postValue(false)
            },
            onFail = {
                _uiEvent.postValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_LOAD))
                _isLoading.postValue(false)
            },
        )
    }

    fun updateRecentlyViewedGoods() {
        goodsRepository.fetchRecentGoods { goods ->
            _recentlyViewedGoods.postValue(goods)
        }
    }

    fun fetchAndSetCartCache() {
        cartRepository.fetchAllCartItems({ cartResponse ->
            val cartItems = getCartItemByCartResponse(cartResponse)
            cachedCartItems = cartItems.associateBy { it.goods.id }.toMutableMap()
            setTotalCartItemSize(cartItems.sumOf { it.quantity })
            bindCartCache()
        }, {
            _uiEvent.postValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_LOAD))
        })
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
        appendCartItemsWithZeroQuantity()
    }

    fun addCartItemOrIncreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _uiEvent.setValue(GoodsUiEvent.NavigateToLogin)
        } else {
            val cached = cachedCartItems[cartItem.goods.id]
            if (cached == null) {
                addCartItem(cartItem)
            } else {
                updateCartItemQuantity(cached.id, cartItem.copy(quantity = cached.quantity + 1))
            }
        }
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _uiEvent.setValue(GoodsUiEvent.NavigateToLogin)
        } else {
            cachedCartItems[cartItem.goods.id]?.let { existing ->
                if (existing.quantity <= 1) {
                    cartRepository.delete(existing.id, {
                        cachedCartItems.remove(existing.goods.id)
                        bindCartCache()
                    }, {
                        _uiEvent.setValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_DELETE))
                    })
                } else {
                    updateCartItemQuantity(
                        existing.id,
                        cartItem.copy(quantity = existing.quantity - 1)
                    )
                }
            }
        }
    }

    private fun addCartItem(cartItem: CartItem) {
        cartRepository.addCartItem(cartItem.goods, 1, {
            cachedCartItems[cartItem.goods.id] = cartItem.copy(quantity = 1)
            bindCartCache()
        }, {
            _uiEvent.setValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_ADD))
        })
    }

    private fun updateCartItemQuantity(cartId: Int, cartItem: CartItem) {
        cartRepository.updateQuantity(cartId, CartQuantity(cartItem.quantity), {
            cachedCartItems[cartItem.goods.id] = cartItem
            bindCartCache()
        }, {
            _uiEvent.setValue(GoodsUiEvent.ShowToast(ToastMessageKey.FAIL_CART_UPDATE))
        })
    }


    companion object {
        private const val PAGE_SIZE = 20
        private const val CART_EMPTY = "0"
        private const val CART_MAX = "99+"
    }
}