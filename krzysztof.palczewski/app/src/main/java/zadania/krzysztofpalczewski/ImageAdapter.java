package zadania.krzysztofpalczewski;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by K on 2017-01-26.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private Context mContext;
    private List<Array> imageList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            desc = (TextView) view.findViewById(R.id.description);
            imageView = (ImageView) view.findViewById(R.id.image);
        }
    }

    public ImageAdapter(Context mContext, List<Array> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Array imgDetails = imageList.get(position);
        holder.title.setText(imgDetails.getTitle());
        holder.desc.setText(imgDetails.getDesc());

        if (imgDetails.getImage() != null) {
            holder.imageView.setImageBitmap(imgDetails.getImage());
        } else {
            holder.imageView.setImageResource(R.drawable.temporary_image);
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}

