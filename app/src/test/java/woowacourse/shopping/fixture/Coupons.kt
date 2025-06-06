package woowacourse.shopping.fixture

import woowacourse.shopping.domain.coupon.BuyXGetY
import woowacourse.shopping.domain.coupon.Fixed
import woowacourse.shopping.domain.coupon.FreeShipping
import woowacourse.shopping.domain.coupon.Percentage
import java.time.LocalDate

val FIXED =
    Fixed(
        description = "",
        code = "",
        explanationDate = LocalDate.of(2022, 1, 2),
        id = 0,
        discount = 5000000,
        minimumAmount = 0,
    )

val FREE_SHIPPING =
    FreeShipping(
        description = "",
        code = "",
        explanationDate = LocalDate.of(2022, 1, 2),
        id = 0,
        minimumAmount = 10000000,
    )

val PERCENTAGE =
    Percentage(
        description = "",
        code = "",
        explanationDate = LocalDate.of(2022, 1, 2),
        id = 0,
        discount = 30,
    )

val BUY_X_GET_Y =
    BuyXGetY(
        description = "",
        code = "",
        explanationDate = LocalDate.of(2022, 1, 2),
        id = 0,
        buyQuantity = 2,
        getQuantity = 1,
    )
