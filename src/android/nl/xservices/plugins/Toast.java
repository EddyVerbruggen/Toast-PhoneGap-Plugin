package nl.xservices.plugins;

import android.os.Build;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.ViewGroup;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Toast extends CordovaPlugin {

  private static final String ACTION_SHOW_EVENT = "show";
  private static final String ACTION_HIDE_EVENT = "hide";

  private static final int GRAVITY_TOP = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
  private static final int GRAVITY_CENTER = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
  private static final int GRAVITY_BOTTOM = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;

  private static final int BASE_TOP_BOTTOM_OFFSET = 20;

  private android.widget.Toast mostRecentToast;
  private ViewGroup viewGroup;

  private static final boolean IS_AT_LEAST_LOLLIPOP = Build.VERSION.SDK_INT >= 21;

  // note that webView.isPaused() is not Xwalk compatible, so tracking it poor-man style
  private boolean isPaused;

  private String currentMessage;
  private JSONObject currentData;
  private static CountDownTimer _timer;

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_HIDE_EVENT.equals(action)) {
      returnTapEvent("hide", currentMessage, currentData, callbackContext);
      hide();
      callbackContext.success();
      return true;

    } else if (ACTION_SHOW_EVENT.equals(action)) {
      if (this.isPaused) {
        return true;
      }

      final JSONObject options = args.getJSONObject(0);

      final String duration = options.getString("duration");
      final String position = options.getString("position");
      final int addPixelsY = options.has("addPixelsY") ? options.getInt("addPixelsY") : 0;
      final JSONObject data = options.has("data") ? options.getJSONObject("data") : null;
      JSONObject styling = options.optJSONObject("styling");
      final String msg = options.getString("message");
      currentMessage = msg;
      currentData = data;

      String _msg = msg;
      if(styling != null){
        final String textColor = styling.optString("textColor", "#000000");
        _msg = "<font color='"+textColor+"' ><b>" + _msg + "</b></font>";

        final String backgroundColor = styling.optString("backgroundColor", "#333333");
        final Double textSize = styling.optDouble("textSize", -1);
        final double opacity = styling.optDouble("opacity", 0.8);
        final int cornerRadius = styling.optInt("cornerRadius", 100);
        final int horizontalPadding = styling.optInt("horizontalPadding", 50);
        final int verticalPadding = styling.optInt("verticalPadding", 30);
      }
      final String html = _msg;


      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          int hideAfterMs;
          if ("short".equalsIgnoreCase(duration)) {
            hideAfterMs = 2000;
          } else if ("long".equalsIgnoreCase(duration)) {
            hideAfterMs = 4000;
          } else {
            // assuming a number of ms
            hideAfterMs = Integer.parseInt(duration);
          }

          Spanned message;
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            message = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
          } else {
            message = Html.fromHtml(html);
          }
          final android.widget.Toast toast = android.widget.Toast.makeText(
              IS_AT_LEAST_LOLLIPOP ? cordova.getActivity().getWindow().getContext() : cordova.getActivity().getApplicationContext(),
              message,
              "short".equalsIgnoreCase(duration) ? android.widget.Toast.LENGTH_SHORT : android.widget.Toast.LENGTH_LONG
          );

          toast.addCallback(new android.widget.Toast.Callback(){
            public void onToastShown() {

            }

            public void onToastHidden() {
              returnTapEvent("hide", msg, data, callbackContext);
            }
          });

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


          // trigger show every 2500 ms for as long as the requested duration
          _timer = new CountDownTimer(hideAfterMs, 2500) {
            public void onTick(long millisUntilFinished) {
              // see https://github.com/EddyVerbruggen/Toast-PhoneGap-Plugin/issues/116
              // and https://github.com/EddyVerbruggen/Toast-PhoneGap-Plugin/issues/120
//              if (!IS_AT_LEAST_PIE) {
//                toast.show();
//              }
            }
            public void onFinish() {
              returnTapEvent("hide", msg, data, callbackContext);
              toast.cancel();
            }
          }.start();

          mostRecentToast = toast;
          toast.show();

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


  private void hide() {
    if (mostRecentToast != null) {
      mostRecentToast.cancel();
      getViewGroup().setOnTouchListener(null);
    }
    if (_timer != null) {
      _timer.cancel();
    }
  }

  private boolean returnTapEvent(String eventName, String message, JSONObject data, CallbackContext callbackContext) {
    final JSONObject json = new JSONObject();
    try {
      json.put("event", eventName);
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
    hide();
    this.isPaused = true;
  }

  @Override
  public void onResume(boolean multitasking) {
    this.isPaused = false;
  }
}
