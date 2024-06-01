package woowacourse.shopping.domain.model

interface DataCallback<T> {
    fun onSuccess(result: T)

    fun onFailure(t: Throwable)
}
