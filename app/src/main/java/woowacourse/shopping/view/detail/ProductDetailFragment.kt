package woowacourse.shopping.view.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.RecentlyProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.RemoteProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl
import woowacourse.shopping.databinding.FragmentProductDetailBinding
import woowacourse.shopping.utils.ShoppingUtils.makeToast
import woowacourse.shopping.view.MainActivityListener
import woowacourse.shopping.view.ViewModelFactory
import woowacourse.shopping.utils.exception.ErrorEvent

class ProductDetailFragment : Fragment(), OnClickNavigateDetail {
    private var mainActivityListener: MainActivityListener? = null
    private var _binding: FragmentProductDetailBinding? = null
    val binding: FragmentProductDetailBinding get() = _binding!!
    private val productDetailViewModel: ProductDetailViewModel by lazy {
        val viewModelFactory =
            ViewModelFactory {
                ProductDetailViewModel(
                    productRepository = RemoteProductRepositoryImpl(),
                    shoppingCartRepository = RemoteShoppingCartRepositoryImpl(),
                    recentlyProductRepository = RecentlyProductRepositoryImpl(requireContext()),
                )
            }
        viewModelFactory.create(ProductDetailViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadProduct()
        observeData()
    }

    private fun observeData() {
        productDetailViewModel.productDetailEvent.observe(viewLifecycleOwner) { productDetailState ->
            when (productDetailState) {
                is ProductDetailEvent.AddShoppingCart.Success -> {
                    mainActivityListener?.saveUpdateProduct(
                        productDetailState.productId,
                        productDetailState.count,
                    )
                    requireContext().makeToast(
                        getString(R.string.add_cart_text),
                    )
                }

                ProductDetailEvent.UpdateRecentlyProductItem.Success -> {
                    mainActivityListener?.saveUpdateRecentlyProduct()
                }
            }
        }

        productDetailViewModel.errorEvent.observe(viewLifecycleOwner) { errorState ->
            requireContext().makeToast(
                errorState.receiveErrorMessage(),
            )
        }
    }

    private fun receiveId(): Long {
        return arguments?.getLong(PRODUCT_ID) ?: throw ErrorEvent.LoadDataEvent()
    }

    private fun loadProduct() {
        try {
            productDetailViewModel.loadProductItem(receiveId())
        } catch (e: ErrorEvent.LoadDataEvent) {
            requireContext().makeToast(
                getString(R.string.error_data_load),
            )
            clickClose()
        }
    }

    private fun initView() {
        binding.vm = productDetailViewModel
        binding.onClickNavigateDetail = this
        binding.onClickDetail = productDetailViewModel
        binding.onClickCartItemCounter = productDetailViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainActivityListener = null
    }

    override fun clickClose() {
        mainActivityListener?.popFragment()
    }

    companion object {
        fun createBundle(id: Long): Bundle {
            return Bundle().apply { putLong(PRODUCT_ID, id) }
        }

        private const val PRODUCT_ID = "productId"
    }
}
