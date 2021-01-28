#import <Cordova/CDVPlugin.h>

@interface IGStory : CDVPlugin {
}


@property (assign) NSString* callbackId;

- (void)shareToStory:(CDVInvokedUrlCommand *)command;

@end
