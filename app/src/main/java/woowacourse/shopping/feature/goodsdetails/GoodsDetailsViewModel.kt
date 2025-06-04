package woowacourse.shopping.feature.goodsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

sealed class UiEvent {
    data class ShowToast(val messageKey: ToastMessageKey) : UiEvent()
    data class CartAddSuccess(val quantity: Int) : UiEvent()
    data class ShowMostRecentlyViewed(val goods: Goods) : UiEvent()
    data object ClickMostRecentlyViewed : UiEvent()
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

    private val _event = MutableSingleLiveData<UiEvent>()
    val event: SingleLiveData<UiEvent> get() = _event

    private val _mostRecentlyViewedGoods = MutableLiveData<Goods>()
    val mostRecentlyViewedGoods: LiveData<Goods> get() = _mostRecentlyViewedGoods

    fun initMostRecentlyViewedGoods() {
        goodsRepository.fetchMostRecentGoods { goods ->
            goods?.let {
                if (goodsUiModel.id != goods.id) {
                    _event.setValue(UiEvent.ShowMostRecentlyViewed(it))
                }
            }
            loggingRecentViewedGoods(goodsUiModel.toDomain())
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
            if (cartUiModel != null) {
                cartRepository.updateQuantity(
                    cartUiModel.cartId,
                    CartQuantity(cartUiModel.cartQuantity + item.quantity),
                    { onCartAdded(item.quantity) },
                    { _event.setValue(UiEvent.ShowToast(ToastMessageKey.FAIL_CART_UPDATE)) },
                )
            } else {
                cartRepository.addCartItem(
                    item.goods,
                    item.quantity,
                    { onCartAdded(item.quantity) },
                    { _event.setValue(UiEvent.ShowToast(ToastMessageKey.FAIL_CART_ADD)) },
                )
            }
        }
    }

    private fun onCartAdded(quantity: Int) {
        _event.setValue(UiEvent.CartAddSuccess(quantity))
        _cartItem.value = _cartItem.value?.copy(quantity = 1)
    }

    fun onClickMostRecentlyGoodsSection() {
        _event.setValue(UiEvent.ClickMostRecentlyViewed)
    }

    fun loggingRecentViewedGoods(goods: Goods) {
        goodsRepository.loggingRecentGoods(goods) {}
    }
}
