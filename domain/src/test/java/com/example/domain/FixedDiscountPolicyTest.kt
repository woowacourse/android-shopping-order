package com.example.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FixedDiscountPolicyTest {

    private lateinit var fixedDiscountPolicy: FixedDiscountPolicy

    @BeforeEach
    fun setUp() {
        fixedDiscountPolicy = FixedDiscountPolicy(
            listOf(
                FixedDiscountPolicyUnit(50000, 2000),
                FixedDiscountPolicyUnit(100000, 5000),
                FixedDiscountPolicyUnit(200000, 12000),
            )
        )
    }

    @Test
    fun `상품가격의 총합이 50000이면 할인 금액은 2000원이다`() {
        // given
        val productsSum = 50000

        // when
        val actual = fixedDiscountPolicy.getDiscountPrice(productsSum)

        // then
        assertThat(actual).isEqualTo(2000)
    }

    @Test
    fun `상품가격의 총합이 49999원이면 할인 금액은 0원이다`() {
        // given
        val productsSum = 49999

        // when
        val actual = fixedDiscountPolicy.getDiscountPrice(productsSum)

        // then
        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun `상품가격의 총합이 210000이면 할인 금액은 12000원이다`() {
        // given
        val productsSum = 210000

        // when
        val actual = fixedDiscountPolicy.getDiscountPrice(productsSum)

        // then
        assertThat(actual).isEqualTo(12000)
    }

    @Test
    fun `상품 가격이 50000이면 할인된 최종 가격은 48000이다`() {
        // given
        val productsSum = 50000
        fixedDiscountPolicy.getDiscountPrice(productsSum)

        // when
        val actual = fixedDiscountPolicy.getFinalPrice(productsSum)

        // then
        assertThat(actual).isEqualTo(48000)
    }

    @Test
    fun `상품 가격이 49999이면 할인되지 않고 최종 가격은 49999이다`() {
        // given
        val productsSum = 49999
        fixedDiscountPolicy.getDiscountPrice(productsSum)

        // when
        val actual = fixedDiscountPolicy.getFinalPrice(productsSum)

        // then
        assertThat(actual).isEqualTo(49999)
    }

    @Test
    fun `상품 가격이 200000이면 할인된 최종 가격은 188000이다`() {
        // given
        val productsSum = 200000
        fixedDiscountPolicy.getDiscountPrice(productsSum)

        // when
        val actual = fixedDiscountPolicy.getFinalPrice(productsSum)

        // then
        assertThat(actual).isEqualTo(188000)
    }
}