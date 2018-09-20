package com.application.actionbar;

import android.support.v4.app.Fragment;
import android.view.View;
import com.application.constant.UserSetting;
import com.application.ui.RegionSearchSettingFragment;
import com.application.ui.account.EditProfileFragment;
import com.application.ui.account.ProfileTextFragment;
import com.application.ui.account.ThreeSizesFragment;
import com.application.ui.region.ChooseRegionFragment;
import com.application.ui.region.RegionSettingFragment;
import glas.bbsystem.R;


public class ProfileRegisterActionBar extends NativeActionBar {

  @Override
  protected int findResourceIdForActionbar(Fragment activePage) {
    int resId = super.findResourceIdForActionbar(activePage);
    if (activePage instanceof EditProfileFragment) {
      resId = R.layout.cv_native_actionbar_button_right;
    }
    return resId;
  }

  @Override
  protected void setupImgLeft() {
    View.OnClickListener clickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Fragment activePage = mNavigationManager.getActivePage();
        if (mNavigationManager.isBackStackEmpty()) {
          if (activePage instanceof EditProfileFragment) {
            ((EditProfileFragment) activePage).backToFirstScreen();
          }
        } else {
          mNavigationManager.goBack();
        }
      }
    };

    mImgLeft.setOnClickListener(clickListener);
  }

  @Override
  protected void setupRightButton() {
    View.OnClickListener clickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Fragment activePage = mNavigationManager.getActivePage();
        if (activePage instanceof EditProfileFragment) {
          ((EditProfileFragment) activePage).editProfile();
        } else if (activePage instanceof RegionSearchSettingFragment) {
          ((RegionSearchSettingFragment) activePage).onSave(v);
        } else if (activePage instanceof ThreeSizesFragment) {
          ((ThreeSizesFragment) activePage).onSave();
        } else if (activePage instanceof ProfileTextFragment) {
          ((ProfileTextFragment) activePage).onSave();
        } else if (activePage instanceof RegionSettingFragment) {
          ((RegionSettingFragment) activePage).onSave();
        } else if (activePage instanceof ChooseRegionFragment) {
          ((ChooseRegionFragment) activePage).onSave();
        }
      }
    };
    mBtnRight.setOnClickListener(clickListener);
  }

  @Override
  protected void syncImgLeft(Fragment activePage) {
    if (activePage instanceof EditProfileFragment) {
      mImgLeft.setImageResource(R.drawable.ic_action_navigation_arrow_back);
      mImgLeft.setVisibility(View.VISIBLE);
    } else {
      super.syncImgLeft(activePage);
    }
  }

  @Override
  protected void syncRightButton(Fragment activePage) {
    if (activePage instanceof EditProfileFragment) {
      mBtnRight.setText(R.string.common_done);
    } else if (activePage instanceof ThreeSizesFragment) {
      mBtnRight.setText(R.string.common_save);
    } else if (activePage instanceof ProfileTextFragment) {
      mBtnRight.setText(R.string.common_save);
    } else {
      super.syncRightButton(activePage);
    }
  }

  @Override
  protected void syncCenterTextview(Fragment activePage) {
    if (activePage instanceof EditProfileFragment) {
      mTxtCenter.setText(R.string.title_register1);
    } else if (activePage instanceof ThreeSizesFragment) {
      mTxtCenter.setText(R.string.profile_reg_three_sizes);
    } else if (activePage instanceof ProfileTextFragment) {
      int field = ((ProfileTextFragment) activePage).getField();

      switch (field) {
        case ProfileTextFragment.FIELD_FETISH:
          mTxtCenter.setText(R.string.profile_reg_fetish);
          break;

        case ProfileTextFragment.FIELD_HOBBY:
          mTxtCenter.setText(R.string.profile_reg_hobby);
          break;

        case ProfileTextFragment.FIELD_MESSAGE:
          int gender = ((ProfileTextFragment) activePage).getGender();
          if (gender == UserSetting.GENDER_FEMALE) {
            mTxtCenter.setText(R.string.profile_reg_message_female);
          } else {
            mTxtCenter.setText(R.string.profile_reg_message_male);
          }
          break;

        case ProfileTextFragment.FIELD_TYPE_MAN:
          mTxtCenter.setText(R.string.profile_reg_type_man);
          break;

        default:
          break;
      }
    } else {
      super.syncCenterTextview(activePage);
    }
  }

}
