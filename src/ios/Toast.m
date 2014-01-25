#import "Toast.h"
#import "Toast+UIView.h"
#import <Cordova/CDV.h>

@implementation Toast

- (void)show:(CDVInvokedUrlCommand*)command {

  // TODO pass in
  NSString *message = @"Hi, this is a test Toast";

  // TODO pass in
  iToastGravity grv = iToastGravityCenter;

  // TODO pass in
  NSInteger drTime = iToastDurationShort;

  [self.view makeToast:@"This is a piece of toast."];

//  [[[[iToast makeText:NSLocalizedString(message, @"")]
//       setGravity:grv offsetLeft:0 offsetTop:0] setDuration:drTime] show];

}

@end