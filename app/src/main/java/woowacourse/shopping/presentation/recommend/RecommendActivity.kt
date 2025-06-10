package woowacourse.shopping.presentation.recommend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityRecommendBinding
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.getParcelableArrayListExtraCompat
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.order.OrderActivity

class RecommendActivity :
    AppCompatActivity(),
    RecommendClickListener,
    CartCounterClickListener {
    private lateinit var binding: ActivityRecommendBinding
    private lateinit var viewModel: RecommendViewModel
    private val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
        setupViewModel()
        initInsets()
        setupToolbar()
        initAdapter()
        observeViewModel()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommend)
        binding.clickListener = this
        binding.lifecycleOwner = this
    }

    private fun setupViewModel() {
        val selectedCartItems =
            intent.getParcelableArrayListExtraCompat<CartItemUiModel>(Extra.KEY_SELECT_ITEMS)
        val defaultArgs = bundleOf(Extra.KEY_SELECT_ITEMS to selectedCartItems)
        val creationExtras =
            MutableCreationExtras(defaultViewModelCreationExtras).apply {
                this[VIEW_MODEL_DEFAULT_ARGS_KEY] = defaultArgs
            }
        val factory = RecommendViewModelFactory()
        viewModel =
            ViewModelProvider(
                viewModelStore,
                factory,
                creationExtras,
            )[RecommendViewModel::class.java]
        binding.vm = viewModel
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clRecommend) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbRecommend)
        binding.tbRecommend.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun initAdapter() {
        binding.rvRecommend.apply {
            adapter = recommendAdapter
            itemAnimator = null
        }
    }

    private fun observeViewModel() {
        viewModel.recommendProducts.observe(this) {
            recommendAdapter.submitList(it)
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun onClickAddToCart(cartItem: CartItemUiModel) {
        viewModel.addToCart(cartItem)
    }

    override fun onClickOrder() {
        val selectedItems = viewModel.selectedItems.value ?: return
        val intent =
            OrderActivity.newIntent(this, selectedItems)
        startActivity(intent)
    }

    override fun onClickPlus(id: Long) {
        viewModel.increaseQuantity(id)
    }

    override fun onClickMinus(id: Long) {
        viewModel.decreaseQuantity(id)
    }

    companion object {
        fun newIntent(
            context: Context,
            selectedItems: List<CartItemUiModel>,
        ): Intent =
            Intent(context, RecommendActivity::class.java).apply {
                putParcelableArrayListExtra(Extra.KEY_SELECT_ITEMS, ArrayList(selectedItems))
            }

        private val VIEW_MODEL_DEFAULT_ARGS_KEY = object : CreationExtras.Key<Bundle> {}
    }
}
