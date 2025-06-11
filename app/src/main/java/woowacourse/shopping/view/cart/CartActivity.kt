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
import woowacourse.shopping.App
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.view.cart.carts.CartListFragment
import woowacourse.shopping.view.cart.recommend.RecommendFragment
import woowacourse.shopping.view.cart.vm.CartViewModel
import woowacourse.shopping.view.cart.vm.CartViewModelFactory
import woowacourse.shopping.view.core.ext.showToast
import woowacourse.shopping.view.core.handler.NetworkExceptionHandler
import woowacourse.shopping.view.order.OrderActivity

class CartActivity : AppCompatActivity() {
    private lateinit var networkDelegator: NetworkExceptionHandler

    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        val container = (application as App).container
        CartViewModelFactory(
            container.repositoryModule.defaultCartRepository,
            container.repositoryModule.defaultProductRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        networkDelegator = NetworkExceptionHandler(this)
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

                is CartUiEvent.ChangeScreen -> changeFragment(event.orders)
                is CartUiEvent.ShowErrorMessage -> {
                    networkDelegator.showErrorMessage(event.throwable)
                }

                CartUiEvent.ShowNotHasPurchaseCart -> {
                    showToast(getString(R.string.text_not_has_purchase_cart))
                }
            }
        }
    }

    private fun changeFragment(orders: ShoppingCarts?) {
        when (supportFragmentManager.findFragmentById(R.id.fragment_container_view)) {
            is CartListFragment -> {
                supportFragmentManager.commit {
                    replace(R.id.fragment_container_view, RecommendFragment())
                }
            }

            is RecommendFragment -> {
                orders?.let { startActivity(OrderActivity.newIntent(this, it)) }
            }
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
