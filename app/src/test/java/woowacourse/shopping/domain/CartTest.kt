package woowacourse.shopping.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.entity.Cart

class CartTest {
    @Test
    fun `쇼핑 카트에 상품 추가`() {
        // given
        val cart = Cart()
        // when
        val actual = cart.add(fakeCartProduct())
        // then
        val expect = Cart(fakeCartProduct())
        actual shouldBe expect
    }

    @Test
    fun `product id 가 같은 상품을 추가 하면, 덮어 씌워진다`() {
        // given
        val cart = Cart(fakeCartProduct(productId = 1))
        val newCartProduct = fakeCartProduct(productId = 1, count = 3)
        // when
        val actual = cart.add(newCartProduct)
        // then
        val expect = Cart(newCartProduct)
        actual shouldBe expect
    }

    @Test
    fun `카트에 새 카트를 추가할 수 있다`() {
        // given
        val cart = Cart(fakeCartProduct(productId = 1))
        val newCart = Cart(fakeCartProduct(productId = 2, count = 3))
        // when
        val actual = cart.addAll(newCart)
        // then
        val expect = Cart(fakeCartProduct(productId = 1), fakeCartProduct(productId = 2, count = 3))
        actual shouldBe expect
    }

    @Test
    fun `카트에 새로운 카트를 추가할 때, product id 가 같은 상품이 있으면 덮어씌워진다`() {
        // given
        val cart = Cart(fakeCartProduct(productId = 1))
        val newCart =
            Cart(fakeCartProduct(productId = 1, count = 3), fakeCartProduct(productId = 2, count = 1))
        // when
        val actual = cart.addAll(newCart)
        // then
        val expect =
            Cart(fakeCartProduct(productId = 1, count = 3), fakeCartProduct(productId = 2, count = 1))
        actual shouldBe expect
    }

    @Test
    fun `해당 상품이 쇼핑 카트에 있을 때, 상품 삭제`() {
        // given
        val cartProduct = fakeCartProduct()
        val cart = Cart(cartProduct)
        // when
        val actual = cart.delete(cartProduct.product.id)
        // then
        val expect = Cart()
        actual shouldBe expect
    }

    @Test
    fun `해당 상품이 쇼핑 카트에 없을 때, 상품을 삭제하면 에러 발생`() {
        // given
        val cart = Cart()
        // when
        shouldThrow<IllegalArgumentException> {
            cart.delete(1L)
        }
    }

    @Test
    fun `카트 상품을 수량 만큼 증가한다`() {
        // given
        val amount = 2
        val productId = 1L
        val cart = Cart(fakeCartProduct(productId = productId, count = 1))
        // when
        val actual = cart.increaseProductCount(productId, amount)
        // then
        val expect = Cart(fakeCartProduct(productId = productId, count = 3))
        actual shouldBe expect
    }

    @Test
    fun `카트에 없는 상품의 수량을 증가하면 에러 발생`() {
        // given
        val cart = Cart()
        // when & then
        shouldThrow<IllegalArgumentException> {
            cart.increaseProductCount(1L)
        }
    }

    @Test
    fun `카트 상품을 수량만큼 감소시킨다`() {
        // given
        val amount = 1
        val productId = 1L
        val cart = Cart(fakeCartProduct(productId = productId, count = 2))
        // when
        val actual = cart.decreaseProductCount(productId, amount)
        // then
        val expect = Cart(fakeCartProduct(productId = productId, count = 1))
        actual shouldBe expect
    }

    @Test
    fun `카트에 없는 상품의 수량을 감소하면 에러 발생`() {
        // given
        val cart = Cart()
        // when & then
        shouldThrow<IllegalArgumentException> {
            cart.decreaseProductCount(1L)
        }
    }
}
