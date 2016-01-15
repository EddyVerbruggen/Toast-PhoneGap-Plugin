package nl.xservices.plugins;

import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
    // TODO nice way for the Toast plugin to offer a longer delay than the default short and long options
    // TODO also look at https://github.com/JohnPersano/Supertoasts
    new CountDownTimer(6000, 1000) {
      public void onTick(long millisUntilFinished) {toast.show();}
      public void onFinish() {toast.show();}
    }.start();

    Also, check https://github.com/JohnPersano/SuperToasts
 */
public class Toast extends CordovaPlugin {

  private static final String ACTION_SHOW_EVENT = "show";
  private static final String ACTION_HIDE_EVENT = "hide";

  private static final int GRAVITY_TOP = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
  private static final int GRAVITY_CENTER = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
  private static final int GRAVITY_BOTTOM = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;

  private static final int BASE_TOP_BOTTOM_OFFSET = 20;

  private android.widget.Toast mostRecentToast;
  private ViewGroup viewGroup;

  private static final boolean IS_AT_LEAST_ANDROID5 = Build.VERSION.SDK_INT >= 21;

  // note that webView.isPaused() is not Xwalk compatible, so tracking it poor-man style
  private boolean isPaused;

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_HIDE_EVENT.equals(action)) {
      if (mostRecentToast != null) {
        mostRecentToast.cancel();
        getViewGroup().setOnTouchListener(null);
      }
      callbackContext.success();
      return true;

    } else if (ACTION_SHOW_EVENT.equals(action)) {

      if (this.isPaused) {
        return true;
      }

      final JSONObject options = args.getJSONObject(0);

      final String message = options.getString("message");
      final String duration = options.getString("duration");
      final String position = options.getString("position");
      final int addPixelsY = options.has("addPixelsY") ? options.getInt("addPixelsY") : 0;
      final JSONObject data = options.has("data") ? options.getJSONObject("data") : null;

      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          final android.widget.Toast toast = android.widget.Toast.makeText(
              IS_AT_LEAST_ANDROID5 ? cordova.getActivity().getWindow().getContext() : cordova.getActivity().getApplicationContext(),
              message,
              "short".equals(duration) ? android.widget.Toast.LENGTH_SHORT : android.widget.Toast.LENGTH_LONG);

          // if we want to change the background color some day, we can use this
//          try {
//            final Method setTintMethod = Drawable.class.getMethod("setTint", int.class);
//            setTintMethod.invoke(toast.getView().getBackground(), Color.RED); // default is Color.DKGRAY
//          } catch (Exception ignore) {
//          }
          if ("top".equals(position)) {
            toast.setGravity(GRAVITY_TOP, 0, BASE_TOP_BOTTOM_OFFSET + addPixelsY);
          } else  if ("bottom".equals(position)) {
            toast.setGravity(GRAVITY_BOTTOM, 0, BASE_TOP_BOTTOM_OFFSET - addPixelsY);
          } else if ("center".equals(position)) {
            toast.setGravity(GRAVITY_CENTER, 0, addPixelsY);
          } else {
            callbackContext.error("invalid position. valid options are 'top', 'center' and 'bottom'");
            return;
          }

          // On Android >= 5 you can no longer rely on the 'toast.getView().setOnTouchListener',
          // so created something funky that compares the Toast position to the tap coordinates.
          if (IS_AT_LEAST_ANDROID5) {
            getViewGroup().setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_DOWN) {
                  return false;
                }
                if (mostRecentToast == null || !mostRecentToast.getView().isShown()) {
                  getViewGroup().setOnTouchListener(null);
                  return false;
                }

                float w = mostRecentToast.getView().getWidth();
                float startX = (view.getWidth() / 2) - (w / 2);
                float endX = (view.getWidth() / 2) + (w / 2);

                float startY;
                float endY;

                float g = mostRecentToast.getGravity();
                float y = mostRecentToast.getYOffset();
                float h = mostRecentToast.getView().getHeight();

                if (g == GRAVITY_BOTTOM) {
                  startY = view.getHeight() - y - h;
                  endY = view.getHeight() - y;
                } else if (g == GRAVITY_CENTER) {
                  startY = (view.getHeight() / 2) + y - (h / 2);
                  endY = (view.getHeight() / 2) + y + (h / 2);
                } else {
                  // top
                  startY = y;
                  endY = y + h;
                }

                float tapX = motionEvent.getX();
                float tapY = motionEvent.getY();

                final boolean tapped = tapX >= startX && tapX <= endX &&
                    tapY >= startY && tapY <= endY;

                if (tapped) {
                  getViewGroup().setOnTouchListener(null);
                  return returnTapEvent(message, data, callbackContext);
                }
                return false;
              }
            });
          } else {
            toast.getView().setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View view, MotionEvent motionEvent) {
                return motionEvent.getAction() == MotionEvent.ACTION_DOWN && returnTapEvent(message, data, callbackContext);
              }
            });
          }

          toast.show();
          mostRecentToast = toast;

          PluginResult pr = new PluginResult(PluginResult.Status.OK);
          pr.setKeepCallback(true);
          callbackContext.sendPluginResult(pr);
        }
      });

      return true;
    } else {
      callbackContext.error("toast." + action + " is not a supported function. Did you mean '" + ACTION_SHOW_EVENT + "'?");
      return false;
    }
  }

  private boolean returnTapEvent(String message, JSONObject data, CallbackContext callbackContext) {
    final JSONObject json = new JSONObject();
    try {
      json.put("event", "touch");
      json.put("message", message);
      json.put("data", data);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    callbackContext.success(json);
    return true;
  }

  // lazy init and caching
  private ViewGroup getViewGroup() {
    if (viewGroup == null) {
      viewGroup = (ViewGroup) ((ViewGroup) cordova.getActivity().findViewById(android.R.id.content)).getChildAt(0);
    }
    return viewGroup;
  }

  @Override
  public void onPause(boolean multitasking) {
    if (mostRecentToast != null) {
      mostRecentToast.cancel();
      getViewGroup().setOnTouchListener(null);
    }
    this.isPaused = true;
  }

  @Override
  public void onResume(boolean multitasking) {
    this.isPaused = false;
  }
}
