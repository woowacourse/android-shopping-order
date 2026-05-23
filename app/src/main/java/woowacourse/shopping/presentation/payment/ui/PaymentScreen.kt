package woowacourse.shopping.presentation.payment.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.R
import woowacourse.shopping.presentation.common.components.ShoppingAppBar
import woowacourse.shopping.presentation.payment.model.CouponUiModel
import woowacourse.shopping.presentation.payment.ui.components.CouponSelector
import woowacourse.shopping.presentation.payment.ui.components.CouponTitle
import woowacourse.shopping.presentation.payment.ui.components.PaymentBottomBar
import woowacourse.shopping.presentation.payment.ui.components.PaymentSummary
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.util.formattedPrice

@Composable
fun PaymentScreen(
    orderAmount: Long,
    coupons: ImmutableList<CouponUiModel>,
    selectedCouponId: Long?,
    onSelectCoupon: (Long) -> Unit,
    onBackClick: () -> Unit,
    onPayClick: () -> Unit,
    discountAmount: Long,
    deliveryFee: Int,
    totalAmount: Long,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.pay),
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(32.dp)
                                .clickable { onBackClick() },
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = stringResource(R.string.pay),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                },
            )
        },
        bottomBar = {
            PaymentBottomBar(
                onClick = onPayClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            CouponSection(
                coupons = coupons,
                selectedCouponId = selectedCouponId,
                onSelectCoupon = { onSelectCoupon(it) },
                modifier = Modifier.padding(horizontal = 18.dp),
            )
            HorizontalDivider(thickness = 2.dp)
            PaymentSummary(
                orderAmount = formattedPrice(orderAmount),
                discountAmount = formattedPrice(discountAmount),
                deliveryFee = formattedPrice(deliveryFee.toLong()),
                totalPayAmount = formattedPrice(totalAmount),
            )
        }
    }
}

@Composable
private fun CouponSection(
    coupons: ImmutableList<CouponUiModel>,
    selectedCouponId: Long?,
    onSelectCoupon: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        CouponTitle()
        CouponSelector(
            modifier =
                Modifier
                    .height(300.dp)
                    .padding(vertical = 20.dp),
            coupons = coupons,
            selectedCouponId = selectedCouponId,
            onSelect = { onSelectCoupon(it) },
        )
    }
}

@Preview
@Composable
private fun PaymentScreenPreview() {
    val coupons =
        listOf(
            CouponUiModel(
                id = 1L,
                name = "5,000원 할인 쿠폰",
                expiredDate = "2026년 5월 22일",
                minPayAmount = null,
            ),
            CouponUiModel(
                id = 2L,
                name = "5,000원 할인 쿠폰",
                expiredDate = "2026년 5월 22일",
                minPayAmount = "10000",
            ),
            CouponUiModel(
                id = 3L,
                name = "5,000원 할인 쿠폰",
                expiredDate = "2026년 5월 22일",
                minPayAmount = "10000",
            ),
            CouponUiModel(
                id = 4L,
                name = "5,000원 할인 쿠폰",
                expiredDate = "2026년 5월 22일",
                minPayAmount = "10000",
            ),
        ).toImmutableList()

    AndroidshoppingTheme {
        PaymentScreen(
            selectedCouponId = 2L,
            orderAmount = 1000,
            onBackClick = {},
            onPayClick = {},
            onSelectCoupon = {},
            coupons = coupons,
            discountAmount = 1000,
            deliveryFee = 3000,
            totalAmount = 4000,
        )
    }
}
