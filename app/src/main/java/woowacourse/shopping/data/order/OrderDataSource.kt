package woowacourse.shopping.data.order

interface OrderDataSource {
    fun order(onSuccess: () -> Unit, onFailure: () -> Unit)
}
