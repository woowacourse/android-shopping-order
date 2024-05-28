package woowacourse.shopping.view.home.adapter.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductPlaceholderBinding

class ProductPlaceholderAdapter : RecyclerView.Adapter<ProductPlaceholderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductPlaceholderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemProductPlaceholderBinding.inflate(layoutInflater)
        return ProductPlaceholderViewHolder(binding)
    }

    override fun getItemCount(): Int = NUMBER_OF_PLACEHOLDERS

    override fun onBindViewHolder(holder: ProductPlaceholderViewHolder, position: Int) {

    }

    companion object {
        private const val NUMBER_OF_PLACEHOLDERS = 6
    }
}
