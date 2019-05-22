
#import "RNPgyerBridge.h"
#import <PgySDK/PgyManager.h>
#import <PgyUpdate/PgyUpdateManager.h>

@implementation RNPgyerBridge

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(initWithAppId:(NSString *)appId)
{
    //启动基本SDK
    [[PgyManager sharedPgyManager] startManagerWithAppId:appId];
    //启动更新检查SDK
    [[PgyUpdateManager sharedPgyManager] startManagerWithAppId:appId];
}

RCT_EXPORT_METHOD(reportException:(NSDictionary *)msg)
{
    
    NSException *err = [NSException exceptionWithName:msg[@"name"] reason:msg[@"reason"] userInfo:msg[@"userInfo"]];
    [[PgyManager sharedPgyManager] reportException:err];
}

@end
  
