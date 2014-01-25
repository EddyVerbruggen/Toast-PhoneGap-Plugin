cordova.define("nl.x-services.plugins.toast.Toast", function(require, exports, module) {function Toast() {
}

  Toast.prototype._show = function (message, duration, position, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Toast", "show", [message, duration, position]);
  };

  Toast.prototype.shortTop = function (message, successCallback, errorCallback) {
    this._show(message, "short", "top", successCallback, errorCallback);
  };

  Toast.prototype.shortCenter = function (message, successCallback, errorCallback) {
    this._show(message, "short", "center", successCallback, errorCallback);
  };

  Toast.prototype.shortBottom = function (message, successCallback, errorCallback) {
    this._show(message, "short", "bottom", successCallback, errorCallback);
  };

  Toast.prototype.longTop = function (message, successCallback, errorCallback) {
    this._show(message, "long", "top", successCallback, errorCallback);
  };

  Toast.prototype.longCenter = function (message, successCallback, errorCallback) {
    this._show(message, "long", "center", successCallback, errorCallback);
  };

  Toast.prototype.longBottom = function (message, successCallback, errorCallback) {
    this._show(message, "long", "bottom", successCallback, errorCallback);
  };

  Toast.install = function () {
    if (!window.plugins) {
      window.plugins = {};
    }

    window.plugins.toast = new Toast();
    return window.plugins.toast;
  };

  cordova.addConstructor(Toast.install);});
