package woowacourse.shopping.ui.payment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Discount
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentCoupon
import woowacourse.shopping.ui.common.frame.CommonFrame
import woowacourse.shopping.ui.payment.item.CouponList
import woowacourse.shopping.ui.payment.item.OrderBtn
import woowacourse.shopping.ui.payment.item.ReceiptItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun PaymentScreen(
    coupons: List<Coupon>,
    order: Order,
    discount: Discount,
    onOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    CommonFrame(
        headerContent = {
            PaymentHeader(
                onClose = { },
            )
        },
        bodyContent = { PaymentBody(
            coupons = coupons,
            order = order,
            discount = discount,
            onOrder = onOrder,
        ) },
        modifier = modifier
    )
}

@Composable
private fun PaymentHeader(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
                .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = stringResource(R.string.cd_back_button),
            modifier =
                Modifier
                    .size(40.dp)
                    .clickable(onClick = onClose),
            tint = Color.White,
        )
        Spacer(Modifier.padding(12.dp))
        Text(
            text = stringResource(R.string.title_payment),
            fontWeight = FontWeight(500),
            fontSize = 20.sp,
            color = Color.White,
        )
    }
}

@Composable
fun PaymentBody(
    coupons: List<Coupon>,
    order: Order,
    discount: Discount,
    onOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CouponList(
            order = order,
            coupons = coupons,
        )
        HorizontalDivider()
        ReceiptItem(
            order = order,
            discount = discount
        )
        OrderBtn(onOrder = onOrder)
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentScreenPreview() {
    PaymentScreen(
        coupons = listOf(
            FixedCoupon(
                id = 1,
                code = "asd",
                description = "5000원 할인 쿠폰",
                expirationDate = LocalDate.now(),
                discountAmount = 5000,
                minimumAmount = 100000
            ),
            FreeShippingCoupon(
                id = 2,
                code = "fads",
                description = "무료 배송 쿠폰",
                expirationDate = LocalDate.now(),
                minimumAmount = 0
            ),
            PercentCoupon(
                id = 3,
                code = "asd",
                description = "점심시간 15% 할인 쿠폰",
                expirationDate = LocalDate.now(),
                discountPercent = 0.15,
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(14, 0),
            ),
            BuyXGetYCoupon(
                id = 4,
                code = "sdhja",
                description = "3+1 쿠폰",
                expirationDate = LocalDate.now(),
                buyQuantity = 3,
                getQuantity = 1
            )
        ),
        order = Order(
            purchaseProducts = emptyList(),
            shippingFee = 3000,
            currentTime = LocalDateTime.now(),
            isRemoteArea = false
        ),
        discount = Discount(),
        onOrder = {  },
    )
}
