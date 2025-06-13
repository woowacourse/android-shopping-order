package woowacourse.shopping.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.data.payment.repository.PaymentRepository
import woowacourse.shopping.domain.model.coupon.CouponService

class CartViewModelFactory(
    private val cartRepository: CartRepository,
    private val goodsRepository: GoodsRepository,
    private val paymentRepository: PaymentRepository,
    private val couponService: CouponService,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartRepository, goodsRepository, paymentRepository, couponService) as T
        }
        throw IllegalArgumentException("알 수 없는 뷰 모델 클래스 입니다")
    }
}
