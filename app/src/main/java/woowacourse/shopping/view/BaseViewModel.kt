package woowacourse.shopping.view

import androidx.lifecycle.ViewModel
import woowacourse.shopping.utils.exception.OrderException
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.model.event.ErrorEvent

open class BaseViewModel : ViewModel() {
    private val _errorEvent: MutableSingleLiveData<ErrorEvent> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<ErrorEvent> get() = _errorEvent

    protected fun handleException(e: Exception) {
        when (e) {
            is OrderException ->
                _errorEvent.setValue(e.event)
            else -> _errorEvent.setValue(ErrorEvent.NotKnownError)
        }
    }
}
