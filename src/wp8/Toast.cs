using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;

// TODO create a custom overlay similar to the iOS implementation because the ShellToast on WP is
// very different from the native Android Toast the iOS impl is inspired on.
// Differences:
// - Only WP8 update 3 will show a Toast when in the foreground
// - A ShellToast can't be positioned
// - A ShellToast has a fixed duration
// DOCS: http://msdn.microsoft.com/en-us/library/windowsphone/develop/jj662938(v=vs.105).aspx
// --> Conclusion: ShellToast is more like a localnotification/pushmessage, so it's a nice WP8 impl of the LocalNotification plugin,
//                 So we'll only add WP8 Toast plugin support if we can create an similar impl as the iOS version of this Toast plugin.
//                 Hence, leaving out the WP8 config in plugin.xml for now.
// --> Future work based on the conclusion: investigate these options http://stackoverflow.com/questions/20346219/how-to-show-toast-after-performing-some-functionality-in-windows-phone-8
namespace Cordova.Extension.Commands {
	public class Toast : BaseCommand {

    public void show(string jsonArgs) {

      var options = JsonHelper.Deserialize<string[]>(jsonArgs);

      var message = options[0];
//      var duration = options[1];
//      var position = options[2];

      ShellToast toast = new ShellToast();
      toast.Title = "Test";
      toast.Content = message;
      toast.Show();

			DispatchCommandResult(new PluginResult(PluginResult.Status.OK));
		}
	}
}