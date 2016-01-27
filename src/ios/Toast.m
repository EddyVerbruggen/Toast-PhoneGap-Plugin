#import "Toast.h"
#import "Toast+UIView.h"
#import <Cordova/CDV.h>

@implementation Toast

- (void)show:(CDVInvokedUrlCommand*)command {

  NSDictionary* options = [command argumentAtIndex:0];

  NSString *message  = options[@"message"];
  NSString *duration = options[@"duration"];
  NSString *position = options[@"position"];
  NSDictionary *data = options[@"data"];
  NSNumber *addPixelsY = options[@"addPixelsY"];
  NSDictionary *styling = options[@"styling"];

  if (![position isEqual: @"top"] && ![position isEqual: @"center"] && ![position isEqual: @"bottom"]) {
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"invalid position. valid options are 'top', 'center' and 'bottom'"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    return;
  }

  NSInteger durationInt;
  if ([duration isEqual: @"short"]) {
    durationInt = 2;
  } else if ([duration isEqual: @"long"]) {
    durationInt = 5;
  } else {
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"invalid duration. valid options are 'short' and 'long'"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    return;
  }

  [self.webView makeToast:message
                 duration:durationInt
                 position:position
               addPixelsY:addPixelsY == nil ? 0 : [addPixelsY intValue]
                     data:data
                  styling:styling
          commandDelegate:self.commandDelegate
               callbackId:command.callbackId];

  CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  pluginResult.keepCallback = [NSNumber numberWithBool:YES];
  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)hide:(CDVInvokedUrlCommand*)command {
  [self.webView hideToast];

  CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end