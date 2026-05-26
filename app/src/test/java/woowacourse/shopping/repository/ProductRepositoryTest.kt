@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.repository

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryTest {
    private lateinit var repo: ProductRepository
    private val totalSize = ProductRepositoryFixture.products.size

    @BeforeEach
    fun setUp() {
        repo = FakeProductRepository(ProductRepositoryFixture.products)
    }

    @Test
    fun `특정 오프셋과 사이즈(20개)를 요청했을 때, 정확히 해당 구간의 데이터를 반환한다`() =
        runBlocking {
            val size = 20
            val page = 0

            val actual = repo.getProducts(page, size).items.size
            val expected = 20

            assertEquals(expected, actual)
        }

    @Test
    fun `전체 데이터 개수보다 큰 범위(남은 데이터가 20개 미만)를 요청했을 때, 예외 없이 남은 개수만큼만 정상 반환한다`() =
        runBlocking {
            val moreThanTotalSize = totalSize + 1

            val actual = repo.getProducts(page = 0, size = moreThanTotalSize).items.size
            val expected = totalSize

            assertEquals(expected, actual)
        }

    @Test
    fun `전체 데이터 개수보다 큰 페이지를 요청했을 때 빈 결과를 반환한다`() =
        runBlocking {
            val actual = repo.getProducts(page = (totalSize / 20) + 1, size = 20).items.size

            assertEquals(0, actual)
        }

    @Test
    fun `음수 limit을 요청했을 때 빈 결과를 반환한다`() =
        runBlocking {
            val actual = repo.getProducts(page = 0, size = -1).items.size

            assertEquals(0, actual)
        }

    @Test
    fun `카테고리로 상품을 조회하면 해당 카테고리 상품만 반환한다`() =
        runBlocking {
            val actual = repo.getProductsByCategory(category = "dessert", page = 0, size = 10).items

            assertEquals(8, actual.size)
            assertEquals(setOf("dessert"), actual.map { it.category }.toSet())
        }
}
