package com.example.domain.page

import com.example.domain.Product
import com.example.domain.cart.CartProduct
import com.example.domain.cart.CartProducts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PageNationTest {
    @Test
    fun `아이템이 6개 이상이면 다음 페이지를 반환한다`() {
        // given
        val cartProducts = getCartProducts()
        val pageNation = PageNation(cartProducts, 1)
        val expected = PageNation(cartProducts, 2)

        // when
        val actual = pageNation.nextPage()

        // then
        assertThat(expected.currentPage).isEqualTo(actual.currentPage)
    }

    @Test
    fun `아이템이 5개 이하면 다음 페이지를 반환하지 않는다`() {
        // given
        val product = Product(1L, "치킨", 13000)
        val cartProduct = CartProduct(1L, product, 3, false)
        val cartProducts = CartProducts(mutableListOf(cartProduct))
        val pageNation = PageNation(cartProducts, 1)
        val expected = PageNation(cartProducts, 1)

        // when
        val actual = pageNation.nextPage()

        // then
        assertThat(expected.currentPage).isEqualTo(actual.currentPage)
    }

    @Test
    fun `6개의 아이템을 가진 장바구니에서 2페이지를 보고 있을 때 하나를 삭제하면 현재 페이지는 1이 된다`() {
        // given
        val pageNation = PageNation(getCartProducts(), 2)
        val expected = 1

        // when
        val actual = pageNation.remove(0L)

        // then
        assertThat(actual.currentPage).isEqualTo(expected)
    }

    @Test
    fun `6개의 아이템을 가진 장바구니에서 1페이지 아이템들의 checked를 true로 변경한다`() {
        // given
        val pageNation = PageNation(getCartProducts(), 1)
        val expected = 1

        // when
        val actual = pageNation.updateCurrentPageCheckedAll(true)

        // then
        assertThat(actual.allList[0].checked).isTrue
        assertThat(actual.allList[1].checked).isTrue
        assertThat(actual.allList[2].checked).isTrue
        assertThat(actual.allList[3].checked).isTrue
        assertThat(actual.allList[4].checked).isTrue
        assertThat(actual.allList[5].checked).isFalse
    }

    @Test
    fun `2번 id를 가진 장바구니 아이템의 count를 2로 바꾼다`() {
        // given
        val pageNation = PageNation(getCartProducts(), 1)
        val expected = 2

        // when
        val actual = pageNation.updateCountState(2L, 2)

        // then
        assertThat(actual.allList[2].count).isEqualTo(expected)
    }

    companion object {
        fun getCartProducts(): CartProducts {
            return CartProducts(
                List(6) {
                    Product(1L, "치킨", 13000)
                }.mapIndexed { index, product ->
                    CartProduct(index.toLong(), product, index + 1, false)
                }.toMutableList(),
            )
        }
    }
}
