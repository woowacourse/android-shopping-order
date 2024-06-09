package woowacourse.shopping.domain.repository

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.HistoryProduct
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.productTestFixture
import woowacourse.shopping.productsTestFixture
import woowacourse.shopping.source.FakeProductDataSource
import woowacourse.shopping.source.FakeProductHistorySource

class DefaultProductHistoryRepositoryTest {
    private lateinit var historySource: ProductHistoryDataSource
    private lateinit var productSource: ProductDataSource
    private lateinit var productHistoryRepository: ProductHistoryRepository

//    @BeforeEach
//    fun setUp() {
//        historySource =
//            FakeProductHistorySource(
//                history = mutableListOf(1, 2, 3),
//            )
//        productSource =
//            FakeProductDataSource(
//                allProducts = productsTestFixture(60).toMutableList(),
//            )
//        productHistoryRepository = DefaultProductHistoryRepository(historySource, productSource)
//    }

    @Test
    fun `이미 내역에 있는 상품을 저장하려고 하면 저장 안됨`() {
        // given setup
        val product = productTestFixture(3)

        // when
        productHistoryRepository.saveProductHistory(product.id)

        // then
        assertThat(productHistoryRepository.loadAllProductHistory()).hasSize(3)
    }

    @Test
    fun `내역에 없는 상품을 저장`() {
        // given setup
        val product = productTestFixture(5)

        // when
        productHistoryRepository.saveProductHistory(product.id)

        // then
        assertThat(productHistoryRepository.loadAllProductHistory()).hasSize(4)
    }

    @Test
    fun `상품 모두 로드`() {
        // given setup

        // when
        val products = productHistoryRepository.loadAllProductHistory()

        // then
        assertThat(products).hasSize(3)
    }

    @Test
    fun `최근 본 상품 내역을 저장한다`() =
        runTest {
            // given
            val productId = 4L
            historySource =
                FakeProductHistorySource(
                    history = mutableListOf(HistoryProduct(1), HistoryProduct(2), HistoryProduct(3)),
                )
            productSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(60).toMutableList(),
                )
            productHistoryRepository = DefaultProductHistoryRepository(historySource, productSource)

            // when
            val actual = productHistoryRepository.saveProductHistory2(productId)

            // then
            assertThat(actual.isSuccess).isTrue()
        }

    @Test
    fun `최근 본 상품 내역을 저장하고 가장 최근 상품을 확인한다`() =
        runTest {
            // given
            val productId = 4L
            historySource =
                FakeProductHistorySource(
                    history = mutableListOf(HistoryProduct(1), HistoryProduct(2), HistoryProduct(3)),
                )
            productSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(60).toMutableList(),
                )
            productHistoryRepository = DefaultProductHistoryRepository(historySource, productSource)

            // when
            productHistoryRepository.saveProductHistory2(productId)
            val actual = productHistoryRepository.loadLatestProduct2().getOrThrow()

            // then
            assertThat(actual.id).isEqualTo(4L)
        }

    @Test
    fun `최근 상품들을 20개 중에 10개를 로드한다`() =
        runTest {
            // given
            historySource =
                FakeProductHistorySource(
                    historyProductsTestFixture(20).toMutableList(),
                )

            productSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(60).toMutableList(),
                )
            productHistoryRepository = DefaultProductHistoryRepository(historySource, productSource)

            // when
            val actual = productHistoryRepository.loadRecentProducts(10).getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                productsDomainTestFixture(10),
            )
        }

    @Test
    fun `가장 최근 상품을 불러온다`() {
        // given
        val productId = 4L
        historySource =
            FakeProductHistorySource(
                history = mutableListOf(HistoryProduct(1), HistoryProduct(2), HistoryProduct(3)),
            )
        productSource =
            FakeProductDataSource(
                allProducts = productsTestFixture(60).toMutableList(),
            )
        productHistoryRepository = DefaultProductHistoryRepository(historySource, productSource)

        // when
        val actual = productHistoryRepository.loadLatestProduct()

        // then
        assertThat(actual).isEqualTo(
            productDomainTestFixture(3),
        )
    }
}

// historyProduct 를 만드는 test fixture
fun historyProductsTestFixture(dataCount: Int): List<HistoryProduct> {
    return List(dataCount) { index ->
        HistoryProduct(index.toLong())
    }
}
