package woowacourse.shopping.data.repository

interface DataCallback<T> {
    fun onSuccess(result: T)

    fun onFailure(t: Throwable)
}
