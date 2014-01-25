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

      final String message = args.getString(0);
      final String duration = args.getString(1);
      final String position = args.getString(2);

      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          android.widget.Toast toast = new android.widget.Toast(webView.getContext());
          toast.setText(message);

          if ("top".equals(position)) {
            // TODO correct position
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
          } else  if ("bottom".equals(position)) {
            // TODO correct position
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
          } else if ("center".equals(position)) {
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
          } else {
            callbackContext.error("invalid position. valid options are 'top', 'center' and 'bottom'");
          }

          if ("short".equals(duration)) {
            toast.setDuration(android.widget.Toast.LENGTH_SHORT);
          } else if ("long".equals(duration)) {
            toast.setDuration(android.widget.Toast.LENGTH_LONG);
          } else {
            callbackContext.error("invalid duration. valid options are 'short' and 'long'");
          }

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