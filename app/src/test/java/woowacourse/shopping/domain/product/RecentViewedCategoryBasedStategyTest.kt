package woowacourse.shopping.domain.product

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecentViewedCategoryBasedStategyTest {
    @Test
    fun `상품 추천 알고리즘은 최근 본 상품 카테고리를 기반으로 최대 10개 노출한다`() {
        // given
        val products: List<Product> =
            List(100) { index ->
                Product(
                    index.toLong(),
                    name = "상품$index",
                    price = index * 100,
                    category = "카테고리1",
                    imageUrl = null,
                )
            }
        val algorithm = RecentViewedCategoryBasedStrategy("카테고리1")

        // when
        val expected =
            List(10) { index ->
                Product(
                    index.toLong(),
                    name = "상품$index",
                    price = index * 100,
                    category = "카테고리1",
                    imageUrl = null,
                )
            }
        val actual = algorithm.recommendedProducts(products, emptyList())

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `해당 카테고리 상품이 10개 미만이라면 해당하는 개수만큼만 노출`() {
        // given
        val products: List<Product> =
            List(5) { index ->
                Product(
                    index.toLong(),
                    name = "상품$index",
                    price = index * 100,
                    category = "카테고리1",
                    imageUrl = null,
                )
            }
        val algorithm = RecentViewedCategoryBasedStrategy("카테고리1")

        // when
        val expected =
            List(5) { index ->
                Product(
                    index.toLong(),
                    name = "상품$index",
                    price = index * 100,
                    category = "카테고리1",
                    imageUrl = null,
                )
            }
        val actual = algorithm.recommendedProducts(products, emptyList())

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `장바구니에 이미 추가된 상품이라면 미노출`() {
        // given
        val products: List<Product> =
            List(30) { index ->
                Product(
                    index.toLong(),
                    name = "상품$index",
                    price = index * 100,
                    category = "카테고리1",
                    imageUrl = null,
                )
            }
        val productsInCart: List<Product> =
            List(10) { index ->
                Product(
                    index.toLong(),
                    name = "상품$index",
                    price = index * 100,
                    category = "카테고리1",
                    imageUrl = null,
                )
            }
        val algorithm = RecentViewedCategoryBasedStrategy("카테고리1")

        // when
        val expected =
            List(10) { _index ->
                val index = _index + 10
                Product(
                    index.toLong(),
                    name = "상품$index",
                    price = index * 100,
                    category = "카테고리1",
                    imageUrl = null,
                )
            }
        val actual = algorithm.recommendedProducts(products, productsInCart)

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
