package woowacourse.shopping.view.recommend

interface CouponEvent {
    sealed interface ErrorEvent : CouponEvent {
        data object NotKnownError : ErrorEvent
    }

    sealed interface SelectCoupon : CouponEvent {
        data object Success : SelectCoupon

        data object InvalidDate : SelectCoupon

        data object InvalidPrice : SelectCoupon

        data object InvalidCount : SelectCoupon
    }

    sealed interface SuccessEvent : CouponEvent

    sealed interface UpdateCouponEvent : CouponEvent {
        data object Success : UpdateCouponEvent, SuccessEvent

        data object Fail : UpdateCouponEvent, ErrorEvent
    }

    sealed interface OrderRecommends : CouponEvent {
        data object Success : OrderRecommends, SuccessEvent

        data object Fail : OrderRecommends, ErrorEvent
    }
}
