/**
 * @Name: ContactsListFragment
 * @Description: Class for loading all contacts in Phone Book to add friend.
 * @History: 2013.08.01 TungDX First create
 */
package com.application.ui.account;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.provider.ContactsContract.Data;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.response.InviteFriendResponse;
import com.application.constant.Constants;
import com.application.imageloader.ContactImageLoader;
import com.application.imageloader.Utils;
import com.application.ui.BaseFragment;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.BuildConfig;
import glas.bbsystem.R;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ContactsListFragment extends BaseFragment implements
    LoaderCallbacks<Cursor>, TextWatcher, OnClickListener,
    ResponseReceiver {

  private static final String KEY_CONTACT_ACTUAL_SEND = "contact_actual_send";
  // Id of loader load contact
  private static final int LOADER_ID_CONTACT = 0;
  private static final int LOADER_SEARCH_CONTACT = 2;
  // Id of loader send invite list friends to server
  private static final int LOADER_ID_SEND_INVITE = 1;
  /**
   * Used for logging.
   */
  private static final String TAG = "ContactsListFragment";
  private static final String[] PROJECTION_PHONE = {
      ContactsContract.CommonDataKinds.Phone._ID,
      ContactsContract.CommonDataKinds.Phone.DATA2,
      ContactsContract.CommonDataKinds.Phone.DATA1,
      ContactsContract.CommonDataKinds.Phone.DATA3,
      ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
      Utility.hasHoneycomb() ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
          : Contacts.DISPLAY_NAME,
      Utility.hasHoneycomb() ? ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
          : Contacts._ID};
  private static final String[] PROJECTION_EMAIL = {
      ContactsContract.CommonDataKinds.Email._ID,
      ContactsContract.CommonDataKinds.Email.DATA2,
      ContactsContract.CommonDataKinds.Email.DATA3,
      ContactsContract.CommonDataKinds.Email.ADDRESS,
      ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY,
      Utility.hasHoneycomb() ? ContactsContract.CommonDataKinds.Email.PHOTO_THUMBNAIL_URI
          : Email._ID};
  private final static String SELECTION_PHONE =
      (Utility.hasHoneycomb() ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
          : Contacts.DISPLAY_NAME)
          + "<>''"
          + " AND "
          + ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP + "=1";
  private final static String SELECTION_EMAIL = ContactsContract.CommonDataKinds.Email.ADDRESS
      + "<>''"
      + " AND "
      + Data.MIMETYPE
      + "='"
      + Email.CONTENT_ITEM_TYPE
      + "'";
  /**
   * List contact has a phone
   */
  private static final int TYPE_PHONE = 0;
  /**
   * List contact has an email
   */
  private static final int TYPE_EMAIL = 1;
  private final static String QUERY = "query";
  private final int REQUEST_SEND_MAIL = 100;
  // Perform load image for contact list
  private ContactImageLoader mImageLoader;
  // View in screen
  private ListView mlistContact;
  private EditText medtSearch;
  private ToggleButton mbtnDeselect;
  private Button mbtnAddFriend;
  private TextView mtxtEmptyView;
  private ProgressDialog mProgressDialog;
  @SuppressLint("UseSparseArrays")
  private Map<Integer, String> mContactSelected = new HashMap<Integer, String>();
  @SuppressLint("UseSparseArrays")
  private Map<Integer, String> mAllContact = new HashMap<Integer, String>();
  private AlertDialog confirmDialogSendSMSOrEmail;
  private String[] mContactActualToSend;
  // Adapter contains item in listview
  private ContactsAdapter mContactsAdapter;
  // Key search
  private String mSearchTerm = null;
  /**
   * Determine type of contact display
   */
  private int mTypeContact = TYPE_PHONE;

  /**
   * List contact is selected to invite
   */
  private Context mAppContext;
  /**
   * call back when list people are selected
   */
  private OnFriendSelected onFriendSelected = new OnFriendSelected() {
    @Override
    public void onFriendSelected(Map<Integer, String> friendList) {
      int size = friendList.size();
      String text = "";
      if (size > 1) {
        text = String.format(getString(R.string.add_n_friends), size);
        mbtnAddFriend.setEnabled(true);
      } else if (size == 1) {
        text = getString(R.string.add_1_friend);
        mbtnAddFriend.setEnabled(true);
      } else {
        text = getString(R.string.add_friend);
        mbtnAddFriend.setEnabled(false);
      }
      if (size == mAllContact.size()) {
        mbtnDeselect.setChecked(true);
      } else {
        mbtnDeselect.setChecked(false);
      }
      mbtnAddFriend.setText(text);
    }
  };

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mAppContext = activity.getApplicationContext();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mSearchTerm = savedInstanceState.getString(QUERY);
      mContactActualToSend = savedInstanceState
          .getStringArray(KEY_CONTACT_ACTUAL_SEND);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_contactslist, container,
        false);
    initView(view);
    initImageLoader();
    return view;
  }

  @Override
  protected boolean isControlNavigation() {
    return false;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // Gets a CursorAdapter
    mContactsAdapter = new ContactsAdapter(mAppContext, null,
        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    mContactsAdapter.addOnFriendSelected(onFriendSelected);
    // Sets the adapter for the ListView
    mlistContact.setAdapter(mContactsAdapter);
    // Determine sim state
    if (Utility.isSimReady(mAppContext)) {
      mTypeContact = TYPE_PHONE;
    } else {
      mTypeContact = TYPE_EMAIL;
    }
    getLoaderManager().initLoader(LOADER_ID_CONTACT, null, this);
  }

  /**
   * Initial view params
   *
   * @param view
   *            View is created when fragment is created
   */
  private void initView(View view) {
    mlistContact = (ListView) view
        .findViewById(R.id.fragment_contactlist_list);
    mtxtEmptyView = (TextView) view
        .findViewById(R.id.fragment_contactlist_txt_empty);
    mlistContact.setEmptyView(mtxtEmptyView);
    medtSearch = (EditText) view
        .findViewById(R.id.item_list_addfriend_header_edt);
    medtSearch.addTextChangedListener(this);
    mbtnDeselect = (ToggleButton) view
        .findViewById(R.id.fragment_contactslist_btn_deselect);
    mbtnAddFriend = (Button) view
        .findViewById(R.id.fragment_contactslist_btn_add);
    // Set default value (there are no selected items)
    mbtnAddFriend.setText(getString(R.string.add_friends));
    mbtnAddFriend.setEnabled(false);
    mbtnAddFriend.setOnClickListener(this);
    mbtnDeselect.setOnClickListener(this);
  }

  /**
   * Initital ImageLoader for fragment
   */
  private void initImageLoader() {
    mImageLoader = new ContactImageLoader(mAppContext,
        getListPreferredItemHeight()) {
      @Override
      protected Bitmap processBitmap(Object data) {
        // This gets called in a background thread and passed the data
        // from
        // ImageLoader.loadImage().
        return loadContactPhotoThumbnail((String) data, getImageSize());
      }
    };
    // Set a placeholder loading image for the image loader
    mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);
    // Add a cache to the image loader
    mImageLoader.addImageCache(getActivity().getSupportFragmentManager(),
        0.1f);
  }

  /**
   * Get item height in listview
   *
   * @return Height of item in listview
   */
  private int getListPreferredItemHeight() {
    final TypedValue typedValue = new TypedValue();
    // Resolve list item preferred height theme attribute into typedValue
    mAppContext.getTheme().resolveAttribute(
        android.R.attr.listPreferredItemHeight, typedValue, true);
    // Create a new DisplayMetrics object
    final DisplayMetrics metrics = new android.util.DisplayMetrics();
    // Populate the DisplayMetrics
    getActivity().getWindowManager().getDefaultDisplay()
        .getMetrics(metrics);
    // Return theme value based on DisplayMetrics
    return (int) typedValue.getDimension(metrics);
  }

  /**
   * @param photoData
   * @param imageSize
   *            Desire size of image
   * @return
   */
  private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {
    // Ensures the Fragment is still added to an activity. As this method is
    // called in a
    // background thread, there's the possibility the Fragment is no longer
    // attached and
    // added to an activity. If so, no need to spend resources loading the
    // contact photo.
    if (!isAdded() || getActivity() == null) {
      return null;
    }
    // Instantiates an AssetFileDescriptor. Given a content Uri pointing to
    // an image file, the
    // ContentResolver can return an AssetFileDescriptor for the file.
    AssetFileDescriptor afd = null;
    // This "try" block catches an Exception if the file descriptor returned
    // from the Contacts
    // Provider doesn't point to an existing file.
    try {
      Uri thumbUri;
      // If Android 3.0 or later, converts the Uri passed as a string to a
      // Uri object.
      if (Utils.hasHoneycomb()) {
        thumbUri = Uri.parse(photoData);
      } else {
        // For versions prior to Android 3.0, appends the string
        // argument to the content
        // Uri for the Contacts table.
        final Uri contactUri = Uri.withAppendedPath(
            Contacts.CONTENT_URI, photoData);
        // Appends the content Uri for the Contacts.Photo table to the
        // previously
        // constructed contact Uri to yield a content URI for the
        // thumbnail image
        thumbUri = Uri.withAppendedPath(contactUri,
            Photo.CONTENT_DIRECTORY);
      }
      // Retrieves a file descriptor from the Contacts Provider. To learn
      // more about this
      // feature, read the reference documentation for
      // ContentResolver#openAssetFileDescriptor.
      afd = mAppContext.getContentResolver().openAssetFileDescriptor(
          thumbUri, "r");
      // Gets a FileDescriptor from the AssetFileDescriptor. A
      // BitmapFactory object can
      // decode the contents of a file pointed to by a FileDescriptor into
      // a Bitmap.
      FileDescriptor fileDescriptor = afd.getFileDescriptor();
      if (fileDescriptor != null) {
        // Decodes a Bitmap from the image pointed to by the
        // FileDescriptor, and scales it
        // to the specified width and height
        return ContactImageLoader.decodeSampledBitmapFromDescriptor(
            fileDescriptor, imageSize, imageSize);
      }
    } catch (FileNotFoundException e) {
      // If the file pointed to by the thumbnail URI doesn't exist, or the
      // file can't be
      // opened in "read" mode, ContentResolver.openAssetFileDescriptor
      // throws a
      // FileNotFoundException.
      if (BuildConfig.DEBUG) {
        LogUtils.d(TAG,
            "Contact photo thumbnail not found for contact "
                + photoData + ": " + e.toString());
      }
    } finally {
      // If an AssetFileDescriptor was returned, try to close it
      if (afd != null) {
        try {
          afd.close();
        } catch (IOException e) {
          // Closing a file descriptor might cause an IOException if
          // the file is
          // already closed. Nothing extra is needed to handle this.
        }
      }
    }
    // If the decoding failed, returns null
    return null;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    Uri contentUri;
    String[] projectionHome = null;
    String order;
    String selection;
    if (mSearchTerm == null) {
      if (mTypeContact == TYPE_PHONE) {
        order =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY
                : Contacts.DISPLAY_NAME;
        selection = SELECTION_PHONE;
        projectionHome = PROJECTION_PHONE;
        contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
      } else {
        order = Email.DISPLAY_NAME_PRIMARY + " COLLATE NOCASE ASC";
        selection = SELECTION_EMAIL;
        projectionHome = PROJECTION_EMAIL;

        contentUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
      }
    } else {
      if (mTypeContact == TYPE_PHONE) {
        order =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY
                : Contacts.DISPLAY_NAME;
        selection = SELECTION_PHONE;
        projectionHome = PROJECTION_PHONE;
        contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        return new CursorLoader(mAppContext, contentUri,
            projectionHome,
            order + " Like '%" + mSearchTerm + "%'", null, order
            + " COLLATE NOCASE ASC");
      } else {
        order = Email.DISPLAY_NAME_PRIMARY + " COLLATE NOCASE ASC";
        selection = SELECTION_EMAIL;
        projectionHome = null;
        contentUri = Uri
            .withAppendedPath(
                ContactsContract.CommonDataKinds.Email.CONTENT_FILTER_URI,
                Uri.encode(mSearchTerm));
      }
    }
    LogUtils.i(TAG, "Uri=" + contentUri.toString());
    return new CursorLoader(mAppContext, contentUri, projectionHome,
        selection, null, order);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
    if (cursor == null) {
      return;
    }
    LogUtils.e("cursor", "cursor=" + cursor.getCount());

    int idLoader = arg0.getId();
    int num = cursor.getCount();
    switch (idLoader) {
      case LOADER_ID_CONTACT:

        LogUtils.i(TAG, "Num contacts=" + num);
        if (num <= 0) {
          mtxtEmptyView.setText(R.string.no_more_items_to_show);
        }
        selectAllContact(cursor);
        mContactsAdapter.swapCursor(cursor);
        break;
      case LOADER_SEARCH_CONTACT:
        if (num <= 0) {
          mtxtEmptyView.setText(R.string.no_more_items_to_show);
        }
        mContactsAdapter.swapCursor(cursor);
        break;

      default:
        break;
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursor) {
    mContactsAdapter.swapCursor(null);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (!TextUtils.isEmpty(mSearchTerm)) {
      outState.putString(QUERY, mSearchTerm);
    }
    outState.putStringArray(KEY_CONTACT_ACTUAL_SEND, mContactActualToSend);
  }

  private int getId(Cursor cursor, int type) {
    if (type == TYPE_EMAIL) {
      return cursor
          .getInt(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Email._ID));
    } else {
      return cursor
          .getInt(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
    }
  }

  private String getDisplayName(Cursor cursor, int type) {
    if (type == TYPE_EMAIL) {
      String value = ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY;
      return cursor.getString(cursor.getColumnIndex(value));
    } else {
      return cursor
          .getString(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
    }
  }

  private String getContactDescription(Cursor cursor, int type) {
    int t = 0;
    if (type == TYPE_EMAIL) {
      t = cursor
          .getInt(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
    } else {
      t = cursor
          .getInt(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));
    }

    switch (type) {
      case TYPE_EMAIL:
        break;
      case TYPE_PHONE:

        break;

      default:
        break;
    }
    int typeDescription;
    if (t == TYPE_EMAIL) {
      typeDescription = cursor
          .getInt(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
    } else {
      typeDescription = cursor
          .getInt(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));
    }
    String description;

    if (t == TYPE_EMAIL) {

      if (typeDescription == Email.TYPE_CUSTOM) {
        description = cursor
            .getString(cursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA3));
      } else {
        description = ContactsContract.CommonDataKinds.Email
            .getTypeLabel(getResources(), t, "Other") + "";
      }

    } else {
      if (typeDescription == Email.TYPE_CUSTOM) {
        description = cursor
            .getString(cursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA3));
      } else {
        description = ContactsContract.CommonDataKinds.Phone
            .getTypeLabel(getResources(), t, "Other") + "";
      }
    }
    return description;
  }

  private String getValueContact(Cursor cursor, int typeContact) {
    if (typeContact == TYPE_EMAIL) {
      return cursor
          .getString(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
    } else {
      return cursor
          .getString(cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    }
  }

  private void selectAllContact(Cursor cursor) {
    if (cursor == null) {
      return;
    }
    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      int id = getId(cursor, mTypeContact);
      String value = getValueContact(cursor, mTypeContact);
      mContactSelected.put(id, value);
      mAllContact.put(id, value);

    }
    onFriendSelected.onFriendSelected(mContactSelected);
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count,
      int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    if (s.length() == 0) {
      mSearchTerm = null;
    } else {
      mSearchTerm = s + "";
    }
  }

  // public class ContactAdapter extends BaseAdapter {
  // private OnFriendSelected mOnFriendSelected;
  //
  // public void addOnFriendSelected(OnFriendSelected onFriendSelected) {
  // mOnFriendSelected = onFriendSelected;
  // }
  //
  // private Map<Integer, String> mContactList = new HashMap<Integer,
  // String>();
  //
  //
  // @Override
  // public int getCount() {
  // return mContactList.size();
  // }
  //
  // @Override
  // public Object getItem(int position) {
  // return mContactList.;
  // }
  //
  // @Override
  // public long getItemId(int position) {
  // return 0;
  // }
  //
  // @Override
  // public View getView(int position, View convertView, ViewGroup parent) {
  // return null;
  // }
  //
  // private class ViewHolder {
  // CheckBox checkBox;
  // QuickContactBadge quickContactBadge;
  // TextView txtName;
  // TextView txtAccountType;
  // }
  //
  // }

  @Override
  public void afterTextChanged(Editable s) {
    getLoaderManager().restartLoader(LOADER_SEARCH_CONTACT, null, this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.fragment_contactslist_btn_add:
        addFriendsHandle();
        break;
      case R.id.fragment_contactslist_btn_deselect:
        ToggleButton toggleButton = (ToggleButton) v;
        if (toggleButton.isChecked()) {
          mContactSelected.clear();
          mContactSelected.putAll(mAllContact);
          mContactsAdapter.notifyDataSetChanged();
          onFriendSelected.onFriendSelected(mContactSelected);
        } else {
          mContactSelected.clear();
          mContactsAdapter.notifyDataSetChanged();
          onFriendSelected.onFriendSelected(mContactSelected);
        }
        break;
      default:
        break;
    }
  }

  private void addFriendsHandle() {
    // Calculate number of invited friends and the received total points
    // if all of them connect to andG.
    int friends = 0;
    int points = 0;

    if (mContactSelected != null && mContactSelected.size() > 0) {
      String[] peoples = new String[mContactSelected.size()];
      for (Map.Entry<Integer, String> entry : mContactSelected.entrySet()) {
        peoples[friends] = entry.getValue();
        friends++;
      }
    }

    friends = mContactSelected.size();
    points = friends * Preferences.getInstance().getInviteFriendPoints();
    String title = getString(R.string.add_friends);
    if (mTypeContact == TYPE_PHONE) {
      String message = String.format(
          getString(R.string.add_friends_sms_msg), friends, points);
      confirmDialogSendSMSOrEmail = new CustomConfirmDialog(getActivity(), title, message, true)
          .setPositiveButton(0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              onYesClick();
            }
          })
          .create();
    } else {
      String message = String.format(
          getString(R.string.add_friends_email_msg), friends, points);
      confirmDialogSendSMSOrEmail = new CustomConfirmDialog(getActivity(), title, message, true)
          .setPositiveButton(0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              onYesClick();
            }
          }).create();
    }
    confirmDialogSendSMSOrEmail.show();
    int dividerId = confirmDialogSendSMSOrEmail.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = confirmDialogSendSMSOrEmail.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          confirmDialogSendSMSOrEmail.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void sendSMS(String[] phones) {
    SmsManager smsManager = SmsManager.getDefault();
    String invitedURL = UserPreferences.getInstance().getInviteUrl();

    String smsContent = String.format(
        getString(R.string.add_friends_sms_content), invitedURL);
    try {
      if (smsManager != null) {
        for (int i = 0; i < phones.length; i++) {
          smsManager.sendTextMessage(phones[i], null, smsContent,
              null, null);
          LogUtils.d(TAG, "Send[" + i + "]=" + phones[i]);
        }
      }
    } catch (IllegalArgumentException iea) {
      LogUtils.e(TAG, getString(R.string.add_friends_sms_error));
    }
  }

  public void sendEmail(String[] emails) {
    final Intent emailIntent = new Intent(
        android.content.Intent.ACTION_SEND);
    String invitedURL = UserPreferences.getInstance().getInviteUrl();
    String emailSubject = getString(R.string.add_friends_email_subject);
    String emailContent = String.format(
        getString(R.string.add_friends_email_content), invitedURL);
    emailIntent.setType("plain/text");
    // emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);
    emailIntent.putExtra(android.content.Intent.EXTRA_BCC, emails);
    emailIntent
        .putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
    // startActivityForResult(emailIntent, REQUEST_SEND_MAIL);
    Intent chooserIntent = Intent.createChooser(emailIntent,
        mAppContext.getString(R.string.send_mail_invite_friends));
    if (chooserIntent != null) {
      // startActivity(chooserIntent);
      chooserIntent.putExtra(Constants.EXTRA_SEND_MAIL_TO, emails);
      startActivityForResult(chooserIntent, REQUEST_SEND_MAIL);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void startRequest(int loaderId) {
    if (loaderId == LOADER_ID_SEND_INVITE) {
      mProgressDialog = ProgressDialog.show(getActivity(), "",
          getString(R.string.sending), true, false);
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    if (response.getCode() != Response.SERVER_SUCCESS
        && response.getCode() != Response.SERVER_NOT_ENOUGHT_MONEY) {
      mProgressDialog.dismiss();
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
      getLoaderManager().destroyLoader(loader.getId());
      return;
    }
    if (loader.getId() == LOADER_ID_SEND_INVITE) {
      Toast.makeText(mAppContext, R.string.message_is_sent,
          Toast.LENGTH_LONG).show();

      // Notify to AddFriendsActivity for navigating to next screen
      // (Meet People or Connections)
      final AddFriendsActivity addFriendsActivity = (AddFriendsActivity) getActivity();
      if (addFriendsActivity != null) {
        // do this because some time request server very fast caused by
        // UI
        // splash
        new Handler().postDelayed(new Runnable() {

          @Override
          public void run() {
            mProgressDialog.dismiss();
            addFriendsActivity.onSendCompleted();
          }
        }, 1000);

      }
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_ID_SEND_INVITE) {
      return new InviteFriendResponse(data);
    }
    return null;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  public void onYesClick() {

    // if not select all, send selected contacts
    if (mContactSelected == null || mContactSelected.size() == 0) {
      return;
    }

    // Get list people to send invitation.
    mContactActualToSend = new String[mContactSelected.size()];
    int i = 0;
    for (Map.Entry<Integer, String> entry : mContactSelected.entrySet()) {
      mContactActualToSend[i] = entry.getValue();
      i++;
    }

    if (mTypeContact == TYPE_PHONE) {
      sendSMS(mContactActualToSend);
    } else {
      sendEmail(mContactActualToSend);
    }
  }

  /**
   * tungdx Trigger when click checkbox in listview
   */
  private interface OnFriendSelected {

    public void onFriendSelected(Map<Integer, String> friendList);
  }

  @SuppressLint("UseSparseArrays")
  private class ContactsAdapter extends CursorAdapter {

    private OnFriendSelected mOnFriendSelected;

    public ContactsAdapter(Context context, Cursor c, int flags) {
      super(context, c, flags);
    }

    public void addOnFriendSelected(OnFriendSelected onFriendSelected) {
      mOnFriendSelected = onFriendSelected;
    }

    @Override
    public void bindView(View arg0, Context context, Cursor cursor) {
      final ViewHolder holder = (ViewHolder) arg0.getTag();
      final int id = getId(cursor, mTypeContact);
      String name = getDisplayName(cursor, mTypeContact);
      String type = getContactDescription(cursor, mTypeContact);

      LogUtils.i(TAG, "Contact type=" + type);
      holder.txtAccountType.setText(type);
      final String value = getValueContact(cursor, mTypeContact);
      LogUtils.i(TAG, "Contact value=" + value);

      holder.txtName.setText(name);

      final String photoUri = cursor
          .getString(Utility.hasHoneycomb() ? cursor
              .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
              : cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
      LogUtils.i(TAG, "photoUri=" + photoUri);
      mImageLoader.loadImage(photoUri, holder.quickContactBadge);
      holder.checkBox.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean check = ((CheckBox) v).isChecked();
          if (check) {
            mContactSelected.put(id, value);
          } else {
            mContactSelected.remove(id);
          }
          mOnFriendSelected.onFriendSelected(mContactSelected);
          notifyDataSetChanged();
        }
      });
      if (mContactSelected.containsKey(id)) {
        holder.checkBox.setChecked(true);
      } else {
        holder.checkBox.setChecked(false);
      }
    }

    @Override
    public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
      final ViewHolder viewHolder = new ViewHolder();
      View view = View.inflate(arg0, R.layout.item_list_contact, null);
      viewHolder.checkBox = (CheckBox) view
          .findViewById(R.id.item_list_contact_chb);
      viewHolder.quickContactBadge = (QuickContactBadge) view
          .findViewById(R.id.item_list_contact_quickcontactbadge);
      viewHolder.txtName = (TextView) view
          .findViewById(R.id.item_list_contact_name);
      viewHolder.txtAccountType = (TextView) view
          .findViewById(R.id.item_list_contact_des);
      viewHolder.quickContactBadge.setEnabled(false);
      view.setTag(viewHolder);
      return view;
    }

    private class ViewHolder {

      CheckBox checkBox;
      QuickContactBadge quickContactBadge;
      TextView txtName;
      TextView txtAccountType;
    }
  }
}
