package woowacourse.shopping.data.datasource.remote.coupon

import io.mockk.clearAllMocks
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO
import woowacourse.shopping.mockwebserver.CouponMockWebserver

internal class CouponRemoteDataSourceImplTest {

    private lateinit var mockWebserver: CouponMockWebserver
    private lateinit var dataSource: CouponRemoteDataSourceImpl

    @Before
    fun setup() {
        mockWebserver = CouponMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        dataSource = CouponRemoteDataSourceImpl()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `쿠폰 정보들을 불러온다`() {
        // when
        var lock = true
        var coupons: List<CouponDTO> = emptyList()
        dataSource.getCoupons { result ->
            result.onSuccess { coupons = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(coupons.size, 2)
        coupons.forEachIndexed { i, it ->
            assertEquals(it.id, (i + 1).toLong())
        }
    }

    @Test
    fun `쿠폰 할인에 대한 정보를 불러온다`() {
        var lock = true
        var couponDiscountPrice: CouponDiscountPriceDTO = CouponDiscountPriceDTO(0, 0)
        dataSource.getPriceWithCoupon(10000, 1) { result ->
            result.onSuccess { couponDiscountPrice = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(couponDiscountPrice.discountPrice, 5000)
    }
}
