//
//  SecondViewController.h
//  ProjectGPS1
//
//  Created by Simon Trelaun on 27/03/2019.
//  Copyright © 2019 Simon Trelaun. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <CoreLocation/CoreLocation.h>
#import <MapKit/MKAnnotation.h>
@import MapKit;

@interface ThirdViewController : UIViewController<NSStreamDelegate,MKMapViewDelegate>{
    IBOutlet MKMapView *carte;
    IBOutlet UIButton *button;
    NSArray *arrayAnnotation;
    MKCoordinateRegion lr;
    MKPolyline *polyline;
    NSMutableArray<NSValue*> *mapPointArray;
    CLLocationCoordinate2D annotationViewCoordinate;
    IBOutlet UIAlertController *alert;
    IBOutlet UIAlertAction *actionOK;
    
    
    
    
    
}
@property(nonatomic,retain) MKMapView *carte;
@property(nonatomic,retain) UIButton *button;
@property(nonatomic,retain)MKPointAnnotation *an;
@property(nonatomic)CLLocationCoordinate2D annotationViewCoordinate;
@property(nonatomic,retain) MKPolyline *polyline;
@property(nonatomic,strong)NSMutableArray<NSValue*> *mapPointArray;
@property (strong, nonatomic) UITapGestureRecognizer *fingerTap;
@property (nonatomic)MKCoordinateRegion lr;
@property(nonatomic,retain) UIAlertController *alert;
@property(nonatomic,retain) UIAlertAction *actionOK;

- (IBAction)clear:(UIButton*)sender;
- (IBAction)tracer:(UIButton*)sender;
- (IBAction)send:(UIButton*)sender;

@end

