package woowacourse.woowacourse.shopping.testfixture

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> runCatchingWithDispatcher(
    dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
    block: suspend () -> T,
): Result<T> =
    runCatching {
        withContext(dispatcher) {
            block()
        }
    }
