package woowacourse.shopping.view.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.getSerializableExtraData
import woowacourse.shopping.view.common.showSnackBar
import woowacourse.shopping.view.common.showToast
import woowacourse.shopping.view.product.ProductsActivity

class OrderActivity :
    AppCompatActivity(),
    OrderListener {
    private val binding: ActivityOrderBinding by lazy { ActivityOrderBinding.inflate(layoutInflater) }
    private val viewModel: OrderViewModel by viewModels {
        OrderViewModel.provideFactory(
            productsToOrder =
                intent.getSerializableExtraData(EXTRA_PRODUCTS_TO_ORDER)
                    ?: error("구매할 상품이 없을 수 없습니다."),
        )
    }
    private val couponAdapter: CouponAdapter by lazy {
        CouponAdapter(
            couponListener =
                object : CouponViewHolder.CouponListener {
                    override fun onCouponClick(couponId: Int) {
                        viewModel.updateApplyingCoupon(couponId)
                    }
                },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orderRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bindViewModel()
        setupAdapter()
        setupObservers()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel
        binding.orderListener = this
    }

    private fun setupAdapter() {
        binding.orderCoupons.adapter = couponAdapter
    }

    private fun setupObservers() {
        viewModel.coupons.observe(this) { coupons: List<CouponItem> ->
            couponAdapter.submitList(coupons)
        }

        viewModel.event.observe(this) { event: OrderEvent ->
            handleEvent(event)
        }
    }

    private fun handleEvent(event: OrderEvent) {
        when (event) {
            OrderEvent.GET_COUPON_FAILURE -> binding.root.showSnackBar("쿠폰을 불러오지 못했습니다.")
            OrderEvent.CREATE_ORDER_SUCCESS -> {
                showToast("주문이 완료됐습니다! 🚀총알 배송갑니다.")
                startActivity(ProductsActivity.newIntent(this))
            }

            OrderEvent.CREATE_ORDER_FAILURE -> binding.root.showSnackBar("주문이 실패했습니다. 잠시 후 다시 시도해주세요.")
        }
    }

    override fun onBackButtonClick() {
        finish()
    }

    override fun onOrderButtonClick() {
        viewModel.createOrder()
    }

    companion object {
        private const val EXTRA_PRODUCTS_TO_ORDER = "woowacourse.shopping.EXTRA_PRODUCTS_TO_ORDER"

        fun newIntent(
            context: Context,
            productsToOrder: Array<ShoppingCartProduct>,
        ): Intent =
            Intent(context, OrderActivity::class.java).apply {
                putExtra(EXTRA_PRODUCTS_TO_ORDER, productsToOrder)
            }
    }
}
