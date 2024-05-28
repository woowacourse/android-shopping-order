package woowacourse.shopping.data.datasource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.product.dummyPagingProduct
import woowacourse.shopping.data.product.server.MockWebProductServer
import woowacourse.shopping.data.retrofit.RemoteProductDataSource
import woowacourse.shopping.domain.model.PagingProduct
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class RemoteProductDataSourceTest {
    private lateinit var mockWebProductServer: MockWebProductServer
    private val dataSource: RemoteProductDataSource = RemoteProductDataSource()

    @Test
    fun `0페이지_상품_목록을_불러온다`() {
        val future = dataSource.getPagingProduct(0, 20)
        var actual: PagingProduct? = null
        val latch = CountDownLatch(1)
        thread {
            actual = future.get()
            latch.countDown()
        }

        latch.await()

        val expected = dummyPagingProduct
        assertThat(actual).isEqualTo(expected)
    }
}
