package woowacourse.shopping.data.remote.dto

data class Message<T>(
    val code: Int,
    val body: T?,
) {
    val isOk get() = code == OK

    companion object {
        private const val OK = 200
    }
}
