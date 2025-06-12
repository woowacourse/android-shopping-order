import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.datasource.remote.CouponDataSource
import woowacourse.shopping.data.dto.coupon.CouponResponse
import woowacourse.shopping.data.repository.remote.CoroutinesTestExtension
import woowacourse.shopping.data.repository.remote.CouponRepositoryImpl

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
class CouponRepositoryImplTest {
    private val couponDataSource: CouponDataSource = mockk()
    private lateinit var couponRepository: CouponRepositoryImpl

    @BeforeEach
    fun setup() {
        couponRepository = CouponRepositoryImpl(couponDataSource)
    }

    @Test
    fun `쿠폰_조회에_성공하면_Success_결과를_반환한다`() =
        runTest {
            val mockCouponsResponse = listOf<CouponResponse>()

            coEvery { couponDataSource.getCoupons() } returns mockCouponsResponse

            val result = couponRepository.getCoupons()

            assertTrue(result.isSuccess)
        }

    @Test
    fun `쿠폰_조회에_실패하면_Failure_결과를_반환한다`() =
        runTest {
            coEvery { couponDataSource.getCoupons() } throws RuntimeException("네트워크 오류 등")

            val result = couponRepository.getCoupons()

            assertTrue(result.isFailure)
        }
}
