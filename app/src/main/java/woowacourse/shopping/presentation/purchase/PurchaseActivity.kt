package woowacourse.shopping.presentation.purchase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityPurchaseBinding

class PurchaseActivity : AppCompatActivity() {
    private val binding: ActivityPurchaseBinding by lazy {
        ActivityPurchaseBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<PurchaseViewModel> {
        (application as ShoppingApplication).getPurchaseViewModelFactory()
    }
    private val adapter by lazy {
        CouponListAdapter(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.rvCoupons.itemAnimator = null
        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.layoutManager = LinearLayoutManager(this)
        initObserve()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCoupons()
    }

    private fun initObserve() {
        viewModel.couponUiModels.observe(this) {
            adapter.submitList(it.couponUiModelList)
            Log.d("Purchase", "hello")
        }
    }

    companion object {
        const val CART_ITEMS_ID = "CART_ITEMS_ID"

        fun startActivity(
            context: Context,
            cartItemIds: List<Int>,
        ) {
            val intent = Intent(context, PurchaseActivity::class.java)
            intent.putExtra(CART_ITEMS_ID, cartItemIds.toIntArray())
            context.startActivity(intent)
        }
    }
}
