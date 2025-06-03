package woowacourse.shopping.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import retrofit2.HttpException
import woowacourse.shopping.App
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.view.cart.carts.CartListFragment
import woowacourse.shopping.view.cart.recommend.RecommendFragment
import woowacourse.shopping.view.cart.vm.CartViewModel
import woowacourse.shopping.view.cart.vm.CartViewModelFactory
import woowacourse.shopping.view.core.ext.showToast

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        val container = (application as App).container
        CartViewModelFactory(
            container.cartRepository,
            container.productRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        val category = intent.getStringExtra(EXTRA_CATEGORY)
        viewModel.setCategory(category)

        setUpBinding()
        setUpSystemBars()
        observeViewModel()
    }

    private fun setUpBinding() {
        with(binding) {
            vm = viewModel
            lifecycleOwner = this@CartActivity
        }
    }

    private fun setUpSystemBars() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.action_bar_title_cart_screen)
    }

    private fun observeViewModel() {
        viewModel.uiEvent.observe(this) { event ->
            when (event) {
                is CartUiEvent.ShowCannotIncrease -> {
                    showToast(getString(R.string.text_over_quantity).format(event.quantity))
                }
                CartUiEvent.ChangeScreen -> changeFragment()
                is CartUiEvent.ShowErrorMessage -> {
                    val messageResId = getErrorMessage(event.throwable)
                    showToast(getString(messageResId))
                }
            }
        }
    }

    private fun getErrorMessage(throwable: Throwable): Int {
        return when (throwable) {
            is NullPointerException -> R.string.error_text_null_result
            is HttpException -> R.string.error_text_network_error
            else -> R.string.error_text_unknown
        }
    }

    private fun changeFragment() {
        when (supportFragmentManager.findFragmentById(R.id.fragment_container_view)) {
            is CartListFragment -> {
                supportFragmentManager.commit {
                    replace(R.id.fragment_container_view, RecommendFragment())
                }
            }

            is RecommendFragment -> {}
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onClickHomeButton()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onClickHomeButton() {
        when (supportFragmentManager.findFragmentById(R.id.fragment_container_view)) {
            is CartListFragment -> {
                val resultIntent = Intent()
                setResult(RESULT_OK, resultIntent)
                finish()
            }

            is RecommendFragment ->
                supportFragmentManager.commit {
                    replace(R.id.fragment_container_view, CartListFragment())
                }
        }
    }

    companion object {
        fun newIntent(
            context: Context,
            category: String?,
        ): Intent {
            return Intent(context, CartActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY, category)
            }
        }

        private const val EXTRA_CATEGORY = "category"
    }
}
