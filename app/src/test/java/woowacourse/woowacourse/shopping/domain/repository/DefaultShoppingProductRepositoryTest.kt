package woowacourse.woowacourse.shopping.domain.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.cart.remote.datasource.CartItemDataSource
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.product.remote.DefaultProductRepository
import woowacourse.shopping.data.product.remote.datasource.ProductDataSource
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.productTestFixture
import woowacourse.shopping.productsTestFixture
import woowacourse.woowacourse.shopping.source.FakeProductDataSource
import woowacourse.woowacourse.shopping.source.FakeShoppingCartProductIdDataSource
import woowacourse.woowacourse.shopping.testfixture.productsIdCountDataTestFixture

class DefaultShoppingProductRepositoryTest {
    lateinit var productDataSource: ProductDataSource
    lateinit var shoppingCartProductIdDataSource: CartItemDataSource
    lateinit var repository: ProductRepository

    @Test
    fun `첫번재 페이지 데이터 로드`() {
        // given
        productDataSource =
            FakeProductDataSource(
                allProducts = productsTestFixture(count = 100).toMutableList(),
            )
        shoppingCartProductIdDataSource =
            FakeShoppingCartProductIdDataSource(
                data = productsIdCountDataTestFixture(dataCount = 10, 2).toMutableList(),
            )
        repository =
            DefaultProductRepository(
                productDataSource = productDataSource,
                cartItemDataSource = shoppingCartProductIdDataSource,
            )

        // when
        val loadedProducts = repository.loadProducts(page = 1)

        // then
        // TODO: 가독성이 너무 별로인데 개선해야 할듯?
        val expected =
            (
                productsTestFixture(count = 10).map { it.toDomain(2) } +
                    productsTestFixture(count = 10) {
                        productTestFixture(
                            (it + 10).toLong(),
                        )
                    }.map {
                        it.toDomain(
                            0,
                        )
                    }
            )
        assertThat(loadedProducts).isEqualTo(expected)
    }
}
