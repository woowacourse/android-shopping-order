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
    fun `한 페이지에 장바구니 아이템이 3개씩 보일 때, 현재 1페이지면 보여지는 상품은 3개다`() {
        // given
        val pageNation = PageNation(dummyCartProductsFour, 1)

        // when
        val actual = pageNation.currentItems.size

        // then
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun `한 페이지에 장바구니 아이템이 3개씩 보일 때, 현재 2페이지면 보여지는 상품은 1개다`() {
        // given
        val pageNation = PageNation(dummyCartProductsFour, 2)

        // when
        val actual = pageNation.currentItems.size

        // then
        val expected = 1
        assertEquals(expected, actual)
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
    fun `장바구니 아이디를 통해 페이지의 상품 Checked 상태를 변경할 수 있다`() {
        val pageNation = PageNation(dummyCartProductsThree, 1)

        val actual = pageNation.updateCartCheckedByCartId(1L, true).currentItems
        val expected = listOf(
            CartProduct(1L, Product(1L, "피자", 12_000, ""), 1, true),
            CartProduct(2L, Product(2L, "치킨", 15_000, ""), 1, false),
            CartProduct(3L, Product(3L, "족발", 28_000, ""), 1, false),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이디를 통해 페이지의 장바구니 상품 개수를 변경할 수 있다`() {
        val pageNation = PageNation(dummyCartProductsThree, 1)

        val actual = pageNation.updateCartCountByCartId(1L, 3).currentItems
        val expected = listOf(
            CartProduct(1L, Product(1L, "피자", 12_000, ""), 3, false),
            CartProduct(2L, Product(2L, "치킨", 15_000, ""), 1, false),
            CartProduct(3L, Product(3L, "족발", 28_000, ""), 1, false),
        )

        assertEquals(expected, actual)
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

    @Test
    fun `장바구니 아이디를 통해 페이지의 장바구니 아이템을 제거할 수 있다`() {
        val pageNation = PageNation(dummyCartProductsThree, 1)

        val actual = pageNation.deleteCartByCartId(1L).currentItems
        val expected = listOf(
            CartProduct(2L, Product(2L, "치킨", 15_000, ""), 1, false),
            CartProduct(3L, Product(3L, "족발", 28_000, ""), 1, false),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이디에 해당하는 장바구니 아이템을 반환한다`() {
        val pageNation = PageNation(dummyCartProductsThree, 1)

        val actual = pageNation.getCartByCartId(1L)
        val expected =
            CartProduct(1L, Product(1L, "피자", 12_000, ""), 1, false)

        assertEquals(expected, actual)
    }

    @Test
    fun `Checked 상태인 장바구니 아이디들을 반환한다`() {
        val pageNation = PageNation(dummyCartProductsFour, 1)

        val actual = pageNation.getAllCheckedCartIds()
        val expected = arrayListOf(4L)

        assertEquals(expected, actual)
    }

    companion object {
        private val dummyCartProductsThree = CartProducts(
            listOf(
                CartProduct(1L, Product(1L, "피자", 12_000, ""), 1, false),
                CartProduct(2L, Product(2L, "치킨", 15_000, ""), 1, false),
                CartProduct(3L, Product(3L, "족발", 28_000, ""), 1, false),
            )
        )

        private val dummyCartProductsFour = CartProducts(
            listOf(
                CartProduct(1L, Product(1L, "피자", 12_000, ""), 1, false),
                CartProduct(2L, Product(2L, "치킨", 15_000, ""), 1, false),
                CartProduct(3L, Product(3L, "족발", 28_000, ""), 1, false),
                CartProduct(4L, Product(4L, "짜장면", 9_000, ""), 1, true),
            )
        )
    }
}
