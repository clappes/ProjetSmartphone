//
//  FirstViewController.m
//  ProjetGPS
//
//  Created by Renaud Lemee on 25/03/2019.
//  Copyright © 2019 Groupe D. All rights reserved.
//

#import "FirstViewController.h"

@interface FirstViewController ()
@end

@implementation FirstViewController
@synthesize carte, longitude, latitude;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _connectedLabel.text = @"Disconnected";
}

- (IBAction) sendMessage {
    
    NSString *response  = [NSString stringWithFormat:@"msg:%@", _dataToSendText.text];
    NSData *data = [[NSData alloc] initWithData:[response dataUsingEncoding:NSASCIIStringEncoding]];
    [outputStream write:[data bytes] maxLength:[data length]];
    
}

- (void) messageReceived:(NSString *)message {
    
    [messages addObject:message];
    
    _dataRecievedTextView.text = message;
  //  NSLog(@"%@", message);
    
    NSString *ligne = [message componentsSeparatedByString:@"\n"][0];
    if([ligne hasPrefix:@"$GPRMC"]){
        
        NSLog(@"ligne:  %@", ligne);
        NSArray *donnees = [ligne componentsSeparatedByString:@","];
        if([donnees count] >4){
            
            self.carte.showsUserLocation=YES;
           
            NSString *longi = [donnees objectAtIndexedSubscript:5];
            NSArray *arrayLongitude = [longi componentsSeparatedByString:@"."];
            NSString *x = [arrayLongitude[0] substringFromIndex:[arrayLongitude[0] length]-2];
            NSString *lg = [arrayLongitude[0] substringToIndex:[arrayLongitude[0] length]-2];
            NSString *virgule = [NSString stringWithFormat:@"%@.%@",x,arrayLongitude[1]];
            
            double val = [lg doubleValue] + [virgule doubleValue]/60;
            longitude.text = [NSString stringWithFormat:@"%f",val];
            CLLocationDegrees longitudePoint = val;
            
            
            NSString *lat = [donnees objectAtIndexedSubscript:3];
            NSArray *arrayLatitude = [lat componentsSeparatedByString:@"."];
            NSString *y = [arrayLatitude[0] substringFromIndex:[arrayLatitude[0] length]-2];
            NSString *lt = [arrayLatitude[0] substringToIndex:[arrayLatitude[0] length]-2];
            NSString *vir = [NSString stringWithFormat:@"%@.%@",y,arrayLatitude[1]];
            
            double val2 = [lt doubleValue] + [vir doubleValue]/60;
            latitude.text = [NSString stringWithFormat:@"%f",val2];
            CLLocationDegrees latitudePoint = val2;
           
            
            CLLocationCoordinate2D coord = CLLocationCoordinate2DMake(latitudePoint, longitudePoint);
            [carte setCenterCoordinate:coord];
            
            CLLocation *location = [[CLLocation alloc] initWithLatitude:latitudePoint longitude:longitudePoint   ];
          
            MKPointAnnotation* annotation = [[MKPointAnnotation alloc] init];
            annotation.coordinate = location.coordinate;
            [carte addAnnotation:annotation];
            
            MKPolyline *polyline = [MKPolyline polylineWithCoordinates:coordinates count:[coordinates length]];
            [carte addOverlay:polyline];
            self.polyline = polyline;
            
            lineView = [[MKPolylineView alloc]initWithPolyline:self.polyline];
            lineView.strokeColor = [UIColor redColor];
            lineView.lineWidth = 5;
        
    }
    }
}

- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    
    NSLog(@"stream event %lu", streamEvent);
    
    switch (streamEvent) {
            
        case NSStreamEventOpenCompleted:
            NSLog(@"Stream opened");
            _connectedLabel.text = @"Connected";
            break;
        case NSStreamEventHasBytesAvailable:
            
            if (theStream == inputStream)
            {
                uint8_t buffer[1024];
                NSInteger len;
                
                while ([inputStream hasBytesAvailable])
                {
                    len = [inputStream read:buffer maxLength:sizeof(buffer)];
                    if (len > 0)
                    {
                        NSString *output = [[NSString alloc] initWithBytes:buffer length:len encoding:NSASCIIStringEncoding];
                        
                        if (nil != output)
                        {
                            NSLog(@"server said: %@", output);
                            [self messageReceived:output];
                        }
                    }
                }
            }
            break;
            
        case NSStreamEventHasSpaceAvailable:
            NSLog(@"Stream has space available now");
            break;
            
        case NSStreamEventErrorOccurred:
            NSLog(@"%@",[theStream streamError].localizedDescription);
            break;
            
        case NSStreamEventEndEncountered:
            
            [theStream close];
            [theStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
            _connectedLabel.text = @"Disconnected";
            NSLog(@"close stream");
            break;
        default:
            NSLog(@"Unknown event");
    }
    
}

- (IBAction)connectToServer:(id)sender {
    
    NSLog(@"Setting up connection to %@ : %i", _ipAddressText.text, [_portText.text intValue]);
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (__bridge CFStringRef) _ipAddressText.text, [_portText.text intValue], &readStream, &writeStream);
    
    messages = [[NSMutableArray alloc] init];
    
    [self open];
}

- (IBAction)disconnect:(id)sender {
    
    [self close];
}

- (void)open {
    
    NSLog(@"Opening streams.");
    
    outputStream = (__bridge NSOutputStream *)writeStream;
    inputStream = (__bridge NSInputStream *)readStream;
    
    [outputStream setDelegate:self];
    [inputStream setDelegate:self];
    
    [outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    
    [outputStream open];
    [inputStream open];
    
    _connectedLabel.text = @"Connected";
}

- (void)close {
    NSLog(@"Closing streams.");
    [inputStream close];
    [outputStream close];
    [inputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [outputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [inputStream setDelegate:nil];
    [outputStream setDelegate:nil];
    inputStream = nil;
    outputStream = nil;
    
    _connectedLabel.text = @"Disconnected";
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
