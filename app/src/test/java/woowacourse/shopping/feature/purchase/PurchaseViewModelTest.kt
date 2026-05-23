package woowacourse.shopping.feature.purchase

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.feature.MainDispatcherExtension
import woowacourse.shopping.feature.fake.FakeCartRepository
import woowacourse.shopping.feature.fake.FakeCouponRepository
import woowacourse.shopping.feature.fake.FakeOrderRepository
import woowacourse.shopping.fixture.TestCartContentFixture
import woowacourse.shopping.fixture.TestCouponFixture
import woowacourse.shopping.fixture.TestProductFixture

@ExtendWith(MainDispatcherExtension::class)
class PurchaseViewModelTest {
    private lateinit var viewModel: PurchaseViewModel

    private val allCoupons = TestCouponFixture.ALL
    private val products = TestProductFixture.products(5)
    private val cartContents = TestCartContentFixture.cartContentsOf(products)
    private val originalPrice = cartContents.sumOf { it.quantity * it.product.priceAmount() }

    @Test
    fun `적용 가능한 쿠폰 목록을 조회한다`() {
        // given: PurchaseViewModel과 모든 쿠폰 정보가 있는 쿠폰 레포지토리가 주어진다.
        viewModel = PurchaseViewModel(
            contentIds = emptyList(),
            originalPrice = 0,
            orderRepository = FakeOrderRepository(),
            couponRepository = FakeCouponRepository(allCoupons),
            cartRepository = FakeCartRepository(),
        )

        // when: 쿠폰 목록을 조회할 때
        viewModel.initialLoading()

        val result = viewModel.uiState.value.couponUiModels

        // then: 모든 쿠폰 목록이 조회된다
        assertThat(result).hasSize(allCoupons.size)
    }

    @Test
    fun `쿠폰 선택 시 총 결제 금액이 재계산된다`() {
        // given: 장바구니에 상품을 담고 주문 뷰모델에 전달하고 쿠폰 목록을 전달 받는다
        viewModel = PurchaseViewModel(
            contentIds = cartContents.map { it.id },
            originalPrice = originalPrice,
            orderRepository = FakeOrderRepository(),
            couponRepository = FakeCouponRepository(allCoupons),
            cartRepository = FakeCartRepository(),
        )
        viewModel.initialLoading()

        // when: 쿠폰을 선택했을 때
        viewModel.couponSelect(allCoupons.first().id)

        // then: 금액이 재계산된다.
        val result = viewModel.uiState.value.totalDiscountedPrice
        assertThat(result).isEqualTo(14_000)
    }
}
