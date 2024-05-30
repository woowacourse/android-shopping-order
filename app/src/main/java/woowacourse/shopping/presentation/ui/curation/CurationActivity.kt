package woowacourse.shopping.presentation.ui.curation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCurationBinding
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.detail.ProductDetailViewModel
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingAdapter
import kotlin.concurrent.thread

class CurationActivity : BindingActivity<ActivityCurationBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_curation

    private val viewModel: CurationViewModel by viewModels { ViewModelFactory() }

    private val curationAdapter: CurationAdapter by lazy { CurationAdapter(viewModel) }

    override fun initStartView() {
        title = "Curation"

        binding.rvCurations.adapter = curationAdapter
        binding.curationActionHandler = viewModel

        viewModel.cartProducts.observe(this) {
            when(it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    curationAdapter.submitList(it.data)
                    binding.tvPrice.text = getString(R.string.won, it.data.sumOf {
                        it.quantity * it.price
                    })

                    binding.tvOrderCount.text = it.data.sumOf {
                        it.quantity
                    }.toString()
                }
            }
        }

        viewModel.eventHandler.observe(this, EventObserver {
            when(it) {
                is CurationEvent.SuccessOrder -> {
                    Toast.makeText(this, "주문이 성공적으로 진행되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}