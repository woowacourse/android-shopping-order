package woowacourse.shopping.domain.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.source.FakeProductDataSource
import woowacourse.shopping.source.FakeShoppingCartDataSource

class CategoryBasedProductRecommendationRepositoryTest {
    private lateinit var productSource: ProductDataSource
    private lateinit var cartSource: ShoppingCartDataSource

    private lateinit var repository: ProductsRecommendationRepository

    @Test
    fun `마지막으로 본 상품이 패션 상품이라면 카테고리가 패션인 상품을 보여준다`() {
        // given
        productSource =
            FakeProductDataSource(
                allProducts =
                    mutableListOf(
                        ProductData(1, "", "", 1000, "fashion"),
                        ProductData(2, "", "", 2000, "fashion"),
                        ProductData(3, "", "", 3000, "fashion"),
                        ProductData(4, "", "", 4000, "fashion"),
                        ProductData(5, "", "", 5000, "fashion"),
                        ProductData(6, "", "", 6000, "fashion"),
                        ProductData(7, "", "", 5000, "electronics"),
                        ProductData(8, "", "", 6000, "electronics"),
                    ),
            )
        cartSource = FakeShoppingCartDataSource()
        repository = CategoryBasedProductRecommendationRepository(productSource, cartSource)

        // when
        val recommendedProducts = repository.recommendedProducts(1)

        // then
        assertThat(recommendedProducts).isEqualTo(
            listOf(
                ProductData(1, "", "", 1000, "fashion").toDomain(),
                ProductData(2, "", "", 2000, "fashion").toDomain(),
                ProductData(3, "", "", 3000, "fashion").toDomain(),
                ProductData(4, "", "", 4000, "fashion").toDomain(),
                ProductData(5, "", "", 5000, "fashion").toDomain(),
                ProductData(6, "", "", 6000, "fashion").toDomain(),
            ),
        )
    }

    @Test
    fun `마지막으로 본 상품이 패션 상품이라면 카테고리가 패션인 상품을 보여준다 이 떄 잡바구니에 이미 있는 상품은 보여주지 않는다`() {
        // given
        productSource =
            FakeProductDataSource(
                allProducts =
                    mutableListOf(
                        ProductData(1, "", "", 1000, "fashion"),
                        ProductData(2, "", "", 2000, "fashion"),
                        ProductData(3, "", "", 3000, "fashion"),
                        ProductData(4, "", "", 4000, "fashion"),
                        ProductData(5, "", "", 5000, "fashion"),
                        ProductData(6, "", "", 6000, "fashion"),
                        ProductData(7, "", "", 5000, "electronics"),
                        ProductData(8, "", "", 6000, "electronics"),
                    ),
            )
        cartSource =
            FakeShoppingCartDataSource(
                CartItemResponse(id = 101, quantity = 0, product = ProductResponse(1, "", 1, "1", "fashion")),
                CartItemResponse(id = 102, quantity = 0, product = ProductResponse(2, "", 1, "1", "fashion")),
            )
        repository = CategoryBasedProductRecommendationRepository(productSource, cartSource)

        // when
        val recommendedProducts = repository.recommendedProducts(1)

        // then
        assertThat(recommendedProducts).isEqualTo(
            listOf(
                ProductData(3, "", "", 3000, "fashion").toDomain(),
                ProductData(4, "", "", 4000, "fashion").toDomain(),
                ProductData(5, "", "", 5000, "fashion").toDomain(),
                ProductData(6, "", "", 6000, "fashion").toDomain(),
            ),
        )
    }

    @Test
    fun `마지막으로 본 상품이 패션 상품이고, 장바구니에 이미 있는 상품도 모두 필터링한 결과가 12개라면, 10개를 리턴한다`() {
        // given
        productSource =
            FakeProductDataSource(
                allProducts =
                    mutableListOf(
                        ProductData(1, "", "", 1000, "fashion"),
                        ProductData(2, "", "", 2000, "fashion"),
                        ProductData(3, "", "", 3000, "fashion"),
                        ProductData(4, "", "", 4000, "fashion"),
                        ProductData(5, "", "", 5000, "fashion"),
                        ProductData(6, "", "", 6000, "fashion"),
                        ProductData(7, "", "", 1000, "fashion"),
                        ProductData(8, "", "", 2000, "fashion"),
                        ProductData(9, "", "", 3000, "fashion"),
                        ProductData(10, "", "", 4000, "fashion"),
                        ProductData(11, "", "", 5000, "fashion"),
                        ProductData(12, "", "", 6000, "fashion"),
                    ),
            )
        cartSource = FakeShoppingCartDataSource()
        repository = CategoryBasedProductRecommendationRepository(productSource, cartSource)

        // when
        val recommendedProducts = repository.recommendedProducts(1)

        // then
        assertThat(recommendedProducts).hasSize(10)
    }
}
