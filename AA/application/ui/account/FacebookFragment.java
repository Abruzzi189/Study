package com.application.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.service.DataFetcherService;
import com.application.ui.BaseFragment;
import com.application.ui.customeview.ItemFriendHolderView;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;


public class FacebookFragment extends BaseFragment {

  private static final String TAG = "FacebookFragment";
  // Suggested by Facebook
  private static final int REAUTH_ACTIVITY_CODE = 100;
  private UiLifecycleHelper lifecycleHelper;
  private ListView mlvFacebookFriends;
  private FriendListAdapter mFriendAdapter;
  private EditText medtSearch;
  private List<GraphUser> friends;
  private List<GraphUser> searchFriends;
  private TextView mtxtNoMore;

  // private static final List<String> PERMISSIONS = Arrays.asList("email",
  // "user_about_me", "user_activities", "user_birthday",
  // "user_education_history", "user_events", "user_hometown",
  // "user_groups", "user_interests", "user_likes", "user_location",
  // "user_photos", "user_work_history");
  private Context mAppContext;
  private TextWatcher searchListener = new TextWatcher() {
    @Override
    public void onTextChanged(CharSequence s, int start, int before,
        int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
      if (medtSearch.getText().toString().replace("\u3000", " ").trim()
          .length() > 0) {
        if (friends != null && friends.size() > 0) {
          if (searchFriends == null) {
            searchFriends = new ArrayList<GraphUser>();
          } else {
            searchFriends.clear();
          }
          for (GraphUser user : friends) {
            if (user.getName()
                .toLowerCase(Locale.US)
                .contains(
                    medtSearch.getText().toString()
                        .toLowerCase(Locale.US))) {
              searchFriends.add(user);
            }
          }
          mFriendAdapter.updateList(searchFriends);
        }
      } else {
        if (friends != null && friends.size() > 0) {
          mFriendAdapter.updateList(friends);
        }
      }
      showNoMoreItem();
    }
  };

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mAppContext = activity.getApplicationContext();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    LogUtils.e("onCreateView", "onCreateView");
    requestDirtyWord();
    View view = inflater.inflate(R.layout.fragment_facebook, container,
        false);
    initialView(view);
    // mtxtNoMore.setText(getResources().getString(R.string.loading));
    mtxtNoMore.setText(R.string.no_more_items_to_show);
    medtSearch.addTextChangedListener(searchListener);
    lifecycleHelper = new UiLifecycleHelper(getActivity(),
        new Session.StatusCallback() {
          @Override
          public void call(Session session, SessionState state,
              Exception exception) {
            if (session != null && state.isOpened()) {
              requestFriendList(session);
            }
          }
        });
    lifecycleHelper.onCreate(savedInstanceState);
    ArrayList<GraphUser> list = new ArrayList<GraphUser>();
    mFriendAdapter = new FriendListAdapter(getActivity(),
        R.layout.item_list_friend_facebook, list);
    mlvFacebookFriends.setAdapter(mFriendAdapter);
    return view;
  }

  /**
   * Notify data service to load list dirty word
   */
  private void requestDirtyWord() {
    Activity activity = getActivity();
    if (activity != null) {
      String token = UserPreferences.getInstance().getToken();
      DataFetcherService.startCheckSticker(activity, token);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REAUTH_ACTIVITY_CODE) {
      lifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  protected boolean isControlNavigation() {
    return false;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    LogUtils.e("onActivityCreated", "onActivityCreated");
    lifecycleHelper = new UiLifecycleHelper(getActivity(),
        new Session.StatusCallback() {
          @Override
          public void call(Session session, SessionState state,
              Exception exception) {
            if (session != null && state.isOpened()) {
              requestFriendList(session);
            }
          }
        });
    lifecycleHelper.onCreate(savedInstanceState);
    ArrayList<GraphUser> list = new ArrayList<GraphUser>();
    mFriendAdapter = new FriendListAdapter(getActivity(),
        R.layout.item_list_friend_facebook, list);
    mlvFacebookFriends.setAdapter(mFriendAdapter);
    medtSearch.setText("");
    ensureOpenSession();
  }

  private void initialView(View view) {
    medtSearch = (EditText) view
        .findViewById(R.id.item_list_addfriend_header_edt);
    mtxtNoMore = (TextView) view
        .findViewById(R.id.fragment_friendlist_txt_NoMoreItem);
    mlvFacebookFriends = (ListView) view
        .findViewById(R.id.fragment_friendlist_list);
  }

  private void showNoMoreItem() {
    mtxtNoMore.setText(R.string.no_more_items_to_show);
    if (mFriendAdapter.getSize() > 0) {
      mlvFacebookFriends.setVisibility(View.VISIBLE);
      mtxtNoMore.setVisibility(View.GONE);
    } else {
      mlvFacebookFriends.setVisibility(View.GONE);
      mtxtNoMore.setVisibility(View.VISIBLE);
    }
  }

  /**
   * check session, if null for login else get friend list
   */
  public void ensureOpenSession() {
    if (mFriendAdapter.getCount() <= 0) {
      // requestFriendList(session);
      Session session = Session.getActiveSession();
      if (session == null || !session.isOpened()) {
        // TODO work for get multiple info
        // session = new Session(getActivity());
        // Session.setActiveSession(session);
        // session.openForRead(new
        // Session.OpenRequest(this).setCallback(
        // new Session.StatusCallback() {
        //
        // @Override
        // public void call(Session session, SessionState state,
        // Exception exception) {
        // if (session != null && state.isOpened()) {
        // requestFriendList(session);
        // }
        // }
        // }).setPermissions(PERMISSIONS));
        // TODO work for base info
        Session.openActiveSession(getActivity(), true,
            new Session.StatusCallback() {
              @Override
              public void call(Session session,
                  SessionState state, Exception exception) {
                if (session != null && state.isOpened()) {
                  requestFriendList(session);
                }
              }
            });
      } else {
        requestFriendList(Session.getActiveSession());
      }
    }
  }

  private void requestFriendList(Session activeSession) {
    mtxtNoMore.setText(getResources().getString(R.string.loading));

    if (activeSession.getState().isOpened()) {
      Request friendRequest = Request.newMyFriendsRequest(activeSession,
          new GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users,
                Response response) {
              if (response.getError() == null) {
                LogUtils.i(TAG, response.toString());
                friends = users;
                mFriendAdapter.updateList(users);
                showNoMoreItem();
              }
            }
          });
      Bundle params = new Bundle();
      params.putString("fields", "id, name, picture");
      friendRequest.setParameters(params);
      friendRequest.executeAsync();
    }
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  // private Request createRequest(String userID, Set<String> extraFields,
  // Session session) {
  // Request request = Request.newGraphPathRequest(session, userID +
  // "/friends", null);
  //
  // Set<String> fields = new HashSet<String>(extraFields);
  // String[] requiredFields = new String[]{
  // "id",
  // "name"
  // };
  // fields.addAll(Arrays.asList(requiredFields));
  //
  // // String pictureField = adapter.getPictureFieldSpecifier();
  // // if (pictureField != null) {
  // // fields.add(pictureField);
  // // }
  //
  // Bundle parameters = request.getParameters();
  // parameters.putString("fields", TextUtils.join(",", fields));
  // request.setParameters(parameters);
  //
  // return request;
  // }
  private class FriendListAdapter extends ArrayAdapter<GraphUser> {

    int mViewWidth = 0;
    private LayoutInflater inflater;
    private List<GraphUser> list;
    private WebDialog dialog = null;
    private ArrayList<String> userPending = new ArrayList<String>();
    View.OnClickListener inviteButtonClick = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        GraphUser user = (GraphUser) v.getTag();
        if (user != null) {
          initialInviteDialog(user.getId(), v);
        }
      }
    };

    public FriendListAdapter(Context context, int textViewResourceId,
        List<GraphUser> objects) {
      super(context, textViewResourceId, objects);
      inflater = (LayoutInflater) getContext().getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      setList(objects);
    }

    public void setList(List<GraphUser> list) {
      for (GraphUser user : list) {
        user = genAvatalink(user);
      }
      this.list = list;
    }

    @Override
    public void add(GraphUser object) {
      object = genAvatalink(object);
      super.add(object);
    }

    private GraphUser genAvatalink(GraphUser object) {
      try {
        JSONObject jsonObject = object.getInnerJSONObject();
        if (jsonObject.has("picture")) {
          JSONObject pictureObject = jsonObject
              .getJSONObject("picture");
          if (pictureObject.has("data")) {
            JSONObject dataObject = pictureObject
                .getJSONObject("data");
            if (dataObject.has("url")) {
              object.setLink(dataObject.getString("url"));
            }
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ItemFriendHolderView holderView = null;
      GraphUser user = getItem(position);
      if (convertView == null) {
        holderView = new ItemFriendHolderView();
        convertView = inflater.inflate(
            R.layout.item_list_friend_facebook, null);
        holderView.ivAvatar = (ImageView) convertView
            .findViewById(R.id.ivAvatar);
        holderView.tvName = (TextView) convertView
            .findViewById(R.id.tvFriendName);
        holderView.btnInvite = (Button) convertView
            .findViewById(R.id.btnInvite);
        convertView.setTag(holderView);

        RelativeLayout container = (RelativeLayout) convertView
            .findViewById(R.id.item_list_friend_facebook_container);
        mViewWidth = container.getWidth()
            - holderView.ivAvatar.getWidth()
            - holderView.ivAvatar.getWidth()
            - holderView.btnInvite.getWidth();
      } else {
        holderView = (ItemFriendHolderView) convertView.getTag();
      }
      String buffer = user.getName();
      if (mViewWidth > 0) {
        buffer = Utility.measureTextSize(holderView.tvName,
            user.getName(), mViewWidth);
      }
      holderView.tvName.setText(buffer);
      holderView.btnInvite.setTag(user);
      holderView.btnInvite.setText(getContext().getResources().getString(
          R.string.add_friends_invite_button_title));
      setTextPending(user.getId(), holderView.btnInvite);
      holderView.btnInvite.setOnClickListener(inviteButtonClick);
      int imageThumbSize = getResources().getDimensionPixelSize(
          R.dimen.image_thumbnail_size);
      getImageFetcher().loadImageWithoutPlaceHolder(user.getLink(),
          holderView.ivAvatar, imageThumbSize);
      return convertView;
    }

    private void setTextPending(String userid, Button btn) {
      for (String value : userPending) {
        if (userid.equalsIgnoreCase(value)) {
          btn.setEnabled(false);
          btn.setText(getResources().getString(
              R.string.add_friends_pending_button_title));
          break;
        } else {
          btn.setEnabled(true);
        }
      }
    }

    private void initialInviteDialog(final String userId, final View view) {
      Bundle params = new Bundle();
      params.putString("message",
          mAppContext.getString(R.string.invite_andg));
      params.putString("to", userId);
      params.putString("data", UserPreferences.getInstance()
          .getInviteUrl());
      dialog = new WebDialog.RequestsDialogBuilder(getActivity(),
          Session.getActiveSession(), params).setOnCompleteListener(
          new WebDialog.OnCompleteListener() {
            @Override
            public void onComplete(Bundle values,
                FacebookException error) {
              if (error != null) {
                if (values != null) {
                  final String requestId = values
                      .getString("request");
                  if (requestId != null) {
                    Toast.makeText(
                        mAppContext
                            .getApplicationContext(),
                        mAppContext
                            .getString(R.string.no_internet),
                        Toast.LENGTH_SHORT).show();
                  } else {
                    Toast.makeText(
                        mAppContext
                            .getApplicationContext(),
                        mAppContext
                            .getString(R.string.request_cancel),
                        Toast.LENGTH_SHORT).show();
                  }
                } else {
                  Toast.makeText(
                      mAppContext.getApplicationContext(),
                      mAppContext
                          .getString(R.string.request_cancel),
                      Toast.LENGTH_SHORT).show();
                }
              } else {
                Set<String> list = values.keySet();
                for (String key : list) {
                  LogUtils.e(TAG, "key=" + key);
                  LogUtils.e(TAG,
                      "values" + values.getString(key));
                }
                final String requestId = values
                    .getString("request");
                if (requestId != null) {
                  userPending.add(userId);
                  Button btnInvite = (Button) view;
                  btnInvite
                      .setText(getResources()
                          .getString(
                              R.string.add_friends_pending_button_title));
                  btnInvite
                      .setBackgroundResource(R.drawable.btn_pending_background);
                  btnInvite.setTextColor(getResources()
                      .getColor(R.color.color_hint_pale));
                  btnInvite.setOnClickListener(null);
                } else {
                  Toast.makeText(mAppContext,
                      "Request cancelled",
                      Toast.LENGTH_SHORT).show();
                }
              }
              dialog = null;
            }
          }).build();
      dialog.show();
    }

    public void updateList(List<GraphUser> list) {
      if (getCount() > 0) {
        clear();
      }
      for (GraphUser graphUser : list) {
        add(graphUser);
      }
      this.notifyDataSetChanged();
    }

    public int getSize() {
      if (this.list == null) {
        return 0;
      }
      return this.list.size();
    }
  }
}
