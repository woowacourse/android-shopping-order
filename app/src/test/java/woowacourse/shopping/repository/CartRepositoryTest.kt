@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.repository

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import woowacourse.shopping.model.Product

class CartRepositoryTest {
    private lateinit var repo: CartRepository

    private val product1: Product = CartRepositoryFixture.shrimpCracker

    @BeforeEach
    fun setUp() {
        repo = FakeCartRepository()
    }

    @Test
    fun `정상적인 상품 객체의 최종 수량을 설정하면 장바구니 목록에 해당 상품이 반영된다`() =
        runBlocking {
            repo.setQuantity(product1.id, 2)

            val actual = repo.getCartItemsByProductIds(setOf(product1.id))

            assertTrue(actual.any { it.productId == product1.id && it.quantity == 2 })
        }

    @Test
    fun `장바구니에 존재하는 상품 수량을 0으로 설정하면 목록에서 제거된다`() =
        runBlocking {
            repo.setQuantity(product1.id, 1)
            repo.setQuantity(product1.id, 0)

            val actual = repo.getCartItemsByProductIds(setOf(product1.id))

            assertFalse(actual.any { it.productId == product1.id })
        }

    @Test
    fun `존재하지 않는 상품 수량을 0으로 설정하면 상태 변경 없이 무시된다`() =
        runBlocking {
            repo.setQuantity(product1.id, 0)

            val actual = repo.getCartItemsByProductIds(setOf(product1.id))

            assertEquals(emptyList<woowacourse.shopping.model.CartItem>(), actual)
        }

    @Test
    fun `전체 데이터 개수보다 큰 페이지를 요청했을 때 빈 결과를 반환한다`() =
        runBlocking {
            repo.setQuantity(product1.id, 1)

            val actual = repo.getCartPage(page = repo.count() + 1, size = 20)

            assertEquals(emptyList<woowacourse.shopping.repository.query.CartPageItem>(), actual.items)
        }

    @Test
    fun `음수 수량을 설정하면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            runBlocking {
                repo.setQuantity(product1.id, -1)
            }
        }
    }
}
