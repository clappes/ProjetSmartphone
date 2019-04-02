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
@synthesize carte, longitude, latitude,polyline,mapPointArray,param;
double direction = 0;
- (void)viewDidLoad {
    [super viewDidLoad];
    _ipAddressText = @"127.0.0.1";
    _portText = @"55555";
    mapPointArray = [NSMutableArray array];
    carte.delegate = self;
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
            
            if ([[donnees objectAtIndexedSubscript:2] isEqualToString:@"A"]) {
               
                self.carte.showsUserLocation=YES;
                
                NSString *longi = [donnees objectAtIndexedSubscript:5];
                NSArray *arrayLongitude = [longi componentsSeparatedByString:@"."];
                NSString *x = [arrayLongitude[0] substringFromIndex:[arrayLongitude[0] length]-2];
                NSString *lg = [arrayLongitude[0] substringToIndex:[arrayLongitude[0] length]-2];
                NSString *virgule = [NSString stringWithFormat:@"%@.%@",x,arrayLongitude[1]];
                
                
                double val = [lg doubleValue] + [virgule doubleValue]/60;
                if ([[donnees objectAtIndexedSubscript:6] isEqualToString:@"W"]) {
                    val = -val;
                }
                longitude.text = [NSString stringWithFormat:@"%f",val];
                CLLocationDegrees longitudePoint = val;
                
                NSLog(@"ligne:  %@", [donnees objectAtIndexedSubscript:8]);
                direction = [[donnees objectAtIndexedSubscript:8] doubleValue];
                
                NSString *lat = [donnees objectAtIndexedSubscript:3];
                NSArray *arrayLatitude = [lat componentsSeparatedByString:@"."];
                NSString *y = [arrayLatitude[0] substringFromIndex:[arrayLatitude[0] length]-2];
                NSString *lt = [arrayLatitude[0] substringToIndex:[arrayLatitude[0] length]-2];
                NSString *vir = [NSString stringWithFormat:@"%@.%@",y,arrayLatitude[1]];
                
                double val2 = [lt doubleValue] + [vir doubleValue]/60;
                if ([[donnees objectAtIndexedSubscript:4] isEqualToString:@"S"]) {
                    val2 = -val2;
                }
                latitude.text = [NSString stringWithFormat:@"%f",val2];
                CLLocationDegrees latitudePoint = val2;
                
                
                CLLocationCoordinate2D coord = CLLocationCoordinate2DMake(latitudePoint, longitudePoint);
                [mapPointArray addObject:[NSValue valueWithMKCoordinate:coord]];
                [carte setCenterCoordinate:coord animated:true];
                
                CLLocation *location = [[CLLocation alloc] initWithLatitude:latitudePoint longitude:longitudePoint   ];
                
                [carte removeAnnotations:[carte annotations]];
                MKPointAnnotation* annotation = [[MKPointAnnotation alloc] init];
                annotation.coordinate = location.coordinate;
                [carte addAnnotation:annotation];
                
                CLLocationCoordinate2D coordinates[[mapPointArray count]];
                for (NSInteger i=0; i<[mapPointArray count]; i++) {
                    CLLocationCoordinate2D coord = mapPointArray[i].MKCoordinateValue;
                    coordinates[i] = coord;
                }
                MKPolyline *polyline = [MKPolyline polylineWithCoordinates:coordinates count:[mapPointArray count]];
                [carte addOverlay:polyline];
                
            }
        
        
    }
    }
}

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id <MKAnnotation>)annotation
{
    // If it's the user location, just return nil.
    if ([annotation isKindOfClass:[MKUserLocation class]])
        return nil;
    
    // Handle any custom annotations.
    if ([annotation isKindOfClass:[MKPointAnnotation class]])
    {
        // Try to dequeue an existing pin view first.
        MKAnnotationView *pinView = (MKAnnotationView*)[mapView dequeueReusableAnnotationViewWithIdentifier:@"CustomPinAnnotationView"];
        if (!pinView)
        {
            // If an existing pin view was not available, create one.
            pinView = [[MKAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"CustomPinAnnotationView"];
            //pinView.animatesDrop = YES;
            pinView.canShowCallout = YES;
            pinView.image = [UIImage imageNamed:@"boat"];
            pinView.calloutOffset = CGPointMake(0, 32);
        } else {
            pinView.annotation = annotation;
            if(direction>180.0 && direction!=360.0){
                pinView.image = [UIImage imageNamed:@"boat-rev"];
                pinView.transform =                       CGAffineTransformRotate(self.carte.transform,(direction + 90)*3.14159265359/180);
                NSLog(@"rot:  %s","rev");
            }else{
                pinView.image = [UIImage imageNamed:@"boat"];
                pinView.transform =                       CGAffineTransformRotate(self.carte.transform,(direction - 90)*3.14159265359/180);
                 NSLog(@"rot:  %s","norm");
            }
        }
        return pinView;
    }
    return nil;
}

- (MKOverlayRenderer *)mapView:(MKMapView *)mapView rendererForOverlay:(id<MKOverlay>)overlay
{
    if (![overlay isKindOfClass:[MKPolygon class]]) {
        MKPolyline *route = overlay;
        MKPolylineRenderer *renderer = [[MKPolylineRenderer alloc] initWithPolyline:route];
        renderer.strokeColor = [UIColor redColor];
        renderer.lineWidth = 3.0;
        return renderer;
    } else {
        return nil;
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
    
    NSLog(@"Connection en cours %@ : %i", _ipAddressText, [_portText intValue]);
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (__bridge CFStringRef) _ipAddressText, [_portText intValue], &readStream, &writeStream);
    
    messages = [[NSMutableArray alloc] init];
    
    [param setEnabled:NO];
     
    [self open];
}

- (IBAction)disconnect:(id)sender {
    
    [param setEnabled:YES];
    
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

- (IBAction)btAlert:(id)sender {
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"Prametre"
                                                                   message:@"Welcome to the world of iOS"
                                                            preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* save = [UIAlertAction actionWithTitle:@"Save" style:UIAlertActionStyleDefault
                                               handler:^(UIAlertAction * action){
                                                   //Do Some action here
                                                   UITextField *ip_textField = alert.textFields[0];
                                                   UITextField *port_textField = alert.textFields[1];
                                                   
                                                   self->_ipAddressText = ip_textField.text;
                                                   self->_portText = port_textField.text;
                                                   
                                               }];
    UIAlertAction* cancel = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleDefault
                                                   handler:^(UIAlertAction * action) {
                                                       
                                                       NSLog(@"cancel btn");
                                                       
                                                       [alert dismissViewControllerAnimated:YES completion:nil];
                                                       
                                                   }];
    
    [alert addAction:cancel];
    [alert addAction:save];
    
    [alert addTextFieldWithConfigurationHandler:^(UITextField *textField) {
        textField.text = @"127.0.0.1";
    }];
    
    [alert addTextFieldWithConfigurationHandler:^(UITextField *textField) {
        textField.text = @"55555";
    }];
    
    [self presentViewController:alert animated:YES completion:nil];
}
@end
