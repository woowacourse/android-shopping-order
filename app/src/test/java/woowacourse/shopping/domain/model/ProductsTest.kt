package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.DUMMY_CATALOG_PRODUCTS_1
import woowacourse.shopping.model.DUMMY_CATALOG_PRODUCTS_2
import woowacourse.shopping.model.DUMMY_PRODUCT_1
import woowacourse.shopping.model.DUMMY_PRODUCT_2
import woowacourse.shopping.model.DUMMY_PRODUCT_3

class ProductsTest {
    @Test
    fun `모든 상품이 선택되어 있으면 모든 상품이 선택되어 있음을 반환한다`() {
        // given
        val selectedProducts =
            DUMMY_CATALOG_PRODUCTS_1.copy(
                products = DUMMY_CATALOG_PRODUCTS_1.products.map { it.copy(isSelected = true) },
            )

        // when
        val result = selectedProducts.isAllSelected

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `선택된 상품들의 총 수량을 반환한다`() {
        // given
        val selected =
            DUMMY_CATALOG_PRODUCTS_1.copy(
                products = DUMMY_CATALOG_PRODUCTS_1.products.map { it.copy(isSelected = true) },
            )

        // when
        val quantity = selected.selectedProductsQuantity

        // then
        assertThat(quantity).isEqualTo(5 + 6 + 7)
    }

    @Test
    fun `선택된 상품들의 총 가격을 반환한다`() {
        // given
        val selected =
            DUMMY_CATALOG_PRODUCTS_1.copy(
                products = DUMMY_CATALOG_PRODUCTS_1.products.map { it.copy(isSelected = true) },
            )

        // when
        val price = selected.selectedProductsPrice

        // then
        val expected =
            DUMMY_PRODUCT_1.totalPrice +
                DUMMY_PRODUCT_2.totalPrice +
                DUMMY_PRODUCT_3.totalPrice

        assertThat(price).isEqualTo(expected)
    }

    @Test
    fun `상품 선택 상태를 반전한다`() {
        // given
        val products = DUMMY_CATALOG_PRODUCTS_1
        val target = products.products.first()

        // when
        val updated = products.updateSelection(target)

        // then
        assertThat(updated.getProductByProductId(target.productDetail.id)?.isSelected).isEqualTo(!target.isSelected)
    }

    @Test
    fun `특정 상품의 수량을 업데이트한다`() {
        // given
        val products = DUMMY_CATALOG_PRODUCTS_1
        val target = products.products.first()

        // when
        val updated = products.updateQuantity(target, 10)

        // then
        assertThat(updated.getProductByProductId(target.productDetail.id)?.quantity).isEqualTo(10)
    }

    @Test
    fun `전체 선택 상태를 반전한다`() {
        // given
        val products = DUMMY_CATALOG_PRODUCTS_1

        // when
        val toggled = products.toggleAllSelection()

        // then
        assertThat(toggled.isAllSelected).isTrue()
    }

    @Test
    fun `상품의 ID로 상품을 조회할 수 있다`() {
        // given
        val products = DUMMY_CATALOG_PRODUCTS_1
        val id = DUMMY_PRODUCT_3.productDetail.id

        // when
        val found = products.getProductByProductId(id)

        // then
        assertThat(found?.productDetail?.id).isEqualTo(id)
    }

    @Test
    fun `장바구니 ID로 상품을 조회할 수 있다`() {
        // given
        val products = DUMMY_CATALOG_PRODUCTS_1
        val cartId = DUMMY_PRODUCT_2.cartId

        // when
        val found = products.getProductByCartId(cartId!!)

        // then
        assertThat(found?.cartId).isEqualTo(cartId)
    }

    @Test
    fun `선택된 상품들의 상품 ID 목록을 반환한다`() {
        // given
        val selected =
            DUMMY_CATALOG_PRODUCTS_1.copy(
                products =
                    DUMMY_CATALOG_PRODUCTS_1.products.mapIndexed { index, product ->
                        if (index < 3) product.copy(isSelected = true) else product
                    },
            )

        // when
        val ids = selected.getSelectedProductIds()

        // then
        assertThat(ids).containsExactly(
            DUMMY_PRODUCT_1.productDetail.id,
            DUMMY_PRODUCT_2.productDetail.id,
            DUMMY_PRODUCT_3.productDetail.id,
        )
    }

    @Test
    fun `선택된 상품들의 장바구니 ID 목록을 반환한다`() {
        // given
        val selected =
            DUMMY_CATALOG_PRODUCTS_1.copy(
                products =
                    DUMMY_CATALOG_PRODUCTS_1.products.mapIndexed { index, product ->
                        if (index < 3) product.copy(isSelected = true) else product
                    },
            )

        // when
        val cartIds = selected.getSelectedCartIds()

        // then
        assertThat(cartIds).containsExactly(
            DUMMY_PRODUCT_1.cartId,
            DUMMY_PRODUCT_2.cartId,
            DUMMY_PRODUCT_3.cartId,
        )
    }

    @Test
    fun `두 Products를 병합하면 상품 목록이 추가된다`() {
        // given
        val left = DUMMY_CATALOG_PRODUCTS_1
        val right = DUMMY_CATALOG_PRODUCTS_2.copy(page = Page(3, isFirst = false, isLast = true))

        // when
        val merged = left + right

        // then
        assertThat(merged.products).hasSize(left.products.size + right.products.size)
        assertThat(merged.page).isEqualTo(right.page)
    }
}
