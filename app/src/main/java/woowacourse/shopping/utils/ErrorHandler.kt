package woowacourse.shopping.utils

object ErrorHandler {
    fun printError(throwable: Throwable) {
        throwable.printStackTrace()
        print(throwable.message)
        println(throwable.stackTraceToString())
    }
}
