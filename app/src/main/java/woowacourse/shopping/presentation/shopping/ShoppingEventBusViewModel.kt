package woowacourse.shopping.presentation.shopping

import androidx.lifecycle.ViewModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class ShoppingEventBusViewModel : ViewModel() {
    private val _updateCartEvent = MutableSingleLiveData<Unit>()
    val updateCartEvent: SingleLiveData<Unit> get() = _updateCartEvent
    private val _refreshCartEvent = MutableSingleLiveData<Unit>()
    val refreshCartEvent: SingleLiveData<Unit> get() = _refreshCartEvent

    private val _updateRecentProductEvent = MutableSingleLiveData<Unit>()
    val updateRecentProductEvent: SingleLiveData<Unit> get() = _updateRecentProductEvent

    fun sendUpdateCartEvent() {
        _updateCartEvent.setValue(Unit)
    }

    fun sendRefreshCartEvent() {
        _refreshCartEvent.setValue(Unit)
    }

    fun sendUpdateRecentProductEvent() {
        _updateRecentProductEvent.setValue(Unit)
    }
}
