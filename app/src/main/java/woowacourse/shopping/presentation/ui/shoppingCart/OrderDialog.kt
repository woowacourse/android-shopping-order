package woowacourse.shopping.presentation.ui.shoppingCart

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import woowacourse.shopping.R
import woowacourse.shopping.databinding.DialogOrderBinding
import woowacourse.shopping.presentation.ui.myPage.MyPageActivity

class OrderDialog : DialogFragment() {
    private lateinit var binding: DialogOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLackAmount()
        binding.buttonShoppingCartRecharge.setOnClickListener { goToRecharge() }
    }

    override fun onResume() {
        super.onResume()
        setWidth()
    }

    private fun goToRecharge() {
        startActivity(Intent(requireContext(), MyPageActivity::class.java))
        dismiss()
    }

    private fun setLackAmount() {
        val lackAmount: Int = arguments?.getInt(LACK_AMOUNT) ?: return dismiss()
        binding.textShoppingCartLackValue.text = getString(R.string.detailPriceFormat, lackAmount)
    }

    private fun setWidth() {
        val windowManager: WindowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (windowManager.currentWindowMetrics.bounds.width() * 0.95).toInt()
        } else {
            (windowManager.defaultDisplay.width * 0.95).toInt()
        }
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        private const val LACK_AMOUNT = "LACK_AMOUNT"
        const val TAG = "ORDER_DIALOG"

        fun makeDialog(lackAmount: Int): OrderDialog {
            return OrderDialog().apply {
                val bundle = Bundle()
                bundle.putInt(LACK_AMOUNT, lackAmount)
                arguments = bundle
            }
        }
    }
}
