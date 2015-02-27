function Toast() {
}

Toast.prototype.show = function (message, duration, position, successCallback, errorCallback, offsetX, offsetY) {
  cordova.exec(successCallback, errorCallback, "Toast", "show", [message, duration, position, offsetX, offsetY]);
};

Toast.prototype.showShortTop = function (message, successCallback, errorCallback, offset) {
  this.show(message, "short", "top", successCallback, errorCallback, offset && offset.x || 0, offset && offset.y || 20);
};

Toast.prototype.showShortCenter = function (message, successCallback, errorCallback, offset) {
  this.show(message, "short", "center", successCallback, errorCallback, offset && offset.x || 0, offset && offset.y || 0);
};

Toast.prototype.showShortBottom = function (message, successCallback, errorCallback, offset) {
  this.show(message, "short", "bottom", successCallback, errorCallback, offset && offset.x || 0, offset && offset.y || 20);
};

Toast.prototype.showLongTop = function (message, successCallback, errorCallback, offset) {
  this.show(message, "long", "top", successCallback, errorCallback, offset && offset.x || 0, offset && offset.y || 20);
};

Toast.prototype.showLongCenter = function (message, successCallback, errorCallback, offset) {
  this.show(message, "long", "center", successCallback, errorCallback, offset && offset.x || 0, offset && offset.y || 0);
};

Toast.prototype.showLongBottom = function (message, successCallback, errorCallback, offset) {
  this.show(message, "long", "bottom", successCallback, errorCallback, offset && offset.x || 0, offset && offset.y || 20);
};

Toast.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.toast = new Toast();
  return window.plugins.toast;
};

cordova.addConstructor(Toast.install);
