package woowacourse.shopping

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CartProductInfoListTest {

    private val cartProductInfoList: CartProductInfoList = CartProductInfoList(
        listOf(
            CartProductInfo(1, makeProduct(1), 1, true),
            CartProductInfo(2, makeProduct(2), 1, true),
            CartProductInfo(3, makeProduct(3), 1, true),
            CartProductInfo(4, makeProduct(4), 1, false),
            CartProductInfo(5, makeProduct(5), 1, false),
        )
    )

    private fun makeProduct(id: Int): Product = Product(id, "", "", Price(1000))

    @Test
    fun `카트 아이디가 일치하는 아이템을 삭제할 수 있다`() {
        // given
        val cartItem = CartProductInfo(1, makeProduct(1), 1, true)
        // when
        val actual = cartProductInfoList.delete(cartItem).items.contains(cartItem)
        // then
        assertThat(actual).isFalse
    }

    @Test
    fun `상품 아이디가 일치하는 카트의 개수를 업데이트 한다`() {
        // given
        val cartItem = CartProductInfo(1, makeProduct(1), 1, true)
        // when
        val actual = cartProductInfoList.updateItemCount(cartItem, 3).items[0].count
        // then
        assertThat(actual).isEqualTo(3)
    }

    @Test
    fun `모든 장바구니 상품들을 주문 상태로 할 수있다`() {
        // when
        val actual = cartProductInfoList.updateAllItemOrdered(true)
        // then
        assertThat(actual.items.first().isOrdered).isTrue
        assertThat(actual.items.last().isOrdered).isTrue
    }

    @Test
    fun `모든 장바구니 상품들을 비주문 상태로 할 수있다`() {
        // when
        val actual = cartProductInfoList.updateAllItemOrdered(false)
        // then
        assertThat(actual.items.first().isOrdered).isFalse
        assertThat(actual.items.last().isOrdered).isFalse
    }

    @Test
    fun `시작지점과 크기를 입력하면, 장바구니 상품의 일부를 가져올 수 있다`() {
        // when
        val actual = cartProductInfoList.getItemsInRange(0, 3)
        // then
        assertThat(actual.items.size).isEqualTo(3)
    }

    @Test
    fun `시작지점부과 크기를 더한값이, 장바구니 크기보다 크면, 시작지점부터 장바구니 크기만큼 가져온다 `() {
        // when
        val actual = cartProductInfoList.getItemsInRange(2, 8)
        // then
        assertThat(actual.items.size).isEqualTo(3)
    }

    @Test
    fun `시작지점이 장바구니 크기보다 크면, 빈 값을 가져온다 `() {
        // when
        val actual = cartProductInfoList.getItemsInRange(5, 8)
        // then
        assertThat(actual.items.size).isEqualTo(0)
    }

    @Test
    fun `다른 장바구니 상품들을 입력했을 때, 카트 아이디가 같다면, 다른 카트로 교체한다 `() {
        // given
        val newCartItemList = CartProductInfoList(
            listOf(
                CartProductInfo(1, makeProduct(2), 3),
                CartProductInfo(2, makeProduct(2), 3),
            )
        )
        // when
        val actual = cartProductInfoList.replaceItemList(newCartItemList)
        // then
        assertThat(actual.items.first().count).isEqualTo(3)
        assertThat(actual.items[1].count).isEqualTo(3)
    }
}
