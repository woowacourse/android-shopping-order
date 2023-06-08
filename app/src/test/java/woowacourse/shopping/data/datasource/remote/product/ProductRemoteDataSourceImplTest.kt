package woowacourse.shopping.data.datasource.remote.product

import io.mockk.clearAllMocks
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.producdetail.ProductDetailRemoteSourceImpl
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.ProductDTO
import woowacourse.shopping.mockwebserver.ProductMockWebserver

internal class ProductRemoteDataSourceImplTest {
    private lateinit var mockWebserver: ProductMockWebserver
    private lateinit var productRemoteDataSourceImpl: ProductRemoteDataSourceImpl
    private lateinit var productDetailRemoteDataSourceImpl: ProductDetailRemoteSourceImpl

    @Before
    fun setup() {
        mockWebserver = ProductMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        productRemoteDataSourceImpl = ProductRemoteDataSourceImpl()
        productDetailRemoteDataSourceImpl = ProductDetailRemoteSourceImpl()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `상품들을 모두 가져온다`() {
        // when
        var lock = true
        var products: List<ProductDTO> = emptyList()
        productRemoteDataSourceImpl.getSubListProducts(10, 0) { result ->
            result.onSuccess { products = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(products.size, 1)
        products.forEachIndexed { i, it ->
            assertEquals(it.id, (i + 1).toLong())
        }
    }

    @Test
    fun `상품을 하나 가져온다`() {
        val productId = 1L
        // when
        var lock = true
        var product: ProductDTO = ProductDTO(0, "", 0, "")
        productDetailRemoteDataSourceImpl.getById(productId) { result ->
            result.onSuccess { product = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }
        assertEquals(product.id, 1)
    }
}
