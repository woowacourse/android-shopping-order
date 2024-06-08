package woowacourse.shopping.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun ViewModel.viewModelLaunch(
    handleException: (Throwable) -> Unit = {},
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

fun <T> ViewModel.viewModelAsync(
    handleException: (Throwable) -> Unit = {},
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> {
    val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            handleException(throwable)
        }
    return viewModelScope.async(
        context = context + exceptionHandler,
        start = start,
        block = block,
    )
}
