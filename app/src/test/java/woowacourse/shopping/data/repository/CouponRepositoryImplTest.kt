package woowacourse.shopping.data.repository

import com.example.domain.model.Coupon
import com.example.domain.model.CouponDiscountPrice
import io.mockk.clearAllMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.coupon.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.mockwebserver.CouponMockWebserver

internal class CouponRepositoryImplTest {
    private lateinit var couponRepositoryImpl: CouponRepositoryImpl
    private lateinit var mockWebserver: CouponMockWebserver

    @Before
    fun setup() {
        mockWebserver = CouponMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        couponRepositoryImpl = CouponRepositoryImpl(CouponRemoteDataSourceImpl())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `쿠폰 정보들을 불러온다`() {
        // when
        var lock = true
        var coupons = emptyList<Coupon>()
        couponRepositoryImpl.getCoupons {
            coupons = it
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assert(coupons.size == 2)
        coupons.forEachIndexed { i, it ->
            assert(it.id == (i + 1).toLong())
        }
    }

    @Test
    fun `쿠폰 할인에 대한 정보를 불러온다`() {
        var lock = true
        var couponDiscountPrice = CouponDiscountPrice(0, 0)
        couponRepositoryImpl.getPriceWithCoupon(10000, 1) {
            couponDiscountPrice = it
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        assert(couponDiscountPrice.discountPrice == 5000)
        assert(couponDiscountPrice.totalPrice == 10000)
    }
}
