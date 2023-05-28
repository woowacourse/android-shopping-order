package woowacouse.shopping.model.page

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.cart.CartProducts
import woowacouse.shopping.model.product.Product

class PageNationTest {
    @Test
    fun `장바구니 아이템이 4개면 페이지 개수는 2개이다`() {
        val pageNation = PageNation(dummyCartProductsFour, 1)

        val actual = pageNation.pageCount
        val expected = 2
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이템이 4개면 다음 페이지로 넘어갈 수 있다`() {
        val pageNation = PageNation(dummyCartProductsFour, 1)

        val actual = pageNation.nextPage()
        val expected = 2
        assertEquals(expected, actual.currentPage)
    }

    @Test
    fun `장바구니 아이템이 3개면 다음 페이지로 넘어갈 수 없다`() {
        val pageNation = PageNation(dummyCartProductsThree, 1)

        val actual = pageNation.nextPage()
        val expected = 1
        assertEquals(expected, actual.currentPage)
    }

    @Test
    fun `현재 페이지가 2일 때 이전 페이지 1로 넘어갈 수 있다`() {
        val pageNation = PageNation(dummyCartProductsFour, 2)

        val actual = pageNation.previousPage()
        val expected = 1
        assertEquals(expected, actual.currentPage)
    }

    @Test
    fun `현재 페이지가 1이면 이전 페이지로 넘어갈 수 없다`() {
        val pageNation = PageNation(dummyCartProductsFour, 1)

        val actual = pageNation.previousPage()
        val expected = 1
        assertEquals(expected, actual.currentPage)
    }

    @Test
    fun `전체 Check 하면 현재 보여지고 있는 페이지의 아이템들의 상태는 Checked가 된다`() {
        val pageNation = PageNation(dummyCartProductsThree, 1)

        val actual = pageNation.updateAllCartsChecked(true)
        val expected = true

        actual.currentItems.forEach {
            assertEquals(expected, it.checked)
        }
    }

    companion object {
        private val dummyCartProductsThree = CartProducts(
            listOf(
                CartProduct(1L, Product(1L, "피자", 12_000, "", 1), false),
                CartProduct(2L, Product(2L, "치킨", 15_000, "", 1), false),
                CartProduct(3L, Product(3L, "족발", 28_000, "", 1), false),
            )
        )

        private val dummyCartProductsFour = CartProducts(
            listOf(
                CartProduct(1L, Product(1L, "피자", 12_000, "", 1), false),
                CartProduct(2L, Product(2L, "치킨", 15_000, "", 1), false),
                CartProduct(3L, Product(3L, "족발", 28_000, "", 1), false),
                CartProduct(4L, Product(4L, "짜장면", 9_000, "", 1), false),
            )
        )
    }
}
