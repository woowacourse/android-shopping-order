package woowacourse.shopping.view.base

sealed class ErrorEvent : Exception() {
    class UpdateCartEvent : ErrorEvent()

    class DeleteCartEvent : ErrorEvent()

    class AddCartEvent : ErrorEvent()

    class LoadDataEvent : ErrorEvent()

    class MaxPagingDataEvent : ErrorEvent()

    class NotKnownError : ErrorEvent()

    class OrderItemsEvent : ErrorEvent()

    fun receiveErrorMessage(): String {
        return when (this) {
            is AddCartEvent -> ERROR_ADD_CART_ITEM
            is DeleteCartEvent -> ERROR_DELETE_CART_ITEM
            is UpdateCartEvent -> ERROR_UPDATE_CART_ITEM
            is LoadDataEvent -> ERROR_DATA_LOAD
            is MaxPagingDataEvent -> MAX_PAGING_DATA
            is NotKnownError -> ERROR_NOT_KNOWN
            is OrderItemsEvent -> ERROR_ORDER
        }
    }

    companion object {
        private const val ERROR_UPDATE_CART_ITEM = "수량 업데이트에 실패하였습니다."
        private const val ERROR_DELETE_CART_ITEM = "삭제에 실패하였습니다."
        private const val ERROR_ADD_CART_ITEM = "장바구니에 담기지 않았습니다."
        private const val ERROR_DATA_LOAD = "데이터가 없습니다!"
        private const val MAX_PAGING_DATA = "모든 데이터가 로드 되었습니다."
        private const val ERROR_NOT_KNOWN = "알 수 없는 에러가 발생했습니다."
        private const val ERROR_ORDER = "주문에 실패하였습니다."
    }
}
