package woowacourse.shopping.data.model.remote

enum class DiscountTypeDto(val value: String) {
    FIXED("fixed"),
    BUY_X_GET_Y("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE("percentage"),
}
