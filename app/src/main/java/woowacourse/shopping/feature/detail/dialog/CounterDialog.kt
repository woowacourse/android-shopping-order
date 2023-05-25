package woowacourse.shopping.feature.detail.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import woowacourse.shopping.R
import woowacourse.shopping.common_ui.CounterView
import woowacourse.shopping.data.repository.local.CartRepositoryImpl
import woowacourse.shopping.data.service.CartProductRemoteService
import woowacourse.shopping.databinding.DialogCounterBinding
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.util.getParcelableCompat

class CounterDialog : DialogFragment(), CounterDialogContract.View {
    private var _binding: DialogCounterBinding? = null
    private val binding: DialogCounterBinding
        get() = _binding!!

    private lateinit var presenter: CounterDialogContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogCounterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product =
            arguments?.getParcelableCompat<ProductUiModel>(PRODUCT_KEY) ?: return dismiss()
        val cartId =
            arguments?.getLong(CART_ID_KEY, 0L) ?: return dismiss()
        setInitPresenter(product, cartId)
        binding.presenter = presenter
        binding.product = product

        binding.counterView.countStateChangeListener =
            object : CounterView.OnCountStateChangeListener {
                override fun onCountChanged(counterNavigationView: CounterView?, count: Int) {
                    presenter.changeCount(count)
                }
            }
    }

    private fun setInitPresenter(product: ProductUiModel, cartId: Long?) {
        presenter = CounterDialogPresenter(
            this,
            CartRepositoryImpl(CartProductRemoteService()),
            product,
            cartId,
        )
        presenter.initPresenter()
    }

    override fun setCountState(count: Int) {
        binding.counterView.setCountState(count)
    }

    override fun notifyChangeApplyCount(changeApplyCount: Int) {
        parentFragmentManager.setFragmentResult(
            CHANGE_COUNTER_APPLY_KEY,
            bundleOf(COUNT_KEY to changeApplyCount),
        )
        requireActivity().runOnUiThread {
            Toast.makeText(
                requireContext(),
                getString(R.string.success_add_cart),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    override fun exit() {
        requireActivity().runOnUiThread { dismiss() }
    }

    companion object {

        private const val PRODUCT_KEY = "product_key"
        private const val CART_ID_KEY = "cart_id_key"
        const val CHANGE_COUNTER_APPLY_KEY = "change_counter_apply_key"
        const val COUNT_KEY = "change_count_key"

        @JvmStatic
        fun newInstance(product: ProductUiModel, cartId: Long?): CounterDialog {
            return CounterDialog().apply {
                arguments = bundleOf(
                    PRODUCT_KEY to product.copy(),
                )
                cartId?.let { arguments?.putLong(CART_ID_KEY, cartId) }
            }
        }
    }
}
