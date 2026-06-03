package woowacourse.shopping.data.remote.server.dto.coupon.item

enum class DiscountType(val content: String) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage");
}