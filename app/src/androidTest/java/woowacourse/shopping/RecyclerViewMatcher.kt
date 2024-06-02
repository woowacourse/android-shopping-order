package woowacourse.shopping
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(private val recyclerViewId: Int) {
    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("RecyclerView with id: $recyclerViewId at position: $position")
            }

            public override fun matchesSafely(view: View): Boolean {
                val recyclerView = view.rootView.findViewById<RecyclerView>(recyclerViewId)
                if (recyclerView?.id != recyclerViewId) {
                    return false
                }
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    ?: return false
                val targetView = if (targetViewId == -1) viewHolder.itemView else viewHolder.itemView.findViewById(targetViewId)
                return view === targetView
            }
        }
    }
}
