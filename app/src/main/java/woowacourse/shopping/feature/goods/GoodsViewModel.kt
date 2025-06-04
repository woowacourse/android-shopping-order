package woowacourse.shopping.feature.goods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.carts.CartFetchError
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

    private var _totalCartItemSize: MutableLiveData<String> = MutableLiveData(CART_EMPTY)
    val totalCartItemSize: LiveData<String> get() = _totalCartItemSize

    private val _navigateToCart = MutableSingleLiveData<Unit>()
    val navigateToCart: SingleLiveData<Unit> get() = _navigateToCart

    private val _recentlyViewedGoods: MutableLiveData<List<Goods>> = MutableLiveData()
    val recentlyViewedGoods: LiveData<List<Goods>> get() = _recentlyViewedGoods

    private val _navigateToLogin = MutableSingleLiveData<Unit>()
    val navigateToLogin: SingleLiveData<Unit> get() = _navigateToLogin

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableSingleLiveData<String>()
    val toastMessage: SingleLiveData<String> get() = _toastMessage

    private var cashedCartItems: MutableMap<Int, CartItem> = mutableMapOf()

    init {
        appendCartItemsWithZeroQuantity()
        updateRecentlyViewedGoods()
    }

    fun findCart(goods: Goods): CartItem? = cashedCartItems.values.find { it.goods.id == goods.id }

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> =
        cartResponse.toCartItems()

    fun login(basicKey: String) {
        Authorization.setBasicKey(basicKey)
        cartRepository.checkValidBasicKey(basicKey, { response ->
            Authorization.setLoginStatus(response == 200)
        }, {
            _toastMessage.postValue(TOAST_FAIL_LOGIN)
        })
    }

    fun onCartClicked() {
        if (!Authorization.isLogin) {
            _navigateToLogin.setValue(Unit)
        } else {
            _navigateToCart.setValue(Unit)
        }
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
            onFail = {
                _toastMessage.postValue(TOAST_FAIL_CART_LOAD)
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
            cashedCartItems = cartItems.associateBy({ it.goods.id }, { it }).toMutableMap()
            setTotalCartItemSize(cartItems.sumOf { it.quantity })
            bindCartCache()
        }, {
            _toastMessage.postValue(TOAST_FAIL_CART_LOAD)
        })
    }

    private fun bindCartCache() {
        val newList = goods.map { cashedCartItems[it.id] ?: CartItem(goods = it, quantity = 0) }
        if (_goodsWithCartQuantity.value != newList) {
            _goodsWithCartQuantity.postValue(newList)
        }
    }

    private fun setTotalCartItemSize(totalCartQuantity: Int) {
        val sizeText = when {
            totalCartQuantity < 1 -> CART_EMPTY
            totalCartQuantity in 1..99 -> totalCartQuantity.toString()
            else -> CART_MAX
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
                    }, {
                        _toastMessage.postValue(TOAST_FAIL_CART_DELETE)
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
        return goodsResponse.content.map { it.toDomain() }
    }

    private fun addCartItem(cartItem: CartItem) {
        cartRepository.addCartItem(cartItem.goods, 1, {
            cashedCartItems[cartItem.goods.id] = cartItem.copy(quantity = 1)
            bindCartCache()
        }, {
            _toastMessage.postValue(TOAST_FAIL_CART_ADD)
        })
    }

    private fun updateCartItemQuantity(
        cartId: Int,
        cartItem: CartItem,
    ) {
        cartRepository.updateQuantity(cartId, CartQuantity(cartItem.quantity), {
            cashedCartItems.replace(cartItem.goods.id, cartItem)
            bindCartCache()
        }, {
            _toastMessage.postValue(TOAST_FAIL_CART_UPDATE)
        })
    }

    companion object {
        private const val PAGE_SIZE = 20

        private const val CART_EMPTY = "0"
        private const val CART_MAX = "99+"

        private const val TOAST_FAIL_LOGIN = "로그인에 실패하였습니다."
        private const val TOAST_FAIL_CART_LOAD = "카트 아이템을 불러오는 데에 실패했습니다."
        private const val TOAST_FAIL_CART_ADD = "카트 아이템 추가에 실패했습니다."
        private const val TOAST_FAIL_CART_UPDATE = "카트 수량 변경에 실패했습니다."
        private const val TOAST_FAIL_CART_DELETE = "카트 아이템 삭제에 실패했습니다."
    }
}