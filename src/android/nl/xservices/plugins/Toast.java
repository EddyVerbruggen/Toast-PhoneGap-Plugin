package nl.xservices.plugins;

import android.view.Gravity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class Toast extends CordovaPlugin {

  private static final String ACTION_SHOW_EVENT = "show";

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_SHOW_EVENT.equals(action)) {

      // TODO pass in
      final CharSequence text = "Hello toast!";

      // TODO pass in duration, accept a few options: short, long (depends on iOS possibilities as well)
      final int duration = android.widget.Toast.LENGTH_LONG; // short=2sec, long=4secs

      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          android.widget.Toast toast = android.widget.Toast.makeText(
              webView.getContext(),
              text,
              duration);

          // TODO pass in position, accept a few options: top, bottom, center (depends on iOS possibilities as well)
          toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);

          toast.show();

          callbackContext.success();
        }
      });

      return true;
    } else {
      callbackContext.error("toast." + action + " is not a supported function. Did you mean '" + ACTION_SHOW_EVENT + "'?");
      return false;
    }
  }
}