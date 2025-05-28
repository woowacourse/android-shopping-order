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
    private var _totalCartItemSize: MutableLiveData<String> = MutableLiveData("0")
    val totalCartItemSize: LiveData<String> get() = _totalCartItemSize
    private val _navigateToCart = MutableSingleLiveData<Unit>()
    val navigateToCart: SingleLiveData<Unit> get() = _navigateToCart
    private val _recentlyViewedGoods: MutableLiveData<List<Goods>> = MutableLiveData()
    val recentlyViewedGoods: LiveData<List<Goods>> get() = _recentlyViewedGoods

    private val _navigateToLogin = MutableSingleLiveData<Unit>()
    val navigateToLogin: SingleLiveData<Unit> get() = _navigateToLogin

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private var cashedCartItems: MutableMap<Int, CartItem> = mutableMapOf()

    init {
        appendCartItemsWithZeroQuantity()
        updateRecentlyViewedGoods()
    }

    fun findCart(goods: Goods): CartItem? = cashedCartItems.values.find { it.goods.id == goods.id }

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> = cartResponse.toCartItems()

    fun login(basicKey: String) {
        Authorization.setBasicKey(basicKey)
        cartRepository.checkValidBasicKey { code ->
            when {
                code == 200 -> Authorization.setLoginStatus(true)
                else -> Authorization.setLoginStatus(false)
            }
        }
    }

    fun onCartClicked() {
        _navigateToCart.setValue(Unit)
    }

    private fun appendCartItemsWithZeroQuantity() {
        val goodsLoadOffset = (page - 1) * PAGE_SIZE
        goodsRepository.fetchPageGoods(
            limit = PAGE_SIZE,
            offset = goodsLoadOffset,
            onComplete = { goodsResponse ->
                val fetchedGoods = getGoodsByGoodsResponse(goodsResponse)
                goods.addAll(fetchedGoods)
                _isFullLoaded.postValue(goodsResponse.last)
                _goodsWithCartQuantity.postValue(goods.map { CartItem(goods = it, quantity = 0) })
                _isLoading.postValue(false)
            },
            onFail = { throwable ->
                throw (throwable)
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
            cashedCartItems =
                cartItems
                    .associateBy(
                        { it.goods.id },
                        { it },
                    ).toMutableMap()

            setTotalCartItemSize(cartItems.sumOf { it.quantity })
            bindCartCache()
        }, {})
    }

    private fun bindCartCache() {
        val newList = goods.map { cashedCartItems[it.id] ?: CartItem(goods = it, quantity = 0) }
        if (_goodsWithCartQuantity.value != newList) {
            _goodsWithCartQuantity.postValue(newList)
        }
    }

    private fun setTotalCartItemSize(totalCartQuantity: Int) {
        val sizeText =
            when {
                totalCartQuantity < 1 -> "0"
                totalCartQuantity in 1..99 -> totalCartQuantity.toString()
                else -> "99+"
            }
        _totalCartItemSize.postValue(sizeText)
    }

    fun addPage() {
        page++
        appendCartItemsWithZeroQuantity()
    }

    fun addCartItemOrIncreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _navigateToLogin.setValue(Unit)
        } else {
            val cashedCartItem = cashedCartItems[cartItem.goods.id]
            if (cashedCartItem == null) {
                addCartItem(cartItem)
            } else {
                updateCartItemQuantity(
                    cashedCartItem.id,
                    cartItem.copy(quantity = cashedCartItem.quantity + 1),
                )
            }
        }
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _navigateToLogin.setValue(Unit)
        } else {
            cashedCartItems[cartItem.goods.id]?.let { cartItemWillRemove ->
                if (cartItemWillRemove.quantity - 1 <= 0) {
                    cartRepository.delete(cartItemWillRemove.id, {
                        cashedCartItems.remove(cartItemWillRemove.goods.id)
                        bindCartCache()
                    })
                } else {
                    updateCartItemQuantity(
                        cartItemWillRemove.id,
                        cartItem.copy(quantity = cartItemWillRemove.quantity - 1),
                    )
                }
            }
        }
    }

    private fun getGoodsByGoodsResponse(goodsResponse: GoodsResponse): List<Goods> {
        val contents = goodsResponse.content
        return contents.map { it.toDomain() }
    }

    private fun addCartItem(cartItem: CartItem) {
        cartRepository.addCartItem(cartItem.goods, 1, {
            cashedCartItems[cartItem.goods.id] = cartItem.copy(quantity = 1)
            bindCartCache()
        }, {})
    }

    private fun updateCartItemQuantity(
        cartId: Int,
        cartItem: CartItem,
    ) {
        cartRepository.updateQuantity(cartId, CartQuantity(cartItem.quantity), {
            cashedCartItems.replace(cartItem.goods.id, cartItem)
            bindCartCache()
        }, {
        })
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
