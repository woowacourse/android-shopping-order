package woowacourse.shopping.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import woowacourse.shopping.model.fixture.CartProductFixture
import woowacourse.shopping.model.fixture.ProductFixture

class CartPagesTest {

    private lateinit var fakeCartProducts: CartProducts

    @BeforeEach
    fun setup() {
        // given 1 부터 10 까지 상품
        fakeCartProducts = CartProducts(
            CartProductFixture.getCartProducts(quantity = 2, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        )
    }

    @Test
    fun `카트페이지 생성 시 처음 페이지는 1이다`() {
        // given
        val cartPages = CartPages(cartProducts = fakeCartProducts)

        // when & then
        val expected = 1
        assertThat(cartPages.pageNumber.value).isEqualTo(expected)
    }

    @Test
    fun `페이지가 1일 떄 다음 페이지 상품을 요청하면 2 페이지의 상품을 얻는다`() {
        // given
        val cartPages = CartPages(fakeCartProducts)

        // when 다음 페이지 상품 요청
        cartPages.goNextPageProducts()
        val actualProducts = cartPages.getCurrentProducts()

        // then 반환 1 부터 5 상품 & 페이지 번호 1
        val expectedPage = 2
        val expectedProducts = CartProductFixture.getCartProducts(quantity = 2, 6, 7, 8, 9, 10)

        assertAll(
            { assertThat(cartPages.pageNumber.value).isEqualTo(expectedPage) },
            { assertThat(actualProducts.items).isEqualTo(expectedProducts) },
        )
    }

    @Test
    fun `페이지가 2일 떄 이전 페이지 상품을 요청하면 1 페이지의 상품을 얻는다`() {
        // given
        val cartPages = CartPages(fakeCartProducts, Counter(2))

        // when 이전 페이지 상품 요청
        cartPages.goPreviousPageProducts()
        val actualProducts = cartPages.getCurrentProducts()

        // then 반환 1 부터 5 상품 & 페이지 번호 1
        val expectedPage = 1
        val expectedProducts = CartProductFixture.getCartProducts(quantity = 2, 1, 2, 3, 4, 5)

        assertAll(
            { assertThat(cartPages.pageNumber.value).isEqualTo(expectedPage) },
            { assertThat(actualProducts.items).isEqualTo(expectedProducts) },
        )
    }

    @Test
    fun `페이지가 1일 떄 4번 상품을 삭제하면 삭제 후 상품들을 얻는다`() {
        // given
        val cartPages = CartPages(fakeCartProducts, Counter(1))

        // when 삭제된 페이지 상품 요청
        cartPages.deleteProducts(
            ProductFixture.getProduct(4),
        )
        val actualProducts = cartPages.getCurrentProducts()

        // then 반환 1, 2, 3, 5, 6 상품 & 페이지 번호 1
        val expectedProducts = CartProductFixture.getCartProducts(quantity = 2, 1, 2, 3, 5, 6)

        assertThat(actualProducts.items).isEqualTo(expectedProducts)
    }

    @Test
    fun `페이지가 1일 떄 5번 상품의 개수를 줄이면 줄어든 상품들을 얻는다`() {
        // given
        val cartPages = CartPages(fakeCartProducts, Counter(1))

        // when 줄어든 페이지 상품 요청
        cartPages.subCountProducts(
            ProductFixture.getProduct(5),
        )

        val actualProducts = cartPages.getCurrentProducts()

        // then 반환 1, 2, 3, 4, 5 상품 5번 개수 -1 & 페이지 번호 1
        val expectedProducts = CartProductFixture.getCartProducts(quantity = 2, 1, 2, 3, 4) +
            CartProductFixture.getCartProduct(id = 5, quantity = 1)

        assertThat(actualProducts.items).isEqualTo(expectedProducts)
    }

    @Test
    fun `페이지가 1일 떄 1번 상품의 개수를 추가하면 추가된 상품들을 얻는다`() {
        // given
        val cartPages = CartPages(fakeCartProducts, Counter(1))

        // when 삭제된 페이지 상품 요청
        cartPages.addCountProducts(
            ProductFixture.getProduct(id = 1),
        )
        val actualProducts = cartPages.getCurrentProducts()

        // then 반환 1, 2, 3, 4, 5 상품 1번 개수 +1 & 페이지 번호 1
        val expectedProducts = listOf(
            CartProductFixture.getCartProduct(id = 1, quantity = 3),
        ) + CartProductFixture.getCartProducts(quantity = 2, 2, 3, 4, 5)

        assertThat(actualProducts.items).isEqualTo(expectedProducts)
    }

    @Test
    fun `상품이 10개고 페이지가 1일 떄 다음 페이지로 갈 수 있다`() {
        // given
        val cartPages = CartPages(fakeCartProducts, Counter(1))

        // when
        val actual = cartPages.isNextPageAble()

        // then
        val expected = true
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `상품이 10개고 페이지가 2일 떄 다음 페이지로 갈 수 없다`() {
        // given
        val cartPages = CartPages(fakeCartProducts, Counter(2))

        // when
        val actual = cartPages.isNextPageAble()

        // then
        val expected = false
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `상품이 10개고 페이지가 2일 떄 이전 페이지로 갈 수 있다`() {
        // given
        val cartPages = CartPages(fakeCartProducts, Counter(2))

        // when
        val actual = cartPages.isPreviousPageAble()

        // then
        val expected = true
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `페이지가 1일 떄 이전 페이지로 갈 수 없다`() {
        // given
        val cartPages = CartPages(fakeCartProducts, Counter(1))

        // when
        val actual = cartPages.isPreviousPageAble()

        // then
        val expected = false
        assertThat(actual).isEqualTo(expected)
    }
}
