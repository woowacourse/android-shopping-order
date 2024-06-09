package woowacourse.shopping.domain.repository

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.productTestFixture
import woowacourse.shopping.productsTestFixture
import woowacourse.shopping.source.FakeProductDataSource
import woowacourse.woowacourse.shopping.source.FakeOrderDataSource

class DefaultOrderRepositoryTest {
    private lateinit var orderSource: OrderDataSource
    private lateinit var productSource: ProductDataSource
    private lateinit var repo: OrderRepository

    @Test
    fun `주문이 성공한다`() =
        runTest {
            // given
            orderSource = FakeOrderDataSource()
            productSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(60).toMutableList(),
                )
            repo = DefaultOrderRepository(orderSource, productSource)
            // when

            val actual = repo.order2(listOf(1, 2, 3))

            // then
            assertThat(actual.isSuccess).isTrue
        }

    @Test
    fun `주문이 성공하면 저장되어있는 주문 아이템이 삭제된다`() =
        runTest {
            // given
            orderSource = FakeOrderDataSource()
            productSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(60).toMutableList(),
                )
            repo = DefaultOrderRepository(orderSource, productSource)

            // when
            repo.order2(listOf(1, 2, 3))

            val actual = repo.orderItems2().getOrThrow()

            // then
            assertThat(actual).isEmpty()
        }

    @Test
    fun `주문 아이템에 저장한다`() =
        runTest {
            // given
            orderSource = FakeOrderDataSource()
            productSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(60).toMutableList(),
                )
            repo = DefaultOrderRepository(orderSource, productSource)

            // when
            val actual = repo.saveOrderItem2(1, 3)

            // then
            assertThat(actual.isSuccess).isTrue
        }

    @Test
    fun `주문 아이템에 저장하면 해당 아이템을 불러올 수 있다 `() =
        runTest {
            // given
            orderSource = FakeOrderDataSource()
            productSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(60).toMutableList(),
                )
            repo = DefaultOrderRepository(orderSource, productSource)

            // when
            repo.saveOrderItem2(1, 3)
            val actual = repo.orderItems2().getOrThrow()

            // then
            assertThat(actual).isEqualTo(mapOf(1L to 3))
        }

    @Test
    fun `주문 아이템들의 총 가격의 합을 구한다`() =
        runTest {
            // given
            orderSource =
                FakeOrderDataSource(
                    orderSaved =
                        mutableMapOf(
                            1L to 3,
                            2L to 2,
                            3L to 1,
                        ),
                )
            productSource =
                FakeProductDataSource(
                    allProducts =
                        productsTestFixture(
                            60,
                            productFixture = { id -> productTestFixture(id.toLong(), price = id * 100) },
                        ).toMutableList(),
                )
            repo = DefaultOrderRepository(orderSource, productSource)

            // when
            val actual = repo.orderItemsTotalPrice2().getOrThrow()

            // then
            assertThat(actual).isEqualTo(1000)
        }
}
