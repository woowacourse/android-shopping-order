package woowacourse.shopping.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import woowacourse.shopping.buildBuyXGetYCoupon
import woowacourse.shopping.buildCoupons
import woowacourse.shopping.buildFixedCoupon
import woowacourse.shopping.buildPercentageCoupon
import woowacourse.shopping.buildShoppingProduct
import woowacourse.shopping.data.dto.response.AvailableTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CouponTest {
    @Nested
    @DisplayName("쿠폰의 유효기간 만료 여부를 검사할 수 있다")
    inner class ExpirationDateTest {
        @Test
        fun `유효기간이 지나지 않은 쿠폰은 isValidPeriod가 true이다`() {
            // given
            val availableTime = AvailableTime(LocalTime.of(0, 0), LocalTime.of(23, 59))
            val expirationDate = LocalDate.of(2222, 12, 30)
            val nowDate = LocalDate.now()
            val coupons = buildCoupons(expirationDate, availableTime)

            // when
            val isAllCouponsValid =
                coupons.all { coupon ->
                    coupon.isValidPeriod(nowDate)
                }

            // then
            assertThat(isAllCouponsValid).isEqualTo(true)
        }

        @Test
        fun `유효기간이 지난 쿠폰은 isValidPeriod가 false이다`() {
            // given
            val availableTime = AvailableTime(LocalTime.of(0, 0), LocalTime.of(23, 59))
            val expirationDate = LocalDate.of(1999, 6, 1)
            val nowDate = LocalDate.now()
            val coupons = buildCoupons(expirationDate, availableTime)

            // when
            val isAllCouponsExpired =
                coupons.none { coupon ->
                    coupon.isValidPeriod(nowDate)
                }

            // then
            assertThat(isAllCouponsExpired).isEqualTo(true)
        }
    }

    @Nested
    @DisplayName("최소 구매 가격과 할인 가격이 정해진 FixedCoupon 테스트")
    inner class FixedCouponTest {
        private lateinit var fixedCoupon: FixedCoupon

        @BeforeEach
        fun setup() {
            // 쿠폰은 유효 기간 내에 있으며, 최소 구매 가격이 10000이고 할인 가격이 1000이다
            val expirationDate = LocalDate.of(2222, 12, 30)
            fixedCoupon = buildFixedCoupon(expirationDate)
        }

        @Test
        fun `최소 구매 금액을 넘지 못하면 할인 가격은 0이다`() {
            // given
            val products =
                listOf(
                    buildShoppingProduct(1000, 1),
                )

            // when
            val discount = fixedCoupon.calculateDiscount(products, LocalDateTime.now())

            // then
            assertThat(discount).isEqualTo(0L)
        }

        @Test
        fun `최소 구매 금액을 넘으면 할인 가격은 1000이다`() {
            // given
            val products =
                listOf(
                    buildShoppingProduct(1000, 1),
                    buildShoppingProduct(2000, 2),
                    buildShoppingProduct(3000, 3),
                )

            // when
            val discount = fixedCoupon.calculateDiscount(products, LocalDateTime.now())

            // then
            assertThat(discount).isEqualTo(1000L)
        }
    }

    @Nested
    @DisplayName("구매 수량과 증정 수량이 정해진 BuyXGetYCoupon 쿠폰 테스트")
    inner class BuyXGetYCouponTest {
        private lateinit var buyXgetYCoupon: BuyXGetYCoupon

        @BeforeEach
        fun setup() {
            // 쿠폰은 유효 기간 내에 있으며 구매 수량(X) = 2, 증정 수량(Y) = 1이다
            val expirationDate = LocalDate.of(2222, 12, 30)
            buyXgetYCoupon = buildBuyXGetYCoupon(expirationDate)
        }

        @Test
        fun `특정 상품의 수량이 X+Y보다 작을 때는 할인 가격이 0이다`() {
            // given
            val products =
                listOf(
                    buildShoppingProduct(1000, 2),
                )

            // when
            val discount = buyXgetYCoupon.calculateDiscount(products, LocalDateTime.now())

            // then
            assertThat(discount).isEqualTo(0L)
        }

        @Test
        fun `특정 상품의 수량이 X+Y일 때는 Y개 만큼의 가격을 할인해 준다`() {
            // given
            val products =
                listOf(
                    buildShoppingProduct(1000, 3),
                )

            // when
            val discount = buyXgetYCoupon.calculateDiscount(products, LocalDateTime.now())

            // then
            assertThat(discount).isEqualTo(1000L)
        }

        @Test
        fun `수량이 X+Y인 상품이 여러 개일 때는 가격이 가장 높은 상품에 적용한다`() {
            // given
            val products =
                listOf(
                    buildShoppingProduct(1000, 3),
                    buildShoppingProduct(10000, 3),
                    buildShoppingProduct(5000, 3),
                )

            // when
            val discount = buyXgetYCoupon.calculateDiscount(products, LocalDateTime.now())

            // then
            assertThat(discount).isEqualTo(10000L)
        }
    }

    @Nested
    @DisplayName("특정 시간대에 적용 가능한 PercentageCoupon 테스트")
    inner class PercentageCouponTest {
        private lateinit var percentageCoupon: PercentageCoupon

        @BeforeEach
        fun setup() {
            // 쿠폰은 유효 기간 내에 있으며, 오후 시간 동안 10% 할인이 적용 가능하다
            val expirationDate = LocalDate.of(2222, 12, 30)
            val availableTime =
                AvailableTime(
                    LocalTime.of(12, 0, 0),
                    LocalTime.of(23, 59, 59),
                )
            percentageCoupon = buildPercentageCoupon(expirationDate, availableTime)
        }

        @Test
        fun `오전에는 할인 가격이 0이다`() {
            // given
            val products =
                listOf(
                    buildShoppingProduct(1000, 10),
                )

            // when
            val discount =
                percentageCoupon.calculateDiscount(
                    products,
                    LocalDateTime.of(2024, 6, 9, 11, 59, 59),
                )

            // then
            assertThat(discount).isEqualTo(0L)
        }

        @Test
        fun `오후에는 할인 가격이 구매 가격의 10%이다`() {
            // given
            val products =
                listOf(
                    buildShoppingProduct(1000, 10),
                )

            // when
            val discount =
                percentageCoupon.calculateDiscount(
                    products,
                    LocalDateTime.of(2024, 6, 9, 12, 1, 1),
                )

            // then
            assertThat(discount).isEqualTo(1000L)
        }
    }
}
