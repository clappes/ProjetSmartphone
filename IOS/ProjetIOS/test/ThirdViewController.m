
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <CoreLocation/CoreLocation.h>
#import <MapKit/MKAnnotation.h>
#import "ThirdViewController.h"
#import <Mapkit/MKPolyline.h>
@import MapKit;

@interface ThirdViewController()

@end
@implementation ThirdViewController
@synthesize carte,button , an, annotationViewCoordinate, polyline, mapPointArray,lr;



- (void)viewDidLoad {
    
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    //Map delegate
    self.carte.delegate=self;
    
    //Init map region
    CLLocationCoordinate2D location = CLLocationCoordinate2DMake(46.153585,-1.160560); // location
    MKCoordinateSpan span = MKCoordinateSpanMake(0.03,0.03); // zoom
    lr = MKCoordinateRegionMake(location,span);  // charge la location et le zoom
    [carte setRegion: lr animated:YES]; // affiche
    
    //init tableau point
    mapPointArray = [[NSMutableArray alloc]init];
    
    //init trajet
    polyline= [[MKPolyline alloc]init];
    
    //Gestionnaire clique map
    UITapGestureRecognizer *fingerTap = [[UITapGestureRecognizer alloc]
                                         initWithTarget:self action:@selector(handleMapFingerTap:)];
    fingerTap.numberOfTapsRequired = 1;
    fingerTap.numberOfTouchesRequired = 1;
    [self.carte addGestureRecognizer:fingerTap];
}


- (void)handleMapFingerTap:(UIGestureRecognizer *)gestureRecognizer {
    
    if (gestureRecognizer.state != UIGestureRecognizerStateEnded) {
        return;
    }
    
    //Recupérer position clique
    CGPoint touchPoint = [gestureRecognizer locationInView:self.carte];
    CLLocationCoordinate2D touchMapCoordinate =
    [self.carte convertPoint:touchPoint toCoordinateFromView:self.carte];
    CLLocation *location = [[CLLocation alloc] initWithLatitude:touchMapCoordinate.latitude longitude:touchMapCoordinate.longitude];
    
    //Création marker
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = location.coordinate;
    annotationPoint.title=@"WayPoint";
    
    //Ajout point au tableau
    NSValue *val = [NSValue valueWithMKCoordinate:touchMapCoordinate];
    [mapPointArray addObject:val];
    
    //Ajout marker
    [self.carte addAnnotation: annotationPoint];
    
    alert = [UIAlertController alertControllerWithTitle:@"WayPoint" message:@"Veuillez entrer une vitesse" preferredStyle:UIAlertControllerStyleAlert];
    [alert addTextFieldWithConfigurationHandler :^(UITextField *textField){
        textField.placeholder= @"5.2";
    }];
    
    
    actionOK = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        NSString *vitesse= self -> alert.textFields[0].text;
        NSNumber *latitude= [NSNumber numberWithDouble:annotationPoint.coordinate.latitude];
        NSNumber *longitude= [NSNumber numberWithDouble:annotationPoint.coordinate.longitude];
        annotationPoint.subtitle= [NSString stringWithFormat:@"Vitesse: %@", vitesse];
        NSLog(@"Vitesse %@", vitesse);
        
        
        
    }];
    [ alert addAction: actionOK];
    [self presentViewController:alert animated:YES completion:nil];

}

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id<MKOverlay>)overlay
{
    if([overlay isKindOfClass:[MKPolyline class]])
    {
        MKPolylineView *lineView = [[MKPolylineView alloc] initWithPolyline:overlay];
        lineView.lineWidth = 1;
        lineView.strokeColor = [UIColor redColor];
        lineView.fillColor = [UIColor redColor];
        return lineView;
    }
    return nil;
}

-(MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotationPoint
{
    if ([annotationPoint isKindOfClass:[MKUserLocation class]])//keep the user as default
        return nil;
    
    static NSString *annotationIdentifier = @"annotationIdentifier";
    MKPinAnnotationView *pinView = [[MKPinAnnotationView alloc]initWithAnnotation:annotationPoint reuseIdentifier:annotationIdentifier];
    
    UIButton *modifier = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
    if([[annotationPoint title]isEqualToString:@"WayPoint"])
    {
        [modifier addTarget:self action:@selector(popup:) forControlEvents:UIControlEventTouchUpInside];
        
    }
    
    pinView.rightCalloutAccessoryView=modifier;
    pinView.pinColor=MKPinAnnotationColorPurple;
    pinView.draggable=YES;
    pinView.canShowCallout=YES;
    
    return pinView;
}

- (void)mapView:(MKMapView *)mapView
 annotationView:(MKAnnotationView *)annotationView
didChangeDragState:(MKAnnotationViewDragState)newState
   fromOldState:(MKAnnotationViewDragState)oldState
{
    if (newState == MKAnnotationViewDragStateStarting)
    {
        CLLocationCoordinate2D startAt = annotationView.annotation.coordinate;
        NSLog(@"dropped at %f,%f", startAt.latitude, startAt.longitude);
        
    }
    if (newState == MKAnnotationViewDragStateEnding)
    {
        CLLocationCoordinate2D droppedAt = annotationView.annotation.coordinate;
        NSLog(@"dropped at %f,%f", droppedAt.latitude, droppedAt.longitude);
        
    }
}

-(void)popup:(id)sender {
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    
    alert = [UIAlertController alertControllerWithTitle:@"WayPoint" message:@"Entrer votre nouvelle vitesse :" preferredStyle:UIAlertControllerStyleAlert];
    [alert addTextFieldWithConfigurationHandler :^(UITextField *textField){
        textField.placeholder= @"5.2";
    }];
    
    actionOK = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        NSString *vitesse= self -> alert.textFields[0].text;
        
        NSNumber *latitude= [NSNumber numberWithDouble:annotationPoint.coordinate.latitude];
        NSNumber *longitude= [NSNumber numberWithDouble:annotationPoint.coordinate.longitude];
        annotationPoint.subtitle= [NSString stringWithFormat:@"Vitesse: %@",vitesse];
        //NSLog(@"Vitesse %@", vitesse);
        
        NSUInteger indexlat = [self -> mapPointArray indexOfObject:latitude];
        NSUInteger indexlong = [self -> mapPointArray indexOfObject:longitude];
        
       /*if([ vitesse isEqualToString:@""]==true){
            vitesse=[self -> mapPointArray objectAtIndex:indexlong+1];
            
        }
        if (indexlat== indexlong-1){
            [self -> mapPointArray replaceObjectAtIndex:indexlong+1 withObject:vitesse];
            
        }*/
        
    }];
    
    [ alert addAction: actionOK];
    [self presentViewController:alert animated:YES completion:nil];
    [self.carte addAnnotation: annotationPoint];
    
    
}

- (IBAction)clear:(UIButton *)sender {
    
    
    id userAnnotation = self.carte.userLocation;
    NSMutableArray *annotations = [NSMutableArray arrayWithArray:self.carte.annotations];
    [annotations removeObject:userAnnotation];
    [self.carte removeOverlays:carte.overlays];
    [self.carte removeAnnotations:annotations];
    mapPointArray = [[NSMutableArray alloc]init];
}

- (IBAction)tracer:(UIButton*)sender {
    
    CLLocationCoordinate2D coordinates[[mapPointArray count]];
    if([mapPointArray count] != 1){
        for (NSInteger i=0; i<[mapPointArray count]; i++) {
            // NSInteger j=i-1;
            CLLocationCoordinate2D coord = mapPointArray[i].MKCoordinateValue;
            
            coordinates[i] = coord;
        }
    }
    MKPolyline *line = [MKPolyline polylineWithCoordinates:coordinates count:[mapPointArray count]];
    [self.carte addOverlay:line];
    NSLog(@"ça marche");
    
    
}

- (IBAction)send:(UIBarButtonItem *)sender {
    
}

@end

