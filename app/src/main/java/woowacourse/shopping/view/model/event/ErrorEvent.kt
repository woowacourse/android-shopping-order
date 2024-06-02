package woowacourse.shopping.view.model.event

sealed interface ErrorEvent {
    data object NotKnownError: ErrorEvent

    sealed interface CartEvent : ErrorEvent {
        data object UpdateCartEvent : CartEvent
        data object DeleteCartEvent : CartEvent
        data object AddCartEvent : CartEvent
    }

    sealed interface LoadEvent: ErrorEvent {
        data object MaxPagingDataEvent: LoadEvent
        data object LoadDataEvent: LoadEvent
    }

    fun receiveErrorMessage(): String{
        return when(this){
            CartEvent.AddCartEvent -> ERROR_ADD_CART_ITEM
            CartEvent.DeleteCartEvent -> ERROR_DELETE_CART_ITEM
            CartEvent.UpdateCartEvent -> ERROR_UPDATE_CART_ITEM
            LoadEvent.LoadDataEvent -> ERROR_DATA_LOAD
            LoadEvent.MaxPagingDataEvent -> MAX_PAGING_DAT
            NotKnownError -> ERROR_NOT_KNOWN
        }
    }

    companion object {
        const val ERROR_UPDATE_CART_ITEM = "수량 업데이트에 실패하였습니다.."
        const val ERROR_DELETE_CART_ITEM = "삭제에 실패하였습니다.."
        const val ERROR_ADD_CART_ITEM = "장바구니에 담기지 않았습니다.."
        const val ERROR_DATA_LOAD = "데이터가 없습니다!"
        const val MAX_PAGING_DAT = "모든 데이터가 로드 되었습니다."
        const val ERROR_NOT_KNOWN = "알 수 없는 에러가 발생했습니다.."
    }
}
