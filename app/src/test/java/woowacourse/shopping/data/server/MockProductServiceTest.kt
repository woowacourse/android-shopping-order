package woowacourse.shopping.data.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import woowacourse.shopping.data.network.MockingServer
import woowacourse.shopping.data.storage.ProductStorage

class MockProductServiceTest {
    private val server = MockingServer()
    private val datasource = ProductStorage

    @Test
    fun `특정_ID_값의_상품_데이터를_가져온다`() {
        val actual = server.getProduct(1L)
        val expected = datasource[1L]

        assertThat(actual.id).isEqualTo(expected.id)
    }

    @Test
    fun `상품_ID_리스트에_해당하는_상품_목록_가져온다`() {
        val actual = server.getProducts(listOf(1L, 2L, 3L))

        val expected = listOf(1L, 2L, 3L)

        assertThat(actual.map { it.id }).isEqualTo(expected)
    }

    @Test
    fun `첫_번째_페이지의_상품_목록을_가져온다`() {
        val actual = server.singlePage(0, 10)
        val expected = datasource.singlePage(0, 10)

        assertThat(actual.products).isEqualTo(expected.products)
    }
}
