package woowacourse.shopping.data.remote.response.addorder

sealed interface AddOrderResponse {
    interface Success {
        data class OrderComplete(val successOrderId: Int) : AddOrderResponse
    }

    interface Failure {
        data class ShortageStock(val errorData: AddOrderErrorBody) : AddOrderResponse
        data class LackOfPoint(val errorData: AddOrderErrorBody) : AddOrderResponse
    }
}
