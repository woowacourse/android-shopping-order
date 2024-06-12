package woowacourse.shopping.ui.cart

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.product.remote.RemoteProductRepository
import woowacourse.shopping.data.recent.local.RoomRecentProductRepository
import woowacourse.shopping.data.remote.ApiError
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.coupon.CouponActivity

class CartActivity : AppCompatActivity() {
    private val cartSelectionFragment: Fragment by lazy { CartSelectionFragment() }
    private val cartRecommendFragment: Fragment by lazy { CartRecommendFragment() }

    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CartViewModel> {
        CartViewModelFactory(
            RemoteProductRepository,
            RoomRecentProductRepository.getInstance(
                ShoppingCartDataBase.getInstance(
                    applicationContext,
                ).recentProductDao(),
            ),
            RemoteCartRepository,
        )
    }

    private val removeFragment: () -> Unit = {
        if (isVisibleCartSelectionFragment()) {
            finish()
        }
        removeLastFragment()
    }

    private val backPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                removeFragment()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if (savedInstanceState == null) {
            addFragment(cartSelectionFragment)
        }

        initializeView()
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view_cart, fragment)
            addToBackStack(null)
        }
    }

    private fun initializeView() {
        initializeToolbar()
        observeData()
        observeErrorEvent()
    }

    private fun initializeToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            removeFragment()
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun isVisibleCartSelectionFragment(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_cart)
        return fragment is CartSelectionFragment
    }

    private fun removeLastFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun observeData() {
        viewModel.changedCartEvent.observe(this) {
            setResult(Activity.RESULT_OK)
        }
        viewModel.orderEvent.observe(this) {
            if (isVisibleCartSelectionFragment()) {
                addFragment(cartRecommendFragment)
            } else {
                viewModel.updateSelectedCartItemIds()
            }
        }
        viewModel.selectedCartItemIds.observe(this) {
            navigateToCouponView(it)
        }
    }

    private fun observeErrorEvent() {
        viewModel.productsLoadError.observe(this) {
            showCartErrorToast(it, R.string.product_load_error)
        }
        viewModel.cartItemAddError.observe(this) {
            showCartErrorToast(it, R.string.cart_item_add_error)
        }
        viewModel.cartItemDeleteError.observe(this) {
            showCartErrorToast(it, R.string.cart_item_delete_error)
        }
    }

    private fun showCartErrorToast(
        throwable: Throwable,
        @StringRes errorMessageResId: Int,
    ) {
        if (throwable is ApiError) {
            showToast(errorMessageResId)
        }
        when (throwable) {
            is ApiError.BadRequest -> showToast(errorMessageResId)
            is ApiError.Unauthorized -> showToast(R.string.unauthorized_error)
            is ApiError.Forbidden -> showToast(R.string.unauthorized_error)
            is ApiError.NotFound -> showToast(R.string.product_not_found_error)
            is ApiError.InternalServerError -> showToast(R.string.server_error)
            is ApiError.Exception -> showToast(errorMessageResId)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToCouponView(selectedCartItemIds: List<Int>) {
        val intent = CouponActivity.newIntent(this, selectedCartItemIds)
        startActivity(intent)
    }
}
