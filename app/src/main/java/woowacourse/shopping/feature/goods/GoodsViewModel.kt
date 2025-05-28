package woowacourse.shopping.feature.goods

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
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

    var cashedCartItemWithIndex: Map<Int, Pair<CartItem, Int>> = mapOf()

    init {
        appendCartItemsWithZeroQuantity()
        updateRecentlyViewedGoods()
    }

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
            onComplete = { fetchedGoods ->
                goods.addAll(fetchedGoods)
                goodsRepository.fetchGoodsSize { totalSize ->
                    _isFullLoaded.postValue(page * PAGE_SIZE >= totalSize)
                }
                _goodsWithCartQuantity.postValue(goods.map { CartItem(goods = it, quantity = 0) })
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

    fun updateCartCache() {
        cartRepository.fetchAllCartItems({ cartItems ->
            cashedCartItemWithIndex =
                cartItems
                    .mapIndexed { index, cartItem ->
                        cartItem.goods.id to Pair(cartItem, index)
                    }.toMap()

            val newList = goods.map { goods ->
                cashedCartItemWithIndex[goods.id]?.first ?: CartItem(goods = goods, quantity = 0)
            }

            if (_goodsWithCartQuantity.value != newList) {
                _goodsWithCartQuantity.value = newList
            }

            setTotalCartItemSize(cartItems.sumOf { it.quantity })
        }, {})
    }

    // todo api의 totalsize로 수정
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
            val cashedCartItem = cashedCartItemWithIndex[cartItem.goods.id]
            Log.d("Test",cashedCartItem.toString())
            if(cashedCartItem==null){
                addCartItem(cartItem.goods)
            }
            else{
                increaseCartItem(cashedCartItem.first.id, cartItem.copy(quantity = cashedCartItem.first.quantity + 1))
            }
            updateCartCache()

        }
    }

    private fun addCartItem(goods: Goods){
        cartRepository.addCartItem(goods)
    }

    private fun increaseCartItem(
        cartId: Int,
        cartItem: CartItem,
    ) {
        cartRepository.updateQuantity(cartId, CartQuantity(cartItem.quantity), {}, {})
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (!Authorization.isLogin) {
            _navigateToLogin.setValue(Unit)
        } else {
            // 일단 증가만 구현
            Log.d("카트아이템", "$cartItem")
            Log.d("캐시", "$cashedCartItemWithIndex")
            cashedCartItemWithIndex[cartItem.goods.id]?.first?.let {
                if(it.quantity-1<=0){
                    cartRepository.delete(it.id,{})
                }
                else{
                    increaseCartItem(it.id, cartItem.copy(quantity = it.quantity - 1))
                }
            }
            updateCartCache()
        }
        cartRepository.removeOrDecreaseQuantity(cartItem.goods, cartItem.quantity) {
            updateCartCache()
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
