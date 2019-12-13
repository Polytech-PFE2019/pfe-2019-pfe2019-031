package ez.com.inside.activities.usage;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.business.usagetime.CorrespondanceItem;

public class CorrespondanceAdapter extends RecyclerView.Adapter<CorrespondanceAdapter.MyViewHolder> {

    private List<CorrespondanceItem> mDataset;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView time;
        private TextView text;
        public ConstraintLayout layout;
        public MyViewHolder(ConstraintLayout v) {
            super(v);
            this.image = v.findViewById(R.id.imageCorresponding);
            this.time = v.findViewById(R.id.timeCorresponding);
            this.text = v.findViewById(R.id.textCorresponding);

            layout = v;
        }
    }

    public CorrespondanceAdapter(List<CorrespondanceItem> myDataset) {
        mDataset = myDataset;
    }


    @Override
    public CorrespondanceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.corresponding_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CorrespondanceItem item = mDataset.get(position);
        holder.time.setText(Long.toString(item.getTime()));
        holder.text.setText(item.getText());
        holder.image.setImageDrawable(item.getImage());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
