#import "IGStory.h"

@implementation IGStory

@synthesize callbackId;

- (void)pluginInitialize {
    
}

- (void)shareToStory:(CDVInvokedUrlCommand *)command {
    self.callbackId = command.callbackId;

    NSString* backgroundImage = [command.arguments objectAtIndex:0];
    NSString* attributionURL = [command.arguments objectAtIndex:1];

    NSLog(@"This is backgroundURL: %@", backgroundImage);

    NSURL *backgroundImageURL = [NSURL URLWithString:backgroundImage];

    NSError *backgroundImageError;
    NSData* imageDataBackground = [NSData dataWithContentsOfURL:backgroundImageURL options:NSDataReadingUncached error:&backgroundImageError];

    if (imageDataBackground && !backgroundImageError) {
        [self shareBackgroundAndStickerImage:imageDataBackground  attributionURL:attributionURL commandId: command.callbackId];
    } else {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Somthing went wrong."];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self finishCommandWithResult:result commandId: command.callbackId];
        });
    }
}

- (void)shareBackgroundAndStickerImage:(NSData *)backgroundImage attributionURL:(NSString *)attributionURL commandId:(NSString *)command  {
    NSURL *urlScheme = [NSURL URLWithString:@"instagram-stories://share"];
    if ([[UIApplication sharedApplication] canOpenURL:urlScheme]) {
        NSLog(@"IG IS AVAIALBLE");
        NSArray *pasteboardItems = @[@{
            @"com.instagram.sharedSticker.backgroundImage" : backgroundImage,
            @"com.instagram.sharedSticker.contentURL" : attributionURL
        }];
        NSDictionary *pasteboardOptions = @{UIPasteboardOptionExpirationDate : [[NSDate date] dateByAddingTimeInterval:60 * 5]};
        [[UIPasteboard generalPasteboard] setItems:pasteboardItems options:pasteboardOptions];
        [[UIApplication sharedApplication] openURL:urlScheme options:@{} completionHandler:nil];
        NSDictionary *payload = [NSDictionary dictionaryWithObjectsAndKeys:attributionURL, @"url", nil];
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:payload];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self finishCommandWithResult:result commandId: command];
        });
    } else {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Instagram is not installed."];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self finishCommandWithResult:result commandId: command];
        });
    }
}

- (void)finishCommandWithResult:(CDVPluginResult *)result commandId:(NSString *)command {
    NSLog(@"This is callbackurl: %@", command);
    if (command != nil) {
        [self.commandDelegate sendPluginResult:result callbackId:command];
    }
}

@end
