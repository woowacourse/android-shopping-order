package woowacourse.shopping.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.presentation.common.ErrorType
import woowacourse.shopping.presentation.common.EventState

open class BaseViewModel : ViewModel() {
    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    fun showError(errorType: ErrorType) {
        _errorHandler.value = EventState(errorType)
    }
}
