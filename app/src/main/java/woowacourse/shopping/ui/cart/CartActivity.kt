package woowacourse.shopping.ui.cart

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.common.observeEvent
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.order.remote.RemoteOrderRepository
import woowacourse.shopping.data.product.remote.retrofit.RemoteProductRepository
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.repository.RecentProductRepository

class CartActivity : AppCompatActivity() {
    private lateinit var cartSelectionFragment: Fragment
    private lateinit var cartRecommendFragment: Fragment

    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CartViewModel> {
        CartViewModelFactory(
            RemoteProductRepository(),
            RecentProductRepository.getInstance(),
            RemoteCartRepository(),
            RemoteOrderRepository(),
        )
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
            if (supportFragmentManager.findFragmentById(R.id.fragment_container_view_cart) is CartSelectionFragment) {
                changeFragment(cartRecommendFragment)
            }
        }
    }
}
