package martell.com.vice;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by adao1 on 4/18/2016.
 */

/**
 * Makes the spacking between items in the grid layout.
 * Takes in int value for the amount of spacing.
 */
public class RV_SpaceDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;
    public RV_SpaceDecoration(int space) {
        this.mSpace = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0||parent.getChildAdapterPosition(view) == 1)
            outRect.top = mSpace;
    }
}