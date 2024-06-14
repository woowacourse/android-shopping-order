package woowacourse.shopping.ui.coupon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityCouponBinding
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartUiModel
import woowacourse.shopping.ui.coupon.uimodel.CouponClickListener
import woowacourse.shopping.ui.coupon.uimodel.CouponError
import woowacourse.shopping.ui.coupon.viewmodel.CouponViewModel
import woowacourse.shopping.ui.coupon.viewmodel.CouponViewModelFactory
import woowacourse.shopping.ui.products.ProductContentsActivity
import woowacourse.shopping.ui.utils.parcelableList
import woowacourse.shopping.ui.utils.showToastMessage
import woowacourse.shopping.ui.utils.toUiText

class CouponActivity : AppCompatActivity(), CouponClickListener {
    private lateinit var binding: ActivityCouponBinding
    private lateinit var adapter: CouponAdapter

    private val viewModel: CouponViewModel by viewModels {
        CouponViewModelFactory(
            CouponRepositoryImpl(),
            OrderRepositoryImpl(),
        )
    }

    private val carts: List<CartUiModel> by lazy {
        intent.parcelableList(EXTRA_CART_IDS) ?: emptyList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.loadAvailableCoupons(carts)

        setAdapter()
        binding.lifecycleOwner = this
        binding.vm = viewModel

        observeCoupon()

        clickOrder()

        viewModel.loadInitialPaymentInfo(carts)
        observeErrorMessage()
    }

    private fun setAdapter() {
        binding.rvCouponMain.animation = null
        adapter = CouponAdapter(this)
        binding.rvCouponMain.adapter = adapter
    }

    private fun observeCoupon() {
        viewModel.coupons.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun onCheckBoxClicked(couponId: Long) {
        viewModel.updatePaymentInfo(carts, couponId)
    }

    private fun clickOrder() {
        binding.btnCouponPay.setOnClickListener {
            viewModel.order(carts)
        }

        viewModel.isOrderSuccess.observe(this) {
            finishOrderPage()
            showToastMessage(R.string.order_finish)
        }
    }

    private fun observeErrorMessage() {
        viewModel.dataError.observe(this) { error ->
            when (error) {
                DataError.Network.REQUEST_TIMEOUT -> showToastMessage(error.toUiText())
                DataError.Network.NO_INTERNET -> showToastMessage(error.toUiText())
                DataError.Network.SERVER -> showToastMessage(error.toUiText())
                DataError.Network.INVALID_AUTHORIZATION -> showToastMessage(error.toUiText())
                DataError.UNKNOWN -> {
                    showToastMessage(error.toUiText())
                    finish()
                }
            }
        }

        viewModel.errorScope.observe(this) { error ->
            when (error) {
                CouponError.LoadCoupon -> showToastMessage(R.string.coupon_error)
                CouponError.Order -> showToastMessage(R.string.order_error)
            }
        }
    }

    private fun finishOrderPage() {
        val intent = Intent(this, ProductContentsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    companion object {
        private const val EXTRA_CART_IDS = "cartIds"

        fun newIntent(
            context: Context,
            carts: List<CartUiModel>,
        ): Intent =
            Intent(context, CouponActivity::class.java).apply {
                this.putParcelableArrayListExtra(EXTRA_CART_IDS, ArrayList(carts))
            }
    }
}
