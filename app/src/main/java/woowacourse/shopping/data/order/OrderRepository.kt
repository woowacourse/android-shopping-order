package woowacourse.shopping.data.order

interface OrderRepository {
    fun order(onSuccess: () -> Unit, onFailure: () -> Unit)
}
