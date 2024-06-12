package woowacourse.shopping.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

abstract class BaseViewModel : ViewModel() {
    private val _error: MutableSingleLiveData<Throwable> = MutableSingleLiveData()
    val error: SingleLiveData<Throwable> = _error

    val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            _error.setValue(throwable)
        }

    fun setError(error: Throwable) = _error.setValue(error)
}
