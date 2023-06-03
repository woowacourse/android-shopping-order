package woowacourse.shopping.ui.orderdetail

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import woowacourse.shopping.R
import woowacourse.shopping.databinding.DialogOrderDetailBinding
import woowacourse.shopping.ui.model.UiOrder

class OrderDetailDialog(
    private val orderInfo: UiOrder,
    private val dismissListener: (() -> Unit)?
) : DialogFragment() {
    private var _binding: DialogOrderDetailBinding? = null
    private val binding get() = _binding ?: error(R.string.binding_error)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogOrderDetailBinding.inflate(inflater, container, false)
        binding.orderInfo = orderInfo
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOrderItemRecyclerViewAdapter()
    }

    private fun initOrderItemRecyclerViewAdapter() {
        val adapter = OrderDetailAdapter()
        adapter.submitList(orderInfo.orderItems)
        binding.rvOrderItem.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        setLayout()
    }

    private fun setLayout() {
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            (resources.displayMetrics.heightPixels * 0.8).toInt()
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.let { it() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
