package woowacourse.shopping.view.base

import androidx.lifecycle.ViewModel
import woowacourse.shopping.utils.exception.AddCartException
import woowacourse.shopping.utils.exception.DeleteCartException
import woowacourse.shopping.utils.exception.MaxPagingDataException
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.utils.exception.OrderItemsException
import woowacourse.shopping.utils.exception.UpdateCartException
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData

open class BaseViewModel : ViewModel() {
    private val _errorEvent: MutableSingleLiveData<ErrorEvent> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<ErrorEvent> get() = _errorEvent

    protected fun handleException(t: Throwable) {
        val errorEvent =
            when (t) {
                is NoSuchDataException -> ErrorEvent.LoadDataEvent()
                is UpdateCartException -> ErrorEvent.UpdateCartEvent()
                is DeleteCartException -> ErrorEvent.DeleteCartEvent()
                is AddCartException -> ErrorEvent.AddCartEvent()
                is MaxPagingDataException -> ErrorEvent.MaxPagingDataEvent()
                is OrderItemsException -> ErrorEvent.OrderItemsEvent()
                else -> ErrorEvent.NotKnownError()
            }
        _errorEvent.setValue(errorEvent)
    }
}
