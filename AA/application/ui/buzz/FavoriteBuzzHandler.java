package com.application.ui.buzz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import com.application.connection.Response;
import com.application.connection.request.AddFavoriteRequest;
import com.application.connection.request.RemoveFavoriteRequest;
import com.application.connection.response.AddFavoriteResponse;
import com.application.connection.response.RemoveFavoriteResponse;
import com.application.entity.BuzzListItem;
import com.application.ui.BaseFragment;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class FavoriteBuzzHandler {

  public final int LOADER_ID_ADD_TO_FAVORITES = 1567;
  public final int LOADER_ID_REMOVE_FROM_FAVORITES = 1568;

  private Context context;
  private BaseFragment fragment;

  private boolean isAddingToFavorites = false;
  private int positionAddingToFavorite = -1;
  private boolean isRemovingFromFavorites = false;
  private int positionRemovingFromFavorites = -1;

  private OnHandleFavoriteResult handleFavoriteListener;
  private OnAccessListBuzz accessListBuzzListener;

  public FavoriteBuzzHandler(BaseFragment fragment,
      OnHandleFavoriteResult handleFavoriteListener,
      OnAccessListBuzz accessListBuzzListener) {
    this.fragment = fragment;
    this.context = fragment.getActivity();
    this.handleFavoriteListener = handleFavoriteListener;
    this.accessListBuzzListener = accessListBuzzListener;
  }

  public void handleFavoriteAtPosition(int position, String userId) {
    FavouritedPrefers favouritedPrefers = FavouritedPrefers.getInstance();
    if (favouritedPrefers.hasContainFav(userId)) {
      executeRemoveFromFavorites(position, userId);
    } else {
      executeAddToFavorites(position, userId);
    }
  }

  private void executeAddToFavorites(int position, String userId) {
    if (!isAddingToFavorites) {
      isAddingToFavorites = true;
      positionAddingToFavorite = position;

      String token = UserPreferences.getInstance().getToken();
      AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(
          token, userId);

      if (handleFavoriteListener != null) {
        handleFavoriteListener.startAddFavorite(addFavoriteRequest);
      }
    }
  }

  private void executeRemoveFromFavorites(int position, String userId) {
    if (!isRemovingFromFavorites) {
      isRemovingFromFavorites = true;
      positionRemovingFromFavorites = position;

      String token = UserPreferences.getInstance().getToken();
      RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(
          token, userId);
      if (handleFavoriteListener != null) {
        handleFavoriteListener
            .startRemoveFavorite(removeFavoriteRequest);
      }
    }
  }

  public void handleAddFavoriteResponse(AddFavoriteResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS
        && isAddingToFavorites) {
      fragment.getLoaderManager().destroyLoader(
          LOADER_ID_ADD_TO_FAVORITES);
      if (positionAddingToFavorite > -1) {
        BuzzListItem addingItem = accessListBuzzListener
            .getBuzzAtPosition(positionAddingToFavorite);
        if (addingItem != null) {
          String userId = addingItem.getUserId();
          FavouritedPrefers.getInstance().saveFav(userId);
          UserPreferences.getInstance().increaseFavorite();
          handleFavoriteListener.addFavoriteSuccess(addingItem);
          showDialogAddFavoriteSuccess(addingItem);
          positionAddingToFavorite = -1;
        }
      }
    }
    isAddingToFavorites = false;
  }

  public void handleRemoveFavoriteResponse(RemoveFavoriteResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS
        && isRemovingFromFavorites) {
      fragment.getLoaderManager().destroyLoader(
          LOADER_ID_REMOVE_FROM_FAVORITES);

      if (positionRemovingFromFavorites > -1) {
        BuzzListItem removingItem = accessListBuzzListener
            .getBuzzAtPosition(positionRemovingFromFavorites);
        if (removingItem != null) {
          String userId = removingItem.getUserId();
          FavouritedPrefers.getInstance().removeFav(userId);
          UserPreferences.getInstance().decreaseFavorite();
          if (handleFavoriteListener != null) {
            handleFavoriteListener
                .removeFavoriteSuccess(removingItem);
          }
          showDialogRemoveFavoritesSuccess(removingItem);
          positionRemovingFromFavorites = -1;
        }
      }
    }
    isRemovingFromFavorites = false;
  }

  private void showDialogAddFavoriteSuccess(final BuzzListItem addingItem) {
    String username = addingItem.getUserName();
    String message = String.format(
        context.getString(R.string.profile_add_to_favorites_message),
        username);
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        fragment.getActivity(),
        context.getString(R.string.profile_add_to_favorites_title),
        message, true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (handleFavoriteListener != null) {
              handleFavoriteListener.sendGift(addingItem);
            }
          }
        })
        .setNegativeButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();

    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void showDialogRemoveFavoritesSuccess(
      final BuzzListItem removingItem) {
    String title = context
        .getString(R.string.profile_remove_from_favorites_title);
    String msg = String.format(context
            .getString(R.string.profile_remove_from_favorites_message),
        removingItem.getUserName());
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        fragment.getActivity(), title, msg, false)
        .setPositiveButton(0, null)
        .create();

    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public interface OnHandleFavoriteResult {

    public void startAddFavorite(AddFavoriteRequest addFavoriteRequest);

    public void startRemoveFavorite(
        RemoveFavoriteRequest removeFavoriteRequest);

    public void addFavoriteSuccess(BuzzListItem item);

    public void removeFavoriteSuccess(BuzzListItem item);

    public void sendGift(BuzzListItem item);
  }

  public interface OnAccessListBuzz {

    public BuzzListItem getBuzzAtPosition(int position);

    public int getNumberBuzz();
  }
}
