package woowacourse.shopping.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    val viewModel: CartViewModel by lazy {
        ViewModelProvider(
            this,
            CartViewModelFactory(application as ShoppingApplication),
        )[CartViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        viewModel.totalProducts.observe(this) {
            viewModel.postTotalCount()
            viewModel.postTotalAmount()
        }

        viewModel.cartItemCount.observe(this) { cartItemCount ->
            val fragment = when (cartItemCount) {
                0-> CartRecommendationFragment()
                else -> CartSelectionFragment()
            }
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container_cart_selection, fragment)
            }
        }

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
