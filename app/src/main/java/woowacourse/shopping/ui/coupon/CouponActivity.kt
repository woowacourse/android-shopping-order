package woowacourse.shopping.ui.coupon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityCouponBinding
import woowacourse.shopping.ui.cart.cartitem.CartUiModel
import woowacourse.shopping.ui.products.ProductContentsActivity
import woowacourse.shopping.ui.utils.parcelableList

class CouponActivity : AppCompatActivity(), CouponClickListener {

    private lateinit var binding: ActivityCouponBinding
    private lateinit var adapter: CouponAdapter

    private val viewModel: CouponViewModel by viewModels {
        CouponViewModelFactory(
            CouponRepositoryImpl(),
            OrderRepositoryImpl()
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

    private fun clickOrder(){
        binding.btnCouponPay.setOnClickListener {
            viewModel.order(carts)
        }

        viewModel.isOrderSuccess.observe(this) {
            val intent = Intent(this, ProductContentsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    companion object {

        private const val EXTRA_CART_IDS = "cartIds"

        fun newIntent(
            context: Context,
            carts: List<CartUiModel>
        ): Intent = Intent(context, CouponActivity::class.java).apply {
            this.putParcelableArrayListExtra(EXTRA_CART_IDS, ArrayList(carts))
        }
    }
}
