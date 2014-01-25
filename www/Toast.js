function Toast() {
}

Toast.prototype.show = function (message, duration, position, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Toast", "show", [message, duration, position]);
};

Toast.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.toast = new Toast();
  return window.plugins.toast;
};

cordova.addConstructor(Toast.install);