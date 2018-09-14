package android.gpuimage.com.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ThoNh on 5/9/2018.
 */

public class ListMusicAdapter extends RecyclerView.Adapter<ListMusicAdapter.ListMusicHolder>{
    //region variable
    private static final String TAG = "ListMusicAdapter";
    List<MusicModel> listMusic;
    Context context;
    //endregion

    //region constructor
    public ListMusicAdapter(List<MusicModel> listMusic, Context context) {
        this.listMusic = listMusic;
        this.context = context;
    }
    //endregion

    //region function
    @Override
    public ListMusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_music,parent,false);
        return new ListMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(ListMusicHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+position);
        holder.setData(listMusic.get(position));
        if(position==listMusic.size()-1)
        {
            Log.d(TAG, "onBindViewHolder: 123");
        }
    }

    @Override
    public int getItemCount() {
        return listMusic.size();
    }
    //endregion

    //region inner class
    public class ListMusicHolder extends RecyclerView.ViewHolder
    {
        //region variable
        TextView tvSong;
        //endregion

        //region constructor
        public ListMusicHolder(View itemView) {
            super(itemView);
            tvSong = itemView.findViewById(R.id.tv_song_name);

        }
        //endregion

        //region listener
        public void setData(final MusicModel data)
        {
            tvSong.setText(data.getSongName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicHandler.playMusic(context,data);
                   MainActivity.saveMusicModel = data;

                }
            });
        }
        //endregion
    }
    //endregion
}
