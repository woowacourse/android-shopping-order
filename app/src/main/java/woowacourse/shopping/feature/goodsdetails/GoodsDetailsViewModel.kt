package woowacourse.shopping.feature.goodsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.feature.CartUiModel
import woowacourse.shopping.feature.GoodsUiModel
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.toDomain

sealed class GoodsUiEvent {
    data class ShowToast(val messageKey: ToastMessageKey) : GoodsUiEvent()
    data class CartAddSuccess(val quantity: Int) : GoodsUiEvent()
    data class ShowMostRecentlyViewed(val goods: Goods) : GoodsUiEvent()
    data object ClickMostRecentlyViewed : GoodsUiEvent()
}

enum class ToastMessageKey {
    FAIL_CART_ADD,
    FAIL_CART_UPDATE
}

class GoodsDetailsViewModel(
    private val goodsUiModel: GoodsUiModel,
    private val cartUiModel: CartUiModel?,
    private val cartRepository: CartRepository,
    private val goodsRepository: GoodsRepository,
) : ViewModel() {

    private val _cartItem: MutableLiveData<CartItem> =
        MutableLiveData(CartItem(goodsUiModel.toDomain(), 1))
    val cartItem: LiveData<CartItem> get() = _cartItem

    private val _event = MutableSingleLiveData<GoodsUiEvent>()
    val event: SingleLiveData<GoodsUiEvent> get() = _event

    private val _mostRecentlyViewedGoods = MutableLiveData<Goods>()
    val mostRecentlyViewedGoods: LiveData<Goods> get() = _mostRecentlyViewedGoods

    fun initMostRecentlyViewedGoods() {
        viewModelScope.launch {
            val recent = goodsRepository.fetchMostRecentGoods()
            recent?.let {
                if (goodsUiModel.id != it.id) {
                    _event.setValue(GoodsUiEvent.ShowMostRecentlyViewed(it))
                }
            }
            goodsRepository.loggingRecentGoods(goodsUiModel.toDomain())
        }
    }

    fun increaseSelectorQuantity() {
        _cartItem.value?.let {
            _cartItem.value = it.copy(quantity = it.quantity + 1)
        }
    }

    fun decreaseSelectorQuantity() {
        _cartItem.value?.let {
            if (it.quantity > 1) {
                _cartItem.value = it.copy(quantity = it.quantity - 1)
            }
        }
    }

    fun addToCart() {
        cartItem.value?.let { item ->
            viewModelScope.launch {
                try {
                    if (cartUiModel != null) {
                        cartRepository.updateQuantity(
                            cartUiModel.cartId,
                            CartQuantity(cartUiModel.cartQuantity + item.quantity)
                        )
                    } else {
                        cartRepository.addCartItem(item.goods, item.quantity)
                    }
                    handleCartAdded(item.quantity)
                } catch (e: Exception) {
                    val key = if (cartUiModel != null) ToastMessageKey.FAIL_CART_UPDATE else ToastMessageKey.FAIL_CART_ADD
                    _event.setValue(GoodsUiEvent.ShowToast(key))
                }
            }
        }
    }

    private fun handleCartAdded(quantity: Int) {
        _event.setValue(GoodsUiEvent.CartAddSuccess(quantity))
        _cartItem.value = _cartItem.value?.copy(quantity = 1)
    }

    fun handleClickMostRecentlyGoodsSection() {
        _event.setValue(GoodsUiEvent.ClickMostRecentlyViewed)
    }
}
