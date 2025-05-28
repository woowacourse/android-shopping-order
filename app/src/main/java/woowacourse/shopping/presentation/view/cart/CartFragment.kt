package woowacourse.shopping.presentation.view.cart

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.adapter.CartAdapter

class CartFragment :
    BaseFragment<FragmentCartBinding>(R.layout.fragment_cart),
    CartAdapter.CartEventListener,
    ItemCounterListener {
    private val cartAdapter: CartAdapter by lazy {
        CartAdapter(
            eventListener = this,
            itemCounterListener = this,
        )
    }

    private val viewModel: CartViewModel by viewModels { CartViewModel.Factory }
    private val backCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToScreen()
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        initListener()
        setCartAdapter()

        requireActivity().onBackPressedDispatcher.addCallback(backCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backCallback.remove()
    }

    override fun onProductDeletion(cartItem: CartItem) {
        viewModel.deleteProduct(cartItem)
    }

    override fun increase(product: ProductUiModel) {
        viewModel.increaseAmount(product)
    }

    override fun decrease(product: ProductUiModel) {
        viewModel.decreaseAmount(product)
    }

    private fun setCartAdapter() {
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun initObserver() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.page.observe(viewLifecycleOwner) {
            binding.recyclerViewCart.smoothScrollToPosition(0)
        }

        viewModel.products.observe(viewLifecycleOwner) {
            cartAdapter.updateCartItems(it)
        }

        viewModel.deleteState.observe(viewLifecycleOwner) {
            it?.let {
                cartAdapter.removeProduct(it)
                viewModel.fetchShoppingCart(isNextPage = false, isRefresh = true)
            }
        }

        viewModel.itemUpdateEvent.observe(viewLifecycleOwner) {
            cartAdapter.updateItem(it)
        }
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener {
            navigateToScreen()
        }
    }

    private fun navigateToScreen() {
        parentFragmentManager.popBackStack()
        parentFragmentManager.commit {
            remove(this@CartFragment)
        }
    }
}
