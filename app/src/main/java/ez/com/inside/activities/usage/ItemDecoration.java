package ez.com.inside.activities.usage;

import android.content.res.Resources;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Charly on 05/12/2017.
 */

public class ItemDecoration extends RecyclerView.ItemDecoration
{
    private int spaceVertical;

    public ItemDecoration()
    {
        Resources r = Resources.getSystem();
        spaceVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r.getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        outRect.bottom = spaceVertical;

        if(parent.getChildAdapterPosition(view) == 0)
            outRect.top = spaceVertical / 3;
    }
}
