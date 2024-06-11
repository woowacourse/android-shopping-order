package woowacourse.shopping.view

import androidx.lifecycle.ViewModel
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData

open class BaseViewModel : ViewModel() {
    private val _errorEvent: MutableSingleLiveData<ErrorEvent> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<ErrorEvent> get() = _errorEvent

    protected fun handleException(t: Throwable) {
        when (t) {
            is ErrorEvent ->
                _errorEvent.setValue(t)
            else -> _errorEvent.setValue(ErrorEvent.NotKnownError())
        }
    }
}
