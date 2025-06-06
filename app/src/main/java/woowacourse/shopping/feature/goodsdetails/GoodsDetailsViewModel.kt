package woowacourse.shopping.feature.goodsdetails

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.feature.GoodsUiModel
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.toDomain

class GoodsDetailsViewModel(
    private val goodsUiModel: GoodsUiModel,
    private val cartRepository: CartRepository,
    private val goodsRepository: GoodsRepository,
    private var cartId: Int,
) : ViewModel() {
    private val _cartItem: MutableLiveData<CartItem> =
        MutableLiveData(CartItem(goodsUiModel.toDomain(), 1))
    val cartItem: LiveData<CartItem> get() = _cartItem
    private val _alertEvent = MutableSingleLiveData<GoodsDetailsAlertMessage>()
    val alertEvent: SingleLiveData<GoodsDetailsAlertMessage> = _alertEvent
    private val _mostRecentlyViewedGoods: MutableLiveData<Goods?> = MutableLiveData(null)
    val mostRecentlyViewedGoods: LiveData<Goods?> get() = _mostRecentlyViewedGoods
    private val _clickMostRecentlyGoodsEvent = MutableSingleLiveData<Goods>()
    val clickMostRecentlyGoodsEvent: SingleLiveData<Goods> get() = _clickMostRecentlyGoodsEvent

    fun initMostRecentlyViewedGoods() {
        viewModelScope.launch {
            val goods = goodsRepository.fetchMostRecentGoods()
            goods?.let {
                if (goodsUiModel.id != goods.id) _mostRecentlyViewedGoods.postValue(it)
            }
            loggingRecentViewedGoods(goodsUiModel.toDomain())
        }
    }

    fun increaseSelectorQuantity() {
        _cartItem.value?.let { currentItem ->
            _cartItem.value = currentItem.copy(quantity = currentItem.quantity + 1)
        }
    }

    fun decreaseSelectorQuantity() {
        _cartItem.value?.let { currentItem ->
            if (currentItem.quantity > 1) {
                _cartItem.value = currentItem.copy(quantity = currentItem.quantity - 1)
            }
        }
    }

    fun addOrUpdateQuantityToCart() {
        cartItem.value?.let { item ->
            viewModelScope.launch {
                if (cartId == NULL_CART_ID) {
                    addCartItem(item)
                } else {
                    updateCartItem(item)
                }
            }
        }
    }

    private suspend fun updateCartItem(item: CartItem) {
        val result = cartRepository.updateQuantity(cartId, CartQuantity(item.quantity))
        when (result) {
            is CartUpdateResult.Success -> {
                alertMessageEvent(
                    R.string.goods_detail_cart_update_complete_toast_message,
                    item.quantity,
                )
            }

            is CartUpdateResult.Error -> {
                // todo
            }
        }
    }

    private fun addCartItem(item: CartItem) {
        viewModelScope.launch {
            val result = cartRepository.addCartItem(item.goods, item.quantity)
            when (result) {
                is CartFetchResult.Error -> TODO()
                is CartFetchResult.Success -> {
                    alertMessageEvent(R.string.goods_detail_cart_insert_complete_toast_message, item.quantity)
                    cartId = result.data.cartId
                }
            }
        }
    }

    private fun alertMessageEvent(
        @StringRes
        messageId: Int,
        quantity: Int,
    ) {
        _alertEvent.setValue(
            GoodsDetailsAlertMessage(
                messageId,
                quantity,
            ),
        )
    }

    fun onClickMostRecentlyGoodsSection() {
        _mostRecentlyViewedGoods.value?.let {
            _clickMostRecentlyGoodsEvent.setValue(it)
        }
    }

    fun loggingRecentViewedGoods(goods: Goods) {
        viewModelScope.launch {
            try {
                goodsRepository.loggingRecentGoods(goods)
            } catch (e: Exception) {
                // todo(최근 본 항목 로깅 실패 처리)
            }
        }
    }

    companion object {
        const val NULL_CART_ID = -1
    }
}
