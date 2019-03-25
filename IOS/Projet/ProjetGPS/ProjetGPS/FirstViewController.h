//
//  FirstViewController.h
//  ProjetGPS
//
//  Created by Renaud Lemee on 25/03/2019.
//  Copyright © 2019 Groupe D. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FirstViewController : UIViewController<NSStreamDelegate>
{
    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    
    NSInputStream   *inputStream;
    NSOutputStream  *outputStream;
    
    NSMutableArray  *messages;
}

@property (weak, nonatomic) IBOutlet UITextField *ipAddressText;
@property (weak, nonatomic) IBOutlet UITextField *portText;
@property (weak, nonatomic) IBOutlet UITextField *dataToSendText;
@property (weak, nonatomic) IBOutlet UITextView *dataRecievedTextView;
@property (weak, nonatomic) IBOutlet UILabel *connectedLabel;


@end

