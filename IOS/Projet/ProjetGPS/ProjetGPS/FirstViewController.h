//
//  FirstViewController.h
//  ProjetGPS
//
//  Created by Renaud Lemee on 25/03/2019.
//  Copyright © 2019 Groupe D. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

@interface FirstViewController : UIViewController<NSStreamDelegate,MKMapViewDelegate>
{
    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    
    NSInputStream   *inputStream;
    NSOutputStream  *outputStream;
    MKMapView *carte;
    MKPolyline *polyline;
    NSMutableArray<NSValue*> *mapPointArray;
    
    
    NSMutableArray  *messages;
}

@property (weak, nonatomic) NSString *ipAddressText;
@property (weak, nonatomic) NSString *portText;
@property (weak, nonatomic) IBOutlet UITextField *dataToSendText;
@property (weak, nonatomic) IBOutlet UITextView *dataRecievedTextView;
@property (weak, nonatomic) IBOutlet UILabel *connectedLabel;
@property (weak, nonatomic) IBOutlet UILabel *latitude;
@property (weak, nonatomic) IBOutlet UILabel *longitude;
@property(retain, nonatomic) IBOutlet MKMapView * carte;
@property (retain, nonatomic) MKPolyline *polyline;
@property (strong, nonatomic) NSMutableArray<NSValue*> *mapPointArray;

- (IBAction)btAlert:(id)sender;


@end

