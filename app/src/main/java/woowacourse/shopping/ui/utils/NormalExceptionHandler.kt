package woowacourse.shopping.ui.utils

import kotlinx.coroutines.CoroutineExceptionHandler

fun exceptionHandler() = CoroutineExceptionHandler { _, exception -> throw exception }
