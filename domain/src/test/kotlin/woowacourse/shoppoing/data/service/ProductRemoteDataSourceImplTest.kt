package woowacourse.shoppoing.data.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.remoteDataSourceImpl.ProductRemoteDataSourceImpl
import woowacourse.shopping.model.Product
import woowacourse.shopping.utils.RetrofitUtil
import woowacourse.shopping.utils.mockWebServer.MockWeb

class ProductRemoteDataSourceImplTest {
    private lateinit var mockWebServer: MockWeb
    private lateinit var remoteProductRepository: ProductRemoteDataSourceImpl

    @Before
    fun setUp() {
        // given
        mockWebServer = MockWeb()
        RetrofitUtil.url = mockWebServer.url
        remoteProductRepository = ProductRemoteDataSourceImpl()
    }

    @Test
    fun `상품 목록을 가져온다`() {
        // given
        var products: List<Product> = emptyList()
        var lock = true

        // when
        remoteProductRepository.getAll { result ->
            result.onSuccess { products = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) { Thread.sleep(100) }

        // then
        assertThat(products).hasSize(100)
        products.forEachIndexed { i, it ->
            assertThat(it.id).isEqualTo(i)
            assertThat(it.name).isEqualTo("치킨$i")
            assertThat(it.price).isEqualTo(10000)
            assertThat(it.imageUrl).isEqualTo(
                "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
            )
        }
    }

    @Test
    fun `ID로 상품을 가져온다`() {
        // given
        var product: Product? = null
        var lock = true

        // when
        remoteProductRepository.findById(1) {
            it.onSuccess { product = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) { Thread.sleep(100) }

        // then
        assertThat(product).isNotNull
        assertThat(product!!.id).isEqualTo(1)
        assertThat(product!!.name).isEqualTo("치킨1")
        assertThat(product!!.price).isEqualTo(10000)
        assertThat(product!!.imageUrl).isEqualTo(
            "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
        )
    }
}
