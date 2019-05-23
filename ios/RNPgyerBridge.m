
#import "RNPgyerBridge.h"
#import <PgySDK/PgyManager.h>
#import <PgyUpdate/PgyUpdateManager.h>
#import <React/RCTConvert.h>

@implementation RNPgyerBridge{
    RCTPromiseResolveBlock updateInfoSuccess;
    RCTPromiseRejectBlock updateInfoFail;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()
+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

- (NSDictionary *)constantsToExport
{

    return @{
             @"version": [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"] ?: [NSNull null],
             @"build": [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"] ?: [NSNull null],
             };
}

RCT_EXPORT_METHOD(initWithConfig:(NSDictionary *)config)
{
    NSString *appId = config[@"appId"];
    NSString *themeColor = config[@"themeColor"];
    NSNumber *shakingThreshold = config[@"shakingThreshold"];
    NSNumber *type = config[@"type"];
    if(themeColor != NULL){
        [[PgyManager sharedPgyManager] setThemeColor:[self colorWithHexString:themeColor]];
    }
    
    if(shakingThreshold != NULL){
        [[PgyManager sharedPgyManager] setShakingThreshold:shakingThreshold.doubleValue];
    }
    
    if(type != NULL){
        [[PgyManager sharedPgyManager] setFeedbackActiveType:type.integerValue];
    }
    
    //启动基本SDK
    [[PgyManager sharedPgyManager] startManagerWithAppId:appId];
    //启动更新检查SDK
    [[PgyUpdateManager sharedPgyManager] startManagerWithAppId:appId];
}

RCT_EXPORT_METHOD(reportException:(NSDictionary *)msg)
{
    @try {
         NSException *err = [NSException exceptionWithName:msg[@"name"] reason:msg[@"reason"] userInfo:msg[@"userInfo"]];
        @throw err;
    }
    @catch (NSException *exception) {
        [[PgyManager sharedPgyManager] reportException:exception];
    }
}

RCT_EXPORT_METHOD(checkUpdate)
{
    [[PgyUpdateManager sharedPgyManager] checkUpdate];
}

RCT_EXPORT_METHOD(getUpdateInfo:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    updateInfoSuccess = resolve;
    updateInfoFail = reject;
    [[PgyUpdateManager sharedPgyManager] checkUpdateWithDelegete:self selector:@selector(updateMethod:)];
}

RCT_EXPORT_METHOD(showFeedbackView)
{
   [[PgyManager sharedPgyManager] showFeedbackView];
}


- (void)updateMethod:(NSDictionary *)response
{
    if(updateInfoSuccess != NULL){
     updateInfoSuccess(response);
        updateInfoSuccess= NULL;
    }
}
-(UIColor *)colorWithHexString:(NSString *)hexColor{
    NSString * cString = [[hexColor stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] uppercaseString];
    
    // String should be 6 or 8 characters
    if ([cString length] < 6) return [UIColor blackColor];
    
    // strip 0X if it appears
    if ([cString hasPrefix:@"0X"]) cString = [cString substringFromIndex:2];
    if ([cString hasPrefix:@"#"]) cString = [cString substringFromIndex:1];
    
    if ([cString length] != 6) return [UIColor blackColor];
    
    // Separate into r, g, b substrings
    NSRange range;
    range.location = 0;
    range.length = 2;
    NSString * rString = [cString substringWithRange:range];
    
    range.location = 2;
    NSString * gString = [cString substringWithRange:range];
    
    range.location = 4;
    NSString * bString = [cString substringWithRange:range];
    
    // Scan values
    unsigned int r, g, b;
    [[NSScanner scannerWithString:rString] scanHexInt:&r];
    [[NSScanner scannerWithString:gString] scanHexInt:&g];
    [[NSScanner scannerWithString:bString] scanHexInt:&b];
    
    return [UIColor colorWithRed:((float)r / 255.0f)
                           green:((float)g / 255.0f)
                            blue:((float)b / 255.0f)
                           alpha:1];
}

@end
  
