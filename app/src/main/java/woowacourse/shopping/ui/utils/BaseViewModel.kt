package woowacourse.shopping.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.ShowError
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel : ViewModel() {
    protected val _dataError: MutableSingleLiveData<ShowError> = MutableSingleLiveData()
    val dataError: SingleLiveData<ShowError> get() = _dataError


    fun ViewModel.viewModelLaunch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(
            context = context + exceptionHandler,
            start = start,
            block = block,
        )
    }

    private val exceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            _dataError.setValue(DataError.UNKNOWN)
        }
}
