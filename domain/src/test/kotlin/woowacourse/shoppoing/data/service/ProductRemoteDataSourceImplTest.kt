package woowacourse.shoppoing.data.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.remoteDataSourceImpl.ProductRemoteDataSourceImpl
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
        // when
        var lock = true
        remoteProductRepository.getAll {
            // then
            it.onSuccess { products ->
                assertThat(products).hasSize(100)
                for (i in 0..99) {
                    assertThat(products[i].id).isEqualTo(i)
                    assertThat(products[i].name).isEqualTo("치킨$i")
                    assertThat(products[i].price).isEqualTo(10000)
                    assertThat(products[i].imageUrl).isEqualTo(
                        "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
                    )
                }
            }.onFailure { e -> throw e }
            lock = false
        }
        while (lock) { Thread.sleep(100) }
    }

    @Test
    fun `ID로 상품을 가져온다`() {
        // when
        var lock = true
        remoteProductRepository.findById(1) {
            it.onSuccess { product ->
                assertThat(product.id).isEqualTo(1)
                assertThat(product.name).isEqualTo("치킨1")
                assertThat(product.price).isEqualTo(10000)
                assertThat(product.imageUrl).isEqualTo(
                    "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
                )
            }.onFailure { e -> throw e }
            lock = false
        }
        while (lock) { Thread.sleep(100) }
    }
}
