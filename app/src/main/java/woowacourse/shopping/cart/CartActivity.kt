package woowacourse.shopping.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    val viewModel: CartViewModel by viewModels { CartViewModelFactory() }
    private var hasHandledTotalCount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        observeCartViewModel()

        applyWindowInsets()
        setSupportActionBar()
    }

    private fun setSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.text_cart_action_bar)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun observeCartViewModel() {
        viewModel.cartProducts.observe(this) { viewModel.refreshProductsInfo() }
        viewModel.selectedEvent.observe(this) { viewModel.refreshProductsInfo() }
        viewModel.totalCount.observe(this) { replaceFragmentByTotalCount(it) }
    }

    private fun replaceFragmentByTotalCount(totalCount: Int) {
        if (!hasHandledTotalCount && totalCount != -1) {
            hasHandledTotalCount = true
            when (totalCount) {
                0 -> replaceCartRecommendationFragment()
                else -> replaceCartSelectionFragment()
            }
        }
    }

    private fun replaceCartRecommendationFragment() {
        binding.checkboxAllSelection.visibility = View.GONE
        binding.textViewAllSelection.visibility = View.GONE

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragment_container_cart_selection,
                CartRecommendationFragment::class.java,
                null,
            )
        }
    }

    private fun replaceCartSelectionFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragment_container_cart_selection,
                CartSelectionFragment::class.java,
                null,
            )
        }
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
