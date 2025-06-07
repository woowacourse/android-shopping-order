package woowacourse.shopping.presentation.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.getParcelableArrayListExtraCompat
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.CouponUiModel

class OrderActivity :
    AppCompatActivity(),
    CouponClickListener {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var viewModel: OrderViewModel
    private val couponAdapter: CouponAdapter by lazy {
        CouponAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
        setupViewModel()
        initInsets()
        setupToolbar()
        initAdapter()
        observeViewModel()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        binding.lifecycleOwner = this
    }

    private fun setupViewModel() {
        val selectedItems =
            intent.getParcelableArrayListExtraCompat<CartItemUiModel>(Extra.KEY_SELECT_ITEMS)
        val defaultArgs = bundleOf(Extra.KEY_SELECT_ITEMS to selectedItems)
        val creationExtras =
            MutableCreationExtras(defaultViewModelCreationExtras).apply {
                this[VIEW_MODEL_DEFAULT_ARGS_KEY] = defaultArgs
            }
        val factory = OrderViewModelFactory()
        viewModel =
            ViewModelProvider(
                viewModelStore,
                factory,
                creationExtras,
            )[OrderViewModel::class.java]
        binding.vm = viewModel
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clOrder) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarOrder)
        binding.toolbarOrder.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun initAdapter() {
        binding.rvCoupon.apply {
            adapter = couponAdapter
            itemAnimator = null
        }
    }

    private fun observeViewModel() {
        viewModel.coupons.observe(this) { coupons ->
            couponAdapter.submitList(coupons)
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun onClickSelect(coupon: CouponUiModel) {
        viewModel.selectCoupon(coupon)
    }

    companion object {
        fun newIntent(
            context: Context,
            selectedCartItem: List<CartItemUiModel>,
        ): Intent =
            Intent(context, OrderActivity::class.java).apply {
                putParcelableArrayListExtra(Extra.KEY_SELECT_ITEMS, ArrayList(selectedCartItem))
            }

        private val VIEW_MODEL_DEFAULT_ARGS_KEY = object : CreationExtras.Key<Bundle> {}
    }
}
