package com.application.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.application.actionbar.NoFragmentActionBar;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.ImageRequest;
import com.application.connection.request.ListPublicImageRequest;
import com.application.connection.request.ListSendImageRequest;
import com.application.connection.response.ListPublicImageResponse;
import com.application.connection.response.ListSendImageResponse;
import com.application.connection.response.UserInfoResponse;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshGridView;
import com.application.ui.profile.DetailPicturePreviousPhotoActivity;
import com.application.ui.profile.DetailPictureProfileActivity;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class GalleryActivity extends BaseFragmentActivity implements
    ResponseReceiver, OnItemClickListener {

  public static final String GALLERY_INDEX = "gallery_index";
  public static final String GALLERY_IMAGE_ID = "gallery_image_id";
  protected final int RESPONSE_LOAD_PUBLIC_GALLERY = 1;
  protected final int RESPONSE_LOAD_SEND_GALLERY = 2;
  protected final int LIST_DEFAULT_SIZE = 24;
  private ProfilePictureData mProfilePictureData;
  private PullToRefreshGridView mpPullToRefreshGridView;
  private GridView mGridPeople;
  private GalleryAdapter mGalleryAdapter;
  private boolean isPublicImage = true;
  private NoFragmentActionBar mActionBar;
  private int totalImage = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    mProfilePictureData = new ProfilePictureData();
    List<String> listImg = new ArrayList<String>();
    super.onCreate(savedInstanceState);
    loadTransportData();
    if (mProfilePictureData.getDataType() == ProfilePictureData.TYPE_GALLERY) {
      isPublicImage = true;
      loadListPublicImage(mProfilePictureData.getUserId());
    } else {
      isPublicImage = false;
      loadListSendImage(mProfilePictureData.getUserId());
    }
    totalImage = mProfilePictureData.getNumberOfImage();
    setContentView(R.layout.activity_gallery);
    initActionBar();
    initView(listImg);
    initialNotificationVew();
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
  }

  /**
   * Load data from bundle
   */
  public void loadTransportData() {
    Bundle dataTransport = getIntent().getExtras();
    if (dataTransport != null) {
      if (mProfilePictureData == null) {
        mProfilePictureData = new ProfilePictureData();
      }
      mProfilePictureData.setDataFromBundle(dataTransport);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initView(List<String> listImg) {
    // Create an buffer to inflate stub
    mpPullToRefreshGridView = (PullToRefreshGridView) findViewById(
        R.id.fragment_gallery_grid_people);
    mpPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2() {
      @Override
      public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        refresh();
      }

      @Override
      public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if (isPublicImage) {
          loadListPublicImage(mProfilePictureData.getUserId());
        } else {
          loadListSendImage(mProfilePictureData.getUserId());
        }
      }
    });
    mpPullToRefreshGridView.setMode(Mode.BOTH);
    Resources resource = getResources();
    mpPullToRefreshGridView.setPullLabelFooter(resource
        .getString(R.string.pull_to_load_more_pull_label));
    mpPullToRefreshGridView.setReleaseLabelFooter(resource
        .getString(R.string.pull_to_load_more_release_label));
    mGridPeople = mpPullToRefreshGridView.getRefreshableView();
    int avaSize = getResources().getDimensionPixelSize(
        R.dimen.activity_setupprofile_img_avatar_height);
    mGalleryAdapter = new GalleryAdapter(this, listImg,
        mProfilePictureData.getGender(), avaSize);
    mGridPeople.getViewTreeObserver().addOnGlobalLayoutListener(
        new OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            int mImageThumbSize = getResources()
                .getDimensionPixelSize(
                    R.dimen.image_thumbnail_size);
            final int mImageThumbSpacing = getResources()
                .getDimensionPixelSize(
                    R.dimen.image_thumbnail_spacing);
            final int numColumns = (int) Math.floor(mGridPeople
                .getWidth()
                / (mImageThumbSize + mImageThumbSpacing));
            if (numColumns > 0) {
              mGridPeople.setNumColumns(numColumns);
              final int columnWidth = (mGridPeople.getWidth() / numColumns)
                  - mImageThumbSpacing;
              mGalleryAdapter.setAvatarSize(columnWidth);
            }
          }
        });
    mGridPeople.setAdapter(mGalleryAdapter);
    mGridPeople.setOnItemClickListener(this);
  }

  private void refresh() {
    mGalleryAdapter.clear();
    mProfilePictureData.setListImg(new ArrayList<Image>());
    if (isPublicImage) {
      loadListPublicImage(mProfilePictureData.getUserId());
    } else {
      loadListSendImage(mProfilePictureData.getUserId());
    }

    if (mpPullToRefreshGridView.getMode() != Mode.BOTH) {
      mpPullToRefreshGridView.setMode(Mode.BOTH);
    }
  }

  public void updateTitle(int numOfImg) {
    String format = getResources().getString(R.string.gallery_title);
    String title = String.format(format, (numOfImg == 0) ? "" : "("
        + numOfImg + ")");
    mActionBar.setTextCenterTitle(title);
  }

  /**
   * Start call API to load all image
   */
  public void loadListPublicImage(String userId) {
    String token = UserPreferences.getInstance().getToken();
    ListPublicImageRequest listPublicImageRequest;
    int totalImg = 0;
    if (mGalleryAdapter != null) {
      totalImg = mGalleryAdapter.getCount();
    }
    if (userId == null || "".equals(userId)) {
      listPublicImageRequest = new ListPublicImageRequest(token,
          totalImg, LIST_DEFAULT_SIZE);
    } else {
      listPublicImageRequest = new ListPublicImageRequest(token, userId,
          totalImg, LIST_DEFAULT_SIZE);
    }
    restartRequestServer(RESPONSE_LOAD_PUBLIC_GALLERY,
        listPublicImageRequest);
  }

  /**
   * Start call API to load all image
   */
  public void loadListSendImage(String userId) {
    String token = UserPreferences.getInstance().getToken();
    ListSendImageRequest listSendImageRequest;
    int totalImg = 0;
    if (mGalleryAdapter != null) {
      totalImg = mGalleryAdapter.getCount();
    }

    listSendImageRequest = new ListSendImageRequest(token, userId,
        totalImg, LIST_DEFAULT_SIZE);
    restartRequestServer(RESPONSE_LOAD_SEND_GALLERY, listSendImageRequest);
  }

  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int position,
      long arg3) {
    if (isPublicImage) {
      Intent intent = new Intent(this, DetailPictureProfileActivity.class);
      UserInfoResponse user = new UserInfoResponse(new ResponseData());
      user.setAvataId(mProfilePictureData.getAvata());
      user.setGender(mProfilePictureData.getGender());
      user.setUserId(mProfilePictureData.getUserId());
      user.setPublicImageNumber(totalImage);
      Bundle userData = ProfilePictureData.parseDataToBundle(position,
          user, mProfilePictureData.getImageId(position));
      intent.putExtras(userData);
      startActivity(intent);
      finish();
    } else {
      Intent intent = new Intent(this,
          DetailPicturePreviousPhotoActivity.class);
      UserInfoResponse user = new UserInfoResponse(new ResponseData());
      user.setAvataId(mProfilePictureData.getAvata());
      user.setGender(mProfilePictureData.getGender());
      user.setUserId(mProfilePictureData.getUserId());
      user.setPublicImageNumber(totalImage);
      Bundle userData = ProfilePictureData.parseDataToBundle(position,
          user, mProfilePictureData.getImageId(position));
      intent.putExtras(userData);
      intent.putExtra(DetailPicturePreviousPhotoActivity.KEY_IS_GALLERY,
          true);
      startActivity(intent);
      finish();
    }
  }

  /**
   * return imageId to ChatFragment
   *
   * @author tungdx
   */
  private void returnImage(final Intent intent) {
    AlertDialog confirmDialog = new CustomConfirmDialog(this,
        getString(R.string.confirm),
        getString(R.string.confirm_send_previous_photo_msg), true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            setResult(RESULT_OK, intent);
            finish();
          }
        })
        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    confirmDialog.setCancelable(false);
    confirmDialog.show();

    int dividerId = confirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = confirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          confirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  @Override
  public void startRequest(int loaderId) {
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (response instanceof ListPublicImageResponse) {
      ListPublicImageResponse listPublicImageResponse = (ListPublicImageResponse) response;
      if (listPublicImageResponse.getCode() == Response.SERVER_SUCCESS) {
        ArrayList<Image> listImage = listPublicImageResponse
            .getListImage();
        int size = listImage.size();
        if (size <= 0) {
          mpPullToRefreshGridView.setMode(Mode.PULL_FROM_START);
        } else {
          mpPullToRefreshGridView.setMode(Mode.BOTH);
        }
        mProfilePictureData.addListImg(listImage);
        ArrayList<String> listImageID = mProfilePictureData
            .getListImg();
        mGalleryAdapter.mlistImg = listImageID;
        int numberOfImage = listImageID.size();
        if (numberOfImage > mProfilePictureData.getNumberOfImage()) {
          mProfilePictureData.setNumberOfImage(numberOfImage);
        } else if (numberOfImage == 0) {
          mProfilePictureData.setNumberOfImage(mGalleryAdapter
              .getCount());
        }
        totalImage = mProfilePictureData.getNumberOfImage();
        mGalleryAdapter.notifyDataSetChanged();
      }
      mpPullToRefreshGridView.onRefreshComplete();
    } else if (response instanceof ListSendImageResponse) {
      ListSendImageResponse listSendImageResponse = (ListSendImageResponse) response;
      if (listSendImageResponse.getCode() == Response.SERVER_SUCCESS) {
        ArrayList<Image> listImage = listSendImageResponse
            .getListImage();
        int size = listImage.size();
        if (size <= 0) {
          mpPullToRefreshGridView.setMode(Mode.PULL_FROM_START);
        }
        mProfilePictureData.addListImg(listImage);
        ArrayList<String> listImageID = mProfilePictureData
            .getListImg();
        mGalleryAdapter.mlistImg = listImageID;
        int numberOfImage = listImageID.size();
        if (numberOfImage > mProfilePictureData.getNumberOfImage()) {
          mProfilePictureData.setNumberOfImage(numberOfImage);
        }

        totalImage = listSendImageResponse.getTotal();

        mGalleryAdapter.notifyDataSetChanged();
      }
      mpPullToRefreshGridView.onRefreshComplete();
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case RESPONSE_LOAD_PUBLIC_GALLERY:
        response = new ListPublicImageResponse(data);
        break;
      case RESPONSE_LOAD_SEND_GALLERY:
        response = new ListSendImageResponse(data);
        break;
      default:
        // Do nothing
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public boolean hasImageFetcher() {
    return true;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public boolean hasShowNotificationView() {
    return true;
  }

  @Override
  public boolean isNoTitle() {
    return false;
  }

  private void initActionBar() {
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    mActionBar = new NoFragmentActionBar(this);
    mActionBar.syncActionBar();
  }

  public boolean isGalleryView() {
    return isPublicImage;
  }

  private class GalleryAdapter extends BaseAdapter {

    private List<String> mlistImg;
    private Context mContext;
    private int mAvatarSize;

    public GalleryAdapter(Context context, List<String> listImg,
        int gender, int avatarSize) {
      this.mContext = context;
      this.mlistImg = listImg;
      this.mAvatarSize = avatarSize;
    }

    public void clear() {
      this.mlistImg.clear();
      notifyDataSetChanged();
    }

    public void setAvatarSize(int height) {
      if (height == mAvatarSize) {
        return;
      }
      mAvatarSize = height;
      notifyDataSetChanged();
    }

    @Override
    public int getCount() {
      if (mlistImg == null) {
        return 0;
      }
      return mlistImg.size();
    }

    @Override
    public Object getItem(int position) {
      return mlistImg.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = View.inflate(mContext, R.layout.item_gallery_grid_image, null);
        holder.imgDisplay = (ImageView) convertView.findViewById(R.id.item_image_gallery);
        holder.frameLayout = (FrameLayout) convertView
            .findViewById(R.id.item_grid_image_gallery_layout_frm);
        GridView.LayoutParams layoutParam = new GridView.LayoutParams(mAvatarSize, mAvatarSize);
        holder.frameLayout.setLayoutParams(layoutParam);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      if (holder == null) {
        return convertView;
      }

      holder.imgDisplay.setScaleType(ScaleType.CENTER_CROP);
      String token = UserPreferences.getInstance().getToken();
      String imgId = mlistImg.get(position);
      if (imgId != null && imgId.length() > 0) {
        ImageRequest imageRequest = new ImageRequest(token, imgId, ImageRequest.THUMBNAIL);
        getImageFetcher().loadImage(imageRequest, holder.imgDisplay, mAvatarSize);
      }
      return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
      super.notifyDataSetChanged();
      updateTitle(totalImage);
    }
  }

  public class ViewHolder {

    public ImageView imgDisplay;
    public FrameLayout frameLayout;
  }
}