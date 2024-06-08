package woowacourse.shopping.presentation.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingCartApplication
import woowacourse.shopping.common.observeEvent
import woowacourse.shopping.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var cartSelectionFragment: Fragment
    private lateinit var cartRecommendFragment: Fragment

    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CartViewModel> {
        (application as ShoppingCartApplication).getCartViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CartFragmentFactory(viewModel)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        cartSelectionFragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, CartSelectionFragment::class.java.name)
        cartRecommendFragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, CartRecommendFragment::class.java.name)
        // binding = DataBindingUtil.setContentView(this, layoutId)

        changeFragment(cartSelectionFragment)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.listener = viewModel
        initializeView()
    }

    private fun initializeView() {
        initializeToolbar()
        initializeCartAdapter()
        viewModel.changedCartEvent.observe(this) {
            it.getContentIfNotHandled() ?: return@observe
            setResult(Activity.RESULT_OK)
        }
        viewModel.isSuccessCreateOrder.observe(this) {
            val isSuccessCreateOrder = it.getContentIfNotHandled() ?: return@observe
            if (isSuccessCreateOrder) {
                showDialogSuccessCreateOrder()
            } else {
                showToastFailureCreateOrder()
            }
        }
    }

    private fun showDialogSuccessCreateOrder() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.create_order_success_title))
            .setMessage(getString(R.string.create_order_success))
            .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
                navigateToProductsView()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToProductsView() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToastFailureCreateOrder() {
        Toast.makeText(this, R.string.create_order_failure, Toast.LENGTH_SHORT).show()
    }

    private fun initializeToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view_cart, fragment)
            addToBackStack(null)
        }
    }

    private fun initializeCartAdapter() {
        viewModel.navigateEvent.observeEvent(this) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_cart)
            if (fragment is CartSelectionFragment) {
                changeFragment(cartRecommendFragment)
            } else {
                viewModel.createOrder()
            }
        }
    }
}
