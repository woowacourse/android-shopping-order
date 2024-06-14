package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.bogoCoupon
import woowacourse.shopping.cartProduct
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.fixedCoupon
import woowacourse.shopping.freeShippingCoupon
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.miracleSale
import woowacourse.shopping.presentation.CoroutinesTestExtension
import woowacourse.shopping.presentation.common.UpdateUiModel
import woowacourse.shopping.presentation.ui.payment.model.PaymentNavigation
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiState

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class, MockKExtension::class)
class PaymentViewModelTest {
    @RelaxedMockK
    private lateinit var orderRepository: OrderRepository

    @RelaxedMockK
    private lateinit var couponRepository: CouponRepository

    @RelaxedMockK
    private lateinit var recentProductRepository: RecentProductRepository

    private val _uiState = MutableLiveData<PaymentUiState>()
    val uiState: LiveData<PaymentUiState> get() = _uiState

    private val updateUiModel: UpdateUiModel = UpdateUiModel()

    @OverrideMockKs
    private lateinit var viewModel: PaymentViewModel

    @Test
    fun `같은 상품을 세개 이상 구입하면 Bogo 쿠폰을 사용할 수 있다`() =
        runTest {
            _uiState.value =
                PaymentUiState().copy(
                    cartProducts =
                        listOf(
                            cartProduct.copy(quantity = 3),
                        ),
                )
            coEvery { couponRepository.getAll() } returns Result.success(listOf(bogoCoupon))
            viewModel.loadCoupons()

            val state = viewModel.uiState.getOrAwaitValue()

            assertSoftly {
                state.couponUiModels.size shouldBe 1
                state.couponUiModels.first().coupon.code shouldBe bogoCoupon.code
            }
        }

    @Test
    fun `10만원 이상 구입하면 5천원 할인 쿠폰을 사용할 수 있다`() =
        runTest {
            _uiState.value =
                PaymentUiState().copy(
                    cartProducts =
                        listOf(
                            cartProduct.copy(price = 100000),
                        ),
                )
            coEvery { couponRepository.getAll() } returns Result.success(listOf(fixedCoupon))
            viewModel.loadCoupons()

            val state = viewModel.uiState.getOrAwaitValue()

            assertSoftly {
                state.couponUiModels.size shouldBe 1
                state.couponUiModels.first().coupon.code shouldBe fixedCoupon.code
            }
        }

    @Test
    fun `5만원 이상 구입하면 무료배송 쿠폰을 사용할 수 있다`() =
        runTest {
            _uiState.value =
                PaymentUiState().copy(
                    cartProducts =
                        listOf(
                            cartProduct.copy(price = 50000),
                        ),
                )

            coEvery { couponRepository.getAll() } returns Result.success(listOf(freeShippingCoupon))
            viewModel.loadCoupons()

            val state = viewModel.uiState.getOrAwaitValue()

            assertSoftly {
                state.couponUiModels.size shouldBe 1
                state.couponUiModels.first().coupon.code shouldBe freeShippingCoupon.code
            }
        }

    @Test
    fun `정해진 시간에 구입하면 미라클 쿠폰을 사용할 수 있다`() =
        runTest {
            _uiState.value =
                PaymentUiState().copy(
                    cartProducts =
                        listOf(
                            cartProduct.copy(price = 50000),
                        ),
                )

            coEvery { couponRepository.getAll() } returns Result.success(listOf(miracleSale))
            viewModel.loadCoupons()

            val state = viewModel.uiState.getOrAwaitValue()

            assertSoftly {
                state.couponUiModels.size shouldBe 1
                state.couponUiModels.first().coupon.code shouldBe miracleSale.code
            }
        }

    @Test
    fun `장바구니에 담은 상품을 주문에 성공하면 화면 이동 이벤트가 전달된다`() =
        runTest {
            _uiState.value =
                PaymentUiState().copy(
                    cartProducts =
                        listOf(
                            cartProduct,
                        ),
                )
            coEvery {
                orderRepository.post(any())
            } returns Result.success(Unit)

            viewModel.pay()

            val state = viewModel.navigateHandler.getOrAwaitValue().getContentIfNotHandled()

            assertSoftly {
                state shouldBe PaymentNavigation.ToShopping(updateUiModel)
            }
        }
}
