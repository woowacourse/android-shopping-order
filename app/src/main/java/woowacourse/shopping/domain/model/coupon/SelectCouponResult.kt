package woowacourse.shopping.domain.model.coupon

interface SelectCouponResult {
    data object InValidDate : SelectCouponResult

    data object InValidCount : SelectCouponResult

    data object InValidPrice : SelectCouponResult

    data object Valid : SelectCouponResult
}
