var defaultAttrId = 'toast-x',
    defaultAttrClass = 'toast-x';

function Toast() {}

function getDuration(duration) {
  switch (duration) {
    case 'short': return 3000;
    case 'long' : return 6000;
  }
}

function addCssFile (path) {
    var head  = document.getElementsByTagName('head')[0];
    var link  = document.createElement('link');
    link.rel  = 'stylesheet';
    link.type = 'text/css';
    link.href = path;
    link.media = 'all';
    head.appendChild(link);
}

Toast.prototype.show = function (message, duration, position, successCallback, errorCallback) {
  var _self = this;
  _self.hide();

  var toast = document.createElement('div');
  var attrId = document.createAttribute("id");
  attrId.value = defaultAttrId;
  toast.setAttributeNode(attrId);

  var attrClass = document.createAttribute('class');
  attrClass.value = !position ? defaultAttrClass : defaultAttrClass + ' ' + position;
  toast.setAttributeNode(attrClass);

  toast.innerHTML = message;
  
  document.body.insertBefore(toast);

  window.setTimeout(function () {
      _self.hide();
  }, getDuration(duration));
};

Toast.prototype.showShortTop = function (message, successCallback, errorCallback) {
  this.show(message, "short", "top", successCallback, errorCallback);
};

Toast.prototype.showShortCenter = function (message, successCallback, errorCallback) {
  this.show(message, "short", "center", successCallback, errorCallback);
};

Toast.prototype.showShortBottom = function (message, successCallback, errorCallback) {
  this.show(message, "short", "bottom", successCallback, errorCallback);
};

Toast.prototype.showLongTop = function (message, successCallback, errorCallback) {
  this.show(message, "long", "top", successCallback, errorCallback);
};

Toast.prototype.showLongCenter = function (message, successCallback, errorCallback) {
  this.show(message, "long", "center", successCallback, errorCallback);
};

Toast.prototype.showLongBottom = function (message, successCallback, errorCallback) {
  this.show(message, "long", "bottom", successCallback, errorCallback);
};

Toast.prototype.hide = function (successCallback, errorCallback) {
  var toast = document.getElementById(defaultAttrId);
  if (toast) {
    toast.parentElement.removeChild(toast);
  }
};

Toast.install = function () {

  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.toast = new Toast();
  return window.plugins.toast;
};

addCssFile('css/toast-x.css');
cordova.addConstructor(Toast.install);