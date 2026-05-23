package woowacourse.shopping.ui.payment.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentCoupon
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import androidx.compose.ui.res.stringResource
import woowacourse.shopping.R

@Composable
fun CouponList(
    order: Order,
    coupons: List<Coupon>,
    selectedCoupon: Coupon?,
    onCouponSelect: (Coupon?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(550.dp)
            .padding(all = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.label_applicable_coupons),
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            color = Color(0xFF333333)
        )
        Text(
            text = stringResource(R.string.label_coupon_limit_info),
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            color = Color(0xFF555555)
        )
        LazyColumn(
            modifier = Modifier
                .height(350.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = coupons, key = { it.id }) { coupon ->
                if (coupon.isEligible(order)) {
                    CouponItem(
                        coupon = coupon,
                        isSelected = selectedCoupon?.id == coupon.id,
                        onSelect = { onCouponSelect(it) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CouponListPreview() {
    CouponList(
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
        selectedCoupon = null,
        onCouponSelect = {  },
    )
}
