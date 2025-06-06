package woowacourse.shopping.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.DUMMY_COUPONS_1

class CouponsTest {
    @Test
    fun `쿠폰 ID를 입력하면 쿠폰이 선택된다`() {
        // given
        val coupons = DUMMY_COUPONS_1

        // when
        val selected = coupons.selectCoupon(2)

        // then
        assertThat(selected.value.count { it.isSelected }).isEqualTo(1)
        assertThat(selected.value.find { it.detail.id == 2 }?.isSelected).isTrue()
        assertThat(selected.value.filter { it.detail.id != 2 }.all { !it.isSelected }).isTrue()
    }

    @Test
    fun `같은 쿠폰 ID를 두 번 선택하면 선택이 해제된다`() {
        // given
        val coupons = DUMMY_COUPONS_1

        // when
        val onceSelected = coupons.selectCoupon(1)
        val twiceSelected = onceSelected.selectCoupon(1)

        // then
        assertThat(twiceSelected.value.all { !it.isSelected }).isTrue()
    }

    @Test
    fun `다른 쿠폰을 선택하면 이전 쿠폰은 해제되고 새 쿠폰만 선택된다`() {
        // given
        val selected1 = DUMMY_COUPONS_1.selectCoupon(1)

        // when
        val selected3 = selected1.selectCoupon(3)

        // then
        assertThat(selected3.value.count { it.isSelected }).isEqualTo(1)
        assertThat(selected3.value.find { it.detail.id == 3 }?.isSelected).isTrue()
        assertThat(selected3.value.filter { it.detail.id != 3 }.all { !it.isSelected }).isTrue()
    }
}
