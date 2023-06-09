package com.example.domain.cart

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class CartTest {

    @Test
    fun `장바구니에서 id를 통해 특정된 장바구니 제품 하나만 반환한다`() {
        // given
        val specifiedCartProduct: CartProduct = createCartProduct(id = 1)
        val cart: Cart = createCart(listOf(specifiedCartProduct, createCartProduct(id = 2)))

        // when
        val result = cart.getById(specifiedCartProduct.id)

        // then
        assertThat(result).isEqualTo(specifiedCartProduct)
    }

    @Test
    fun `장바구니에서 id를 통해 특정된 하나의 장바구니 제품을 반환한다 - 장바구니에 없는 것이라면 null을 반환한다`() {
        // given
        val specifiedCartProduct: CartProduct = createCartProduct(id = 1)
        val cart: Cart = createCart(listOf(createCartProduct(id = 2)))

        // when
        val result = cart.getById(specifiedCartProduct.id)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `장바구니를 초기화한다`() {
        // given
        val cart: Cart = createCart()
        val cartProducts: List<CartProduct> = listOf(createCartProduct())

        // when
        cart.setAll(cartProducts)

        // then
        assertThat(cart.products).isEqualTo(cartProducts)
    }

    @Test
    fun `장바구니 제품을 추가한다`() {
        // given
        val addCartProduct: CartProduct = createCartProduct(id = 1)
        val cart: Cart = createCart()

        // when
        cart.add(addCartProduct)

        // then
        assertThat(cart.getById(addCartProduct.id)).isEqualTo(addCartProduct)
    }

    @Test
    fun `장바구니에서 id를 통해 특정된 하나만 삭제한다`() {
        // given
        val removeId: Long = 5
        val cart: Cart = createCart(listOf(createCartProduct(id = removeId)))

        // when
        cart.removeById(removeId)

        // then
        assertThat(cart.products.find { it.id == removeId }).isNull()
    }

    @Test
    fun `장바구니 제품들이 전체 선택되어 있는지 여부를 반환한다 - 전체 선택 되어 있다면 true를 반환하다`() {
        // given
        val cart: Cart = createCart(
            listOf(createCartProduct(isPicked = true), createCartProduct(isPicked = true))
        )

        // when
        val result = cart.isAllPicked

        // then
        assertThat(result).isTrue
    }

    @Test
    fun `장바구니 제품들이 전체 선택되어 있는지 여부를 반환한다 - 하나 이상 선택 되어 있지 않다면 false를 반환하다`() {
        // given
        val cart: Cart = createCart(
            listOf(createCartProduct(isPicked = false), createCartProduct(isPicked = true))
        )

        // when
        val result = cart.isAllPicked

        // then
        assertThat(result).isFalse
    }

    @Test
    fun `장바구니 제품들 중 선택되어 있는 제품들의 개수를 반환한다`() {
        // given
        val cart: Cart = createCart(
            listOf(createCartProduct(isPicked = false), createCartProduct(isPicked = true))
        )

        // when
        val result = cart.getPickedCount()

        // then
        val expected: Int = 1
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `장바구니 제품들 중 선택되어 있는 장바구니 제품들을 반환한다`() {
        // given
        val pickedCartProduct: CartProduct = createCartProduct(isPicked = true)
        val notPickedCartProduct: CartProduct = createCartProduct(isPicked = false)
        val cart: Cart = createCart(listOf(pickedCartProduct, notPickedCartProduct))

        // when
        val result = cart.getPickedProducts()

        // then
        assertAll(
            { assertThat(result.products.contains(pickedCartProduct)).isTrue },
            { assertThat(result.products.contains(notPickedCartProduct)).isFalse }
        )
    }

    @Test
    fun `장바구니 제품들 중 선택되어 있는 장바구니 제품들의 금액 총합을 반환한다`() {
        // given
        val cart: Cart = createCart(
            listOf(
                createCartProduct(productPrice = 1_000, quantity = 3),
                createCartProduct(productPrice = 2_000, quantity = 1)
            )
        )

        // when
        val result = cart.getPickedProductsTotalPrice()

        // then
        val expected = 5_000
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `장바구니 제품들의 전체 선택 여부를 변경한다`() {
        // given
        val cart: Cart = createCart(listOf(createCartProduct(isPicked = false)))

        // when
        cart.setAllPicked(true)

        // then
        assertThat(cart.isAllPicked).isTrue
    }

    @Test
    fun `장바구니에서 id를 통해 특정된 하나의 선택 여부를 변경한다`() {
        // given
        val specifiedCartProduct: CartProduct = createCartProduct(id = 1, isPicked = false)
        val cart: Cart = createCart(listOf(specifiedCartProduct))

        // when
        cart.setPickedById(specifiedCartProduct.id, true)

        // then
        assertThat(cart.getById(specifiedCartProduct.id)?.isPicked).isTrue
    }

    @Test
    fun `장바구니에서 id를 통해 특정된 하나의 개수를 변경한다`() {
        // given
        val specifiedCartProduct: CartProduct = createCartProduct(id = 1, quantity = 1)
        val cart: Cart = createCart(listOf(specifiedCartProduct))

        // when
        val quantity = 10
        cart.setProductQuantityById(specifiedCartProduct.id, quantity)

        // then
        val expected = 10
        assertThat(cart.getById(specifiedCartProduct.id)?.quantity).isEqualTo(expected)
    }
}
