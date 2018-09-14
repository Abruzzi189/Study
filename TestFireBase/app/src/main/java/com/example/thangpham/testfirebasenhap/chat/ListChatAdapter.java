package com.example.thangpham.testfirebasenhap.chat;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.thangpham.testfirebasenhap.R;
import com.example.thangpham.testfirebasenhap.chat.ListChatAdapter.ListUserHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import java.util.List;

public class ListChatAdapter extends Adapter<ListUserHolder> {

  private UserModel userModel;
  private Context context;
  private List<UserModel> listUsers;

  public ListChatAdapter(Context context, List<UserModel> listUsers,UserModel userModel) {
    this.context = context;
    this.listUsers = listUsers;
    this.userModel=userModel;
  }

  @Override
  public ListUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.custom_list_user, parent, false);

    return new ListUserHolder(view);
  }

  @Override
  public void onBindViewHolder(ListUserHolder holder, int position) {
    holder.setData(listUsers.get(position));
  }

  @Override
  public int getItemCount() {
    Log.e("ThangPham","size: "+ listUsers.size());
    return listUsers.size();
  }

  public class ListUserHolder extends ViewHolder {

    ImageView ivAvatar;
    TextView tvName;


    public ListUserHolder(View itemView) {
      super(itemView);
      ivAvatar = itemView.findViewById(R.id.iv_avatar);
      tvName = itemView.findViewById(R.id.tv_name);

    }

    public void setData(final UserModel data) {
      Picasso.get().load(Uri.parse(data.getAvatarLink())).into(ivAvatar);
      tvName.setText(data.getName());
      itemView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(context,ChatActivity.class);
          intent.putExtra(MainActivity.USER_FROM,userModel);
          intent.putExtra(MainActivity.USER_TO,data);
          context.startActivity(intent);
        }
      });
    }
  }
}
