package woowacourse.shopping.feature.goods

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.carts.dto.CartQuantity
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

@Suppress("ktlint:standard:backing-property-naming")
class GoodsViewModel(
    private val cartRepository: CartRepository,
    private val goodsRepository: GoodsRepository,
) : ViewModel() {
    private val _goods = MutableLiveData<List<Goods>>(emptyList())

    private val _cartCache = MutableLiveData<Map<Int, CartItem>>(emptyMap())

    private var currentPage: Int = 1
    private val _isFullLoaded = MutableLiveData(false)
    val isFullLoaded: LiveData<Boolean> get() = _isFullLoaded

    private val _recentlyViewedGoods = MutableLiveData<List<Goods>>(emptyList())
    val recentlyViewedGoods: LiveData<List<Goods>> get() = _recentlyViewedGoods

    private val _navigateToCart = MutableSingleLiveData<Unit>()
    val navigateToCart: SingleLiveData<Unit> get() = _navigateToCart

    private val _navigateToLogin = MutableSingleLiveData<Unit>()
    val navigateToLogin: SingleLiveData<Unit> get() = _navigateToLogin

    val goodsWithCartQuantity: LiveData<List<CartItem>> =
        MediatorLiveData<List<CartItem>>().apply {
            fun update() {
                val goods = _goods.value ?: emptyList()
                val cartCache = _cartCache.value ?: emptyMap()
                value =
                    goods.map { goods ->
                        cartCache[goods.id] ?: CartItem(goods = goods, quantity = 0)
                    }
            }
            addSource(_goods) { update() }
            addSource(_cartCache) { update() }
        }

    val totalCartItemSize: LiveData<String> =
        _cartCache.map { cartCache ->
            val totalQuantity = cartCache.values.sumOf { it.quantity }
            when {
                totalQuantity < 1 -> "0"
                totalQuantity in 1..99 -> totalQuantity.toString()
                else -> "99+"
            }
        }

    @VisibleForTesting
    internal fun setTestGoods(goodsList: List<Goods>) {
        _goods.value = goodsList
    }

    @VisibleForTesting
    internal fun setTestCartCache(cartItems: List<CartItem>) {
        _cartCache.value = cartItems.associateBy { it.goods.id }
    }

    @VisibleForTesting
    internal fun setTestRecentlyViewedGoods(goodsList: List<Goods>) {
        _recentlyViewedGoods.value = goodsList
    }

    @VisibleForTesting
    internal fun setTestIsFullLoaded(isLoaded: Boolean) {
        _isFullLoaded.value = isLoaded
    }

    fun initialize() {
        appendCartItemsWithZeroQuantity()
    }

    fun findCart(goods: Goods): Int = _cartCache.value?.get(goods.id)?.id ?: -1

    fun mostRecentlyViewedCartId(): Int {
        val recentGoodsId = _recentlyViewedGoods.value?.firstOrNull()?.id
        return _cartCache.value
            ?.values
            ?.find { it.goods.id == recentGoodsId }
            ?.id ?: -1
    }

    fun login(basicKey: String) {
        Authorization.setBasicKey(basicKey)
        cartRepository.checkValidBasicKey(basicKey, { response ->
            when {
                response == 200 -> Authorization.setLoginStatus(true)
                else -> Authorization.setLoginStatus(false)
            }
        }, {})
    }

    fun onCartClicked() {
        if (!Authorization.isLogin) {
            _navigateToLogin.setValue(Unit)
        } else {
            _navigateToCart.setValue(Unit)
        }
    }

    fun addPage() {
        currentPage++
        appendCartItemsWithZeroQuantity()
    }

    fun updateRecentlyViewedGoods() {
        goodsRepository.fetchRecentGoods { goods ->
            _recentlyViewedGoods.value = goods
        }
    }

    fun fetchAndSetCartCache() {
        cartRepository.fetchAllCartItems({ cartResponse ->
            val cartItems = cartResponse.toCartItems()
            _cartCache.value = cartItems.associateBy { it.goods.id }
        }, {})
    }

    fun addCartItemOrIncreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _navigateToLogin.setValue(Unit)
        } else {
            val currentCache = _cartCache.value ?: emptyMap()
            val existingItem = currentCache[cartItem.goods.id]

            if (existingItem == null) {
                addCartItem(cartItem)
            } else {
                updateCartItemQuantity(
                    existingItem.id,
                    cartItem.copy(quantity = existingItem.quantity + 1),
                )
            }
        }
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _navigateToLogin.setValue(Unit)
        } else {
            val currentCache = _cartCache.value ?: emptyMap()
            val existingItem = currentCache[cartItem.goods.id] ?: return

            if (existingItem.quantity - 1 <= 0) {
                cartRepository.delete(existingItem.id, {
                    removeFromCartCache(existingItem.goods.id)
                }, {})
            } else {
                updateCartItemQuantity(
                    existingItem.id,
                    cartItem.copy(quantity = existingItem.quantity - 1),
                )
            }
        }
    }

    private fun removeFromCartCache(goodsId: Int) {
        val currentCache = _cartCache.value?.toMutableMap() ?: mutableMapOf()
        currentCache.remove(goodsId)
        _cartCache.value = currentCache
    }

    private fun appendCartItemsWithZeroQuantity() {
        val goodsLoadOffset = (currentPage - 1) * PAGE_SIZE
        goodsRepository.fetchPageGoods(
            limit = PAGE_SIZE,
            offset = goodsLoadOffset,
            onComplete = { goodsResponse ->
                val loadedGoods = _goods.value ?: emptyList()
                val fetchedGoods = getGoodsByGoodsResponse(goodsResponse)
                _goods.value = (loadedGoods + fetchedGoods).toMutableList()
                _isFullLoaded.postValue(goodsResponse.last)
            },
            onFail = { throwable ->
                throw throwable
            },
        )
    }

    private fun getGoodsByGoodsResponse(goodsResponse: GoodsResponse): List<Goods> = goodsResponse.content.map { it.toDomain() }

    private fun addCartItem(cartItem: CartItem) {
        cartRepository.addCartItem(cartItem.goods, 1, { resultCode: Int, cartId: Int ->
            val newCartItem = cartItem.copy(quantity = 1, id = cartId)
            updateCartCacheItem(newCartItem)
        }, {})
    }

    private fun updateCartItemQuantity(
        cartId: Int,
        cartItem: CartItem,
    ) {
        cartRepository.updateQuantity(cartId, CartQuantity(cartItem.quantity), {
            updateCartCacheItem(cartItem)
        }, {})
    }

    private fun updateCartCacheItem(cartItem: CartItem) {
        val currentCache = _cartCache.value?.toMutableMap() ?: mutableMapOf()
        currentCache[cartItem.goods.id] = cartItem
        _cartCache.value = currentCache
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
