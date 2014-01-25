#import "Toast.h"
#import <Cordova/CDV.h> // TODO: required?
#import "lib/iToast.h"

@implementation Toast

- (void)show:(CDVInvokedUrlCommand*)command {

  // TODO pass in
  NSString *message = @"Hi, this is a test Toast";

  // TODO pass in
  iToastGravity grv = iToastGravityCenter;

  // TODO pass in
  NSInteger drTime = iToastDurationShort;

  [[[[iToast makeText:NSLocalizedString(message, @"")]
       setGravity:grv offsetLeft:0 offsetTop:0] setDuration:drTime] show];

}

@end