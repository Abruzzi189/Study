package com.application.chat;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableRow;
import com.application.util.Emoji;
import com.application.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class EmojiPanel implements OnClickListener {

  private final String TAG = "EmojiPanel";

  private Context mContext;

  private ViewPager mViewPager;
  private CirclePageIndicator mCirclePageIndicator;
  private TableRow mTbrEmojiBar;
  private TableRow mTbrStickerBar;
  private LinearLayout mLinearLayoutSticker;
  private Button mbtnEmoji;
  private Button mbtnSticker;
  private ImageView mbtnBack;
  private FrameLayout loadingLayout;

  private EmojiPagerAdapter mMediaPagerAdapter;
  private EmojiPagerAdapter mEmojiPagerAdapter;
  private IOnEmojiSelected mOnEmojiSelected;
  private List<ImageView> mlistSticker = new ArrayList<ImageView>();
  private boolean isOpenEmojiPanel;
  private String currentStickerCate;
  private boolean isLoadingSticker = true;

  public EmojiPanel(Context context, IOnEmojiSelected listener,
      ViewPager viewPager, CirclePageIndicator indicator,
      Button btnEmoji, Button btnSticker, ImageView btnBack,
      TableRow stickerBar, TableRow emojiBar, LinearLayout lnlSticker,
      FrameLayout loadingLayout) {
    super();
    this.mContext = context;

    this.mViewPager = viewPager;
    this.mCirclePageIndicator = indicator;
    this.mTbrEmojiBar = emojiBar;
    this.mTbrStickerBar = stickerBar;
    this.mLinearLayoutSticker = lnlSticker;
    this.mbtnEmoji = btnEmoji;
    this.mbtnEmoji.setOnClickListener(this);
    this.mbtnSticker = btnSticker;
    this.mbtnSticker.setOnClickListener(this);
    this.mbtnBack = btnBack;
    this.mbtnBack.setOnClickListener(this);
    this.loadingLayout = loadingLayout;

    this.mOnEmojiSelected = listener;

    // Initial current sticker choose
    this.isOpenEmojiPanel = true;
    this.currentStickerCate = "";
  }

  public void setIsLoadingSticker(boolean isLoading) {
    this.isLoadingSticker = isLoading;
    if (!isLoadingSticker && loadingLayout.getVisibility() != View.GONE) {
      loadingLayout.setVisibility(View.INVISIBLE);
    }
  }

  public void onPanelShowed() {
    if (isOpenEmojiPanel) {
      showEmojiPanel();
    }
    initPagerSticker();
  }

  private void showStickerPanel() {
    isOpenEmojiPanel = false;
    mTbrEmojiBar.setVisibility(View.GONE);
    mTbrStickerBar.setVisibility(View.VISIBLE);
    if (mlistSticker.size() > 0) {
      if (currentStickerCate == null || currentStickerCate.length() == 0) {
        mlistSticker.get(0).performClick();
      } else {
        showStickerCategory(currentStickerCate);
      }
      loadingLayout.setVisibility(View.GONE);
      mViewPager.setVisibility(View.VISIBLE);
      mCirclePageIndicator.setVisibility(View.VISIBLE);
    } else {
      // Show loading when list item
      if (isLoadingSticker) {
        loadingLayout.setVisibility(View.VISIBLE);
      } else {
        loadingLayout.setVisibility(View.INVISIBLE);
      }
      mViewPager.setVisibility(View.GONE);
      mCirclePageIndicator.setVisibility(View.GONE);
    }
  }

  private void showStickerCategory(String code) {
    currentStickerCate = code;
    // Get list item sticker
    List<Media> itemMedias = ChatUtils.getItemMediaSticker(mContext, code);
    if (itemMedias != null) {
      mMediaPagerAdapter = new EmojiPagerAdapter(mContext, itemMedias,
          mOnEmojiSelected);

      // Show data view
      mViewPager.setAdapter(mMediaPagerAdapter);
      mCirclePageIndicator.setViewPager(mViewPager);
      mCirclePageIndicator.invalidate();
      mViewPager.getAdapter().notifyDataSetChanged();
    } else {
      if (mlistSticker.size() > 0) {
        mlistSticker.get(0).performClick();
      } else {
        showEmojiPanel();
      }
    }
  }

  private void initPagerSticker() {
    // Clear current sticker categories
    if (!mlistSticker.isEmpty()) {
      mlistSticker.clear();
    }
    if (mLinearLayoutSticker.getChildCount() > 0) {
      mLinearLayoutSticker.removeAllViews();
    }

    List<String> stickerCategories = ChatUtils
        .getListFolderSticker(mContext);
    for (String code : stickerCategories) {
      List<Media> itemMedias = ChatUtils.getItemMediaSticker(mContext,
          code);
      if (itemMedias == null || itemMedias.size() == 0) {
        continue;
      }

      // Get resource
      Resources res = mContext.getResources();
      ImageView img = new ImageView(mContext);

      // Get image sticker header size and setting view
      int imgSize = res
          .getDimensionPixelSize(R.dimen.standard_horizontal_item_height);
      LayoutParams layoutParams = new LayoutParams(imgSize, imgSize);
      img.setBackgroundResource(R.drawable.btn_sticker_categories);
      img.setAdjustViewBounds(true);
      img.setLayoutParams(layoutParams);
      int imgPadding = res
          .getDimensionPixelSize(R.dimen.icon_category_sticker_padding);
      img.setPadding(imgPadding, imgPadding, 0, imgPadding);

      Uri imgURI = ChatUtils.getThumbnailStickerFolder(mContext, code);
      LogUtils.i(TAG, "Thumb URI:" + imgURI);
      img.setImageURI(imgURI);
      img.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          // Get image code
          String code = String.valueOf(v.getTag());
          if (currentStickerCate == null
              || !currentStickerCate.equals(code)) {
            showStickerCategory(code);
          }

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            for (ImageView imgView : mlistSticker) {
              imgView.setAlpha(1f);
            }
            v.setAlpha(0.5f);
          }
        }
      });
      img.setTag(code);
      mlistSticker.add(img);
      mLinearLayoutSticker.addView(img);
    }

    if (isOpenEmojiPanel) {
      return;
    }

    if (mlistSticker.size() > 0) {
      loadingLayout.setVisibility(View.GONE);
      mViewPager.setVisibility(View.VISIBLE);
      mCirclePageIndicator.setVisibility(View.VISIBLE);

      // Show data after initial
      boolean isContain = false;
      if (currentStickerCate != null && currentStickerCate.length() > 0) {
        for (String code : stickerCategories) {
          if (currentStickerCate.equals(code)) {
            showStickerCategory(code);
            isContain = true;
            break;
          }
        }
      }

      if (!isContain) {
        mlistSticker.get(0).performClick();
      }
    } else {
      showEmojiPanel();
    }
  }

  private void showEmojiPanel() {
    isOpenEmojiPanel = true;
    mTbrStickerBar.setVisibility(View.GONE);
    mTbrEmojiBar.setVisibility(View.VISIBLE);

    // Notify emoji view visible
    mViewPager.setVisibility(View.VISIBLE);
    mCirclePageIndicator.setVisibility(View.VISIBLE);
    initPagerEmoji();

    // Show data after initial
    mViewPager.setAdapter(mEmojiPagerAdapter);
    mCirclePageIndicator.setViewPager(mViewPager);
    mCirclePageIndicator.invalidate();

    // Hide sticker view
    loadingLayout.setVisibility(View.GONE);
    mOnEmojiSelected.onStickerPanelHide();
  }

  private void initPagerEmoji() {
    if (mEmojiPagerAdapter == null) {
      List<Media> mItemMedias = ChatUtils.getItemMediaEmoji();
      mEmojiPagerAdapter = new EmojiPagerAdapter(mContext, mItemMedias,
          mOnEmojiSelected);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.item_fragment_chat_media_btn_sticker:
        showStickerPanel();
        break;
      case R.id.item_fragment_chat_media_btn_emoji:
      case R.id.item_fragment_chat_media_btn_back:
        showEmojiPanel();
        break;
    }
  }

  enum MEDIA_TYPE {
    EMOJI, STICKER
  }

  public interface IOnEmojiSelected {

    public void onEmojiSelected(int emoji, String content);

    public void onStickerSelected(Uri sticker, String content);

    public void onStickerPanelHide();
  }

  public static class Media {

    public MEDIA_TYPE type;
    public List<Uri> stickers;
    public List<Emoji> emojis;
  }

  /**
   * Emoji pager adapter manage emoji pager view
   */
  public class EmojiPagerAdapter extends PagerAdapter implements
      OnItemClickListener {

    private List<Media> mItems;
    private Context mContext;
    private IOnEmojiSelected mOnEmojiSelected;

    public EmojiPagerAdapter(Context context, List<Media> items,
        IOnEmojiSelected listener) {
      mItems = items;
      mContext = context;
      mOnEmojiSelected = listener;
    }

    @Override
    public int getCount() {
      if (mItems == null) {
        return 0;
      } else {
        return mItems.size();
      }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == (GridView) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      // Create view inflater
      View view = View.inflate(mContext, R.layout.item_pager_media, null);

      // Item data
      Media item = mItems.get(position);

      // Create grid view
      final GridView grid = (GridView) view
          .findViewById(R.id.item_pager_media_grid);
      final EmojiAdapter adapter = new EmojiAdapter(mContext, item, grid);
      grid.setAdapter(adapter);

      switch (item.type) {
        case EMOJI:
          grid.getViewTreeObserver().addOnGlobalLayoutListener(
              new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                  int gridHeight = grid.getHeight();
                  if (gridHeight == 0) {
                    return;
                  }
                  int rowNum = ChatUtils.NUM_ITEM_EMOJI_ON_PAGE
                      / ChatUtils.NUM_COLUM_EMOJI_ON_PAGE;
                  int itemHeight = gridHeight / rowNum;
                  adapter.setItemHeight(itemHeight);
                  ViewTreeObserver treeObserver = grid
                      .getViewTreeObserver();
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    treeObserver
                        .removeOnGlobalLayoutListener(this);
                  } else {
                    treeObserver
                        .removeGlobalOnLayoutListener(this);
                  }
                }
              });
          grid.setNumColumns(ChatUtils.NUM_COLUM_EMOJI_ON_PAGE);
          break;
        case STICKER:
          grid.getViewTreeObserver().addOnGlobalLayoutListener(
              new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                  int gridHeight = grid.getHeight();
                  if (gridHeight == 0) {
                    return;
                  }

                  int rowNum = ChatUtils.NUM_ITEM_STICKER_ON_PAGE
                      / ChatUtils.NUM_COLUM_STICKER_ON_PAGE;
                  int itemHeight = gridHeight / rowNum;
                  adapter.setItemHeight(itemHeight);
                  ViewTreeObserver treeObserver = grid
                      .getViewTreeObserver();
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    treeObserver
                        .removeOnGlobalLayoutListener(this);
                  } else {
                    treeObserver
                        .removeGlobalOnLayoutListener(this);
                  }
                }
              });
          grid.setNumColumns(ChatUtils.NUM_COLUM_STICKER_ON_PAGE);
          break;
      }

      grid.setOnItemClickListener(this);
      ((ViewPager) container).addView(view);
      return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      if (container instanceof ViewPager && object instanceof View) {
        ((ViewPager) container).removeView((View) object);
      }
    }

    @Override
    public void onItemClick(AdapterView<?> arg1, View arg2, int arg3,
        long arg4) {
      EmojiAdapter adapter = (EmojiAdapter) arg1.getAdapter();
      MEDIA_TYPE type = adapter.getType();
      switch (type) {
        case EMOJI:
          Emoji emoji = (Emoji) adapter.getItem(arg3);
          int resource = emoji.getResource();
          String code = emoji.getCode();
          mOnEmojiSelected.onEmojiSelected(resource, code);
          break;
        case STICKER:
          Uri uri = (Uri) adapter.getItem(arg3);
          String content = ChatUtils.generateMessageSticker(uri);
          mOnEmojiSelected.onStickerSelected(uri, content);
          break;
      }
    }
  }

  /**
   * Grid view adapter
   */
  public class EmojiAdapter extends BaseAdapter {

    private GridView mParentView;
    private Media mItem;
    private Context mContext;
    private int mItemHeight;
    private GridView.LayoutParams mLayoutParams;

    public EmojiAdapter(Context context, Media item, GridView parent) {
      mContext = context;
      mItem = item;
      mParentView = parent;
    }

    public void setItemHeight(int height) {
      if (height == mItemHeight) {
        return;
      }
      mItemHeight = height;
      mLayoutParams = new GridView.LayoutParams(
          LayoutParams.MATCH_PARENT, mItemHeight);
      notifyDataSetChanged();
    }

    @Override
    public int getCount() {
      switch (mItem.type) {
        case EMOJI:
          return mItem.emojis.size();
        case STICKER:
          return mItem.stickers.size();
        default:
          return 0;
      }
    }

    @Override
    public Object getItem(int position) {
      switch (mItem.type) {
        case EMOJI:
          return mItem.emojis.get(position);
        case STICKER:
          return mItem.stickers.get(position);
        default:
          throw new IllegalArgumentException(
              "Type of ItemMedia invalid");
      }
    }

    public MEDIA_TYPE getType() {
      return mItem.type;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(final int position, View convertView,
        ViewGroup parent) {
      // Initial view holder
      ViewHolder holder = null;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = View.inflate(mContext, R.layout.item_grid_media,
            null);
        holder.img = (ImageView) convertView
            .findViewById(R.id.item_grid_media_img);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      switch (mItem.type) {
        case EMOJI:
          Emoji emoji = mItem.emojis.get(position);
          holder.img.setImageResource(emoji.getResource());
          break;
        case STICKER:
          if (mLayoutParams != null) {
            holder.img.setLayoutParams(mLayoutParams);
          }
          int size = mContext.getResources().getDimensionPixelSize(
              R.dimen.item_grid_media_ticker_chat_size_pixel);
          Uri uri = mItem.stickers.get(position);
          Picasso.with(mContext).load(uri).resize(size, size)
              .centerInside().into(holder.img);
          break;
      }

      // Set on click listener
      holder.img.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mParentView.getOnItemClickListener().onItemClick(
              mParentView, v, position, getItemId(position));
        }
      });

      return convertView;
    }

    private class ViewHolder {

      public ImageView img;
    }
  }
}