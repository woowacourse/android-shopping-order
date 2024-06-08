package woowacourse.shopping.domain.usecase.order

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.coupon.fakeCoupons
import woowacourse.shopping.domain.entity.coupon.fakeFixedCoupon
import woowacourse.shopping.domain.entity.coupon.fakeFreeShippingCoupon
import woowacourse.shopping.domain.entity.fakeCartProduct
import woowacourse.shopping.domain.entity.fakeProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository


@ExtendWith(MockKExtension::class)
class DefaultLoadAvailableDiscountCouponsUseCaseTest {
    @RelaxedMockK
    private lateinit var productRepository: ProductRepository

    @RelaxedMockK
    private lateinit var cartRepository: CartRepository

    @RelaxedMockK
    private lateinit var orderRepository: OrderRepository

    @InjectMockKs
    private lateinit var loadAvailableDiscountCouponsUseCase: DefaultLoadAvailableDiscountCouponsUseCase

    @Test
    fun `주문할 상품들에 적용가능한 쿠폰들을 불러온다`() = runTest {
        // given
        val productId = 1L
        val cartProducts = listOf(fakeCartProduct(productId = productId, price = 50_000))
        val cart = Cart(cartProducts)
        val coupons = fakeCoupons(
            fakeFixedCoupon(discountableMinPrice = 50_000),
            fakeFreeShippingCoupon(discountableMinPrice = 50_001),
        )
        coEvery { productRepository.findProductById(any()) } returns Result.success(fakeProduct())
        coEvery { cartRepository.filterCartProducts(any()) } returns Result.success(cart)
        coEvery { orderRepository.loadDiscountCoupons() } returns Result.success(
            coupons
        )
        // when
        val actual = loadAvailableDiscountCouponsUseCase(listOf(productId))
        // then
        val expect = fakeCoupons(
            fakeFixedCoupon(discountableMinPrice = 50_000)
        )
        actual.getOrThrow() shouldBe expect
    }

    @Test
    fun `상품 id들이 유효하지 않을 경우 예외를 반환한다`() = runTest {
        // given
        coEvery { productRepository.findProductById(any()) } returns Result.failure(
            IllegalArgumentException()
        )
        // when
        val actual = loadAvailableDiscountCouponsUseCase(listOf(1L))
        // then
        println("actual : $actual")
        assertSoftly {
            actual.isFailure shouldBe true
            actual.exceptionOrNull().shouldBeTypeOf<IllegalArgumentException>()
        }
    }

    @Test
    fun `장바구니 상품들을 불러오기에 실패할 경우 예외를 반환한다`() = runTest {
        // given
        coEvery { productRepository.findProductById(any()) } returns Result.success(fakeProduct())
        coEvery { cartRepository.filterCartProducts(any()) } returns Result.failure(
            IllegalArgumentException()
        )
        // when
        val actual = loadAvailableDiscountCouponsUseCase(listOf(1L))
        // then
        assertSoftly {
            actual.isFailure shouldBe true
            actual.exceptionOrNull().shouldBeTypeOf<IllegalArgumentException>()
        }
    }

    @Test
    fun `쿠폰 불러오기에 실패할 경우 예외가 발생한다`() = runTest {
        // given
        coEvery { productRepository.findProductById(any()) } returns Result.success(fakeProduct())
        coEvery { cartRepository.filterCartProducts(any()) } returns Result.success(Cart())
        coEvery { orderRepository.loadDiscountCoupons() } returns Result.failure(
            IllegalArgumentException()
        )
        // when
        val actual = loadAvailableDiscountCouponsUseCase(listOf(1L))
        // then
        assertSoftly {
            actual.isFailure shouldBe true
            actual.exceptionOrNull().shouldBeTypeOf<IllegalArgumentException>()
        }
    }
}