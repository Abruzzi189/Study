package com.application.ui.customeview;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;
import com.application.util.LogUtils;
import java.util.ArrayList;
import java.util.List;

public class EllipsizingTextView extends TextView {

  private static final String TAG = "Ellipsizing TextView";

  private static final String ELLIPSIS = "...";
  private final List<EllipsizeListener> ellipsizeListeners = new ArrayList<EllipsizeListener>();
  private boolean isEllipsized;
  private boolean isStale;
  private boolean programmaticChange;
  private String fullText;
  private int maxLines = -1;
  private float lineSpacingMultiplier = 1.0f;
  private float lineAdditionalVerticalPadding = 0.0f;
  public EllipsizingTextView(Context context) {
    super(context);
  }

  public EllipsizingTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public EllipsizingTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void addEllipsizeListener(EllipsizeListener listener) {
    if (listener == null) {
      throw new NullPointerException();
    }
    ellipsizeListeners.add(listener);
  }

  public void removeEllipsizeListener(EllipsizeListener listener) {
    ellipsizeListeners.remove(listener);
  }

  public boolean isEllipsized() {
    return isEllipsized;
  }

  public int getMaxLines() {
    return maxLines;
  }

  @Override
  public void setMaxLines(int maxLines) {
    super.setMaxLines(maxLines);
    this.maxLines = maxLines;
    isStale = true;
  }

  @Override
  public void setLineSpacing(float add, float mult) {
    this.lineAdditionalVerticalPadding = add;
    this.lineSpacingMultiplier = mult;
    super.setLineSpacing(add, mult);
  }

  @Override
  protected void onTextChanged(CharSequence text, int start, int before,
      int after) {
    super.onTextChanged(text, start, before, after);
    if (!programmaticChange) {
      fullText = text.toString();
      isStale = true;
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (isStale) {
      super.setEllipsize(null);
      resetText();
    }
    super.onDraw(canvas);
  }

  private void resetText() {
    int maxLines = getMaxLines();
    String workingText = fullText;
    boolean ellipsized = false;
    if (maxLines != -1) {
      Layout layout = createWorkingLayout(workingText);
      LogUtils.d(TAG, "LineCount:" + layout.getLineCount());

      if (layout.getLineCount() > maxLines) {
        int lText;
        workingText = fullText.substring(0,
            layout.getLineEnd(maxLines - 1)).trim();
        lText = workingText.length();

        // new handle
        String fText = workingText;
        String eText;
        int sCut = -1;

        // handle text for lines before last line of textView
        while (createWorkingLayout(fText).getLineCount() > (maxLines - 1)) {
          int lastSpace = fText.lastIndexOf(' ');
          if (lastSpace == -1) {
            break;
          }
          sCut = lastSpace;
          fText = fText.substring(0, lastSpace);
        }

        // handle text for last line
        // case: text has space
        if (sCut > 0) {
          eText = workingText.substring(sCut, lText);
          lText = eText.length();

          while (createWorkingLayout(fText + eText + ELLIPSIS)
              .getLineCount() > maxLines) {
            lText = lText - 1;
            eText = eText.substring(0, lText);
          }

          workingText = fText + eText + ELLIPSIS;
        }
        // text hasn't space
        else {
          while (createWorkingLayout(workingText + ELLIPSIS)
              .getLineCount() > maxLines) {
            lText = lText - 1;
            workingText = workingText.substring(0, lText);
          }
          workingText = workingText + ELLIPSIS;
        }

        // old handle
        // while (createWorkingLayout(workingText + ELLIPSIS)
        // .getLineCount() > maxLines) {
        // int lastSpace = workingText.lastIndexOf(' ');
        // if (lastSpace == -1) {
        // break;
        // }
        // workingText = workingText.substring(0, lastSpace);
        // }
        // workingText = workingText + ELLIPSIS;

        ellipsized = true;
      }
    }

    LogUtils.d(TAG, "finalText:" + workingText);

    if (!workingText.equals(getText())) {
      programmaticChange = true;
      try {
        setText(workingText);
      } finally {
        programmaticChange = false;
      }
    }
    isStale = false;
    if (ellipsized != isEllipsized) {
      isEllipsized = ellipsized;
      for (EllipsizeListener listener : ellipsizeListeners) {
        listener.ellipsizeStateChanged(ellipsized);
      }
    }
  }

  private Layout createWorkingLayout(String workingText) {
    return new StaticLayout(workingText, getPaint(), getWidth()
        - getPaddingLeft() - getPaddingRight(), Alignment.ALIGN_NORMAL,
        lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
  }

  @Override
  public void setEllipsize(TruncateAt where) {
    // Ellipsize settings are not respected
  }

  public interface EllipsizeListener {

    void ellipsizeStateChanged(boolean ellipsized);
  }
}