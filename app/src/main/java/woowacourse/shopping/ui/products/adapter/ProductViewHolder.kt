package woowacourse.shopping.ui.products.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.products.ProductWithQuantityUiModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel

class ProductViewHolder(
    private val binding: ItemProductBinding,
    private val viewModel: ProductContentsViewModel,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(productWithQuantity: ProductWithQuantityUiModel) {
        Log.e("test", "22")
        binding.productWithQuantity = productWithQuantity
        binding.vm = viewModel
    }
}
