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
import woowacourse.shopping.view.cart.vm.CartViewModel
import woowacourse.shopping.view.cart.vm.CartViewModelFactory
import woowacourse.shopping.view.core.ext.showToast
import kotlin.io.path.Path

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        val container = (application as App).container
        CartViewModelFactory(
            container.cartRepository,
            container.historyLoader,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)

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
        viewModel.event.observe(this) {
            when (it) {
                is CartUiEvent.ShowCannotIncrease -> {
                    showToast(getString(R.string.text_over_quantity).format(it.quantity))
                }

                CartUiEvent.ChangeScreen -> onClickOrderButton()
            }
        }
    }

    private fun onClickOrderButton() {
        when (supportFragmentManager.findFragmentById(R.id.fragment_container_view)) {
            is CartListFragment -> {
                supportFragmentManager.commit {
                    replace(R.id.fragment_container_view, OrderCompleteFragment())
                }
            }
            is OrderCompleteFragment -> {}
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

    private fun onClickHomeButton(){
        when (supportFragmentManager.findFragmentById(R.id.fragment_container_view)) {
            is CartListFragment -> {
                val resultIntent = Intent()
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            is OrderCompleteFragment -> supportFragmentManager.commit {
                replace(R.id.fragment_container_view, CartListFragment())
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
