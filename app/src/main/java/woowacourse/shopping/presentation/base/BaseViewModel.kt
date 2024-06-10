package woowacourse.shopping.presentation.base

import android.database.SQLException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel : ViewModel() {
    private val _message: MutableLiveData<Event<MessageProvider>> = MutableLiveData()
    val message: LiveData<Event<MessageProvider>> get() = _message

    private val _error: MutableLiveData<ErrorState?> = MutableLiveData(null)
    val error: LiveData<ErrorState?> get() = _error

    private val _loading: MutableLiveData<LoadingProvider?> = MutableLiveData(null)
    val loading: LiveData<LoadingProvider?> get() = _loading

    protected fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        val exceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                handleException(throwable)
            }
        return viewModelScope.launch(
            context = context + exceptionHandler,
            start = start,
            block = block,
        )
    }

    private fun handleException(throwable: Throwable) {
        showError(throwable)
    }

    abstract fun retry()

    fun showMessage(messageProvider: MessageProvider) {
        _message.emit(messageProvider)
    }

    private fun handleError(
        e: Throwable,
        onUnhandledError: () -> Unit = {},
    ) {
        Log.d("Ttt e", e.toString())
        when (e) {
            is IllegalArgumentException -> showError("잘못된 요청", "잘못된 요청입니다.")
            is SecurityException -> showError("보안 오류", "인증 오류가 발생했습니다.")
            is NoSuchElementException -> showError("요소 없음", "요청한 요소를 찾을 수 없습니다.")
            is IOException -> showError("서버 오류", "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            is TimeoutException -> showError("시간 초과 오류", "작업 시간이 초과되었습니다. 다시 시도해주세요.")
            is SQLException -> showError("데이터베이스 오류", "데이터베이스에 접근하는 중 오류가 발생했습니다.")
            else -> {
                if (onUnhandledError == {}) {
                    showError("알 수 없는 오류", "알 수 없는 오류가 발생했습니다. 다시 시도해주세요.")
                } else {
                    onUnhandledError()
                }
            }
        }
    }

    fun showError(e: Throwable) {
        handleError(e)
    }

    private fun showError(
        title: String,
        description: String,
    ) {
        val errorState = ErrorState(title = title, description = description)
        _error.postValue(errorState)
    }

    fun hideError() {
        _error.postValue(null)
    }

    fun showLoading(loadingProvider: LoadingProvider) {
        _loading.postValue(loadingProvider)
    }

    fun hideLoading() {
        _loading.postValue(null)
    }
}
