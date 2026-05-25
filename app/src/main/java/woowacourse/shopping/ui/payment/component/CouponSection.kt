package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.ui.common.theme.PrimaryColor

@Composable
fun CouponSection(
    coupons: List<Coupon>,
    items: List<CartItem>,
    shippingFee: Long,
    selectedCouponId: Long?,
    onCouponSelected: (Long) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = "적용 가능한 쿠폰",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "쿠폰은 1개만 적용 가능합니다.",
            fontSize = 12.sp,
            color = Color.Gray,
        )
        Spacer(modifier = Modifier.height(16.dp))

        coupons.forEachIndexed { index, coupon ->
            val discountAmount = coupon.applicableDiscount(items = items, shippingFee = shippingFee)
            CouponItem(
                coupon = coupon,
                discountAmount = discountAmount,
                isSelected = coupon.id == selectedCouponId,
                onClick = { onCouponSelected(coupon.id) },
            )
            if (index < coupons.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CouponItem(
    coupon: Coupon,
    discountAmount: Long,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val isEnabled = discountAmount > 0
    val textColor = if (isEnabled) Color.Black else Color.Red

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(enabled = isEnabled, onClick = onClick),
        verticalAlignment = Alignment.Top,
    ) {
        CustomCheckbox(
            checked = isSelected,
            enabled = isEnabled,
            onClick = onClick,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = coupon.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = textColor,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${coupon.code} · 만료일 ${coupon.expiryDate}",
                fontSize = 12.sp,
                color = if (isEnabled) Color.Gray else Color.Red,
            )
            if (!isEnabled) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "현재 주문에는 할인 금액이 없습니다.",
                    fontSize = 12.sp,
                    color = Color.Red,
                )
            }
        }
    }
}

@Composable
private fun CustomCheckbox(
    checked: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (checked && enabled) PrimaryColor else Color.White)
                .border(
                    width = 1.5.dp,
                    color =
                        when {
                            checked && enabled -> PrimaryColor
                            enabled -> Color.Gray
                            else -> Color.Red
                        },
                    shape = RoundedCornerShape(4.dp),
                ).clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (checked && enabled) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}
