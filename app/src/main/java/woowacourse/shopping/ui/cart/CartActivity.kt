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
import woowacourse.shopping.ui.cart.adapter.CartAdapter

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
    private val adapter by lazy { CartAdapter(viewModel) }

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
        /*
        binding.rvCart.itemAnimator = null
        binding.rvCart.adapter = adapter

        viewModel.cartUiState.observe(this) {
            val cartUiState = it.getContentIfNotHandled() ?: return@observe
            when (cartUiState) {
                CartUiState.Failure -> {
                    Toast.makeText(
                        this,
                        R.string.load_page_error,
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                CartUiState.Loading -> {
                    binding.layoutCartSkeleton.visibility = View.VISIBLE
                    binding.rvCart.visibility = View.GONE
                }

                is CartUiState.Success -> {
                    binding.layoutCartSkeleton.visibility = View.GONE
                    binding.rvCart.visibility = View.VISIBLE
                    adapter.submitList(cartUiState.cartUiModels)
                }
            }
        }
         */
    }
}
