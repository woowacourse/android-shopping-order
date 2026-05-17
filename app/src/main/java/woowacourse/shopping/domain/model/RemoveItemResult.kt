package woowacourse.shopping.domain.model

sealed class RemoveItemResult {
    data object Success : RemoveItemResult()

    data object NotFoundItem : RemoveItemResult()
}
