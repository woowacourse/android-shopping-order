package woowacourse.shopping.data.repository

import com.example.domain.model.Product
import io.mockk.clearAllMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.producdetail.ProductDetailRemoteSourceImpl
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.mockwebserver.ProductMockWebserver

internal class ProductDetailRepositoryImplTest {
    private lateinit var productDetailRepositoryImpl: ProductDetailRepositoryImpl
    private lateinit var mockWebserver: ProductMockWebserver

    @Before
    fun setup() {
        mockWebserver = ProductMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        productDetailRepositoryImpl = ProductDetailRepositoryImpl(ProductDetailRemoteSourceImpl())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `상품 상세 정보를 불러온다`() {
        // when
        var lock = true
        var productDetail: Product? = null
        productDetailRepositoryImpl.getById(1) {
            productDetail = it
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assert(productDetail?.id == 1L)
    }
}
