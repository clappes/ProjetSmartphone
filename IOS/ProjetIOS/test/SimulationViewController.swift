//
//  SimulationViewController.swift
//  test
//
//  Created by Florian Rouillon on 01/04/2019.
//  Copyright © 2019 Florian Rouillon. All rights reserved.
//

import UIKit
import MapKit
import CoreMotion

class SimulationViewController: UIViewController, MKMapViewDelegate {
    
    @IBOutlet weak var map: MKMapView!
    @IBOutlet weak var home: UIButton!
    @IBOutlet weak var urgence: UIButton!
    var motionManager = CMMotionManager()
    let lr = CLLocationCoordinate2D.init(latitude:46.145618,longitude:-1.167781)
    let span = MKCoordinateSpanMake(0.005, 0.005)
    let annot = MKPointAnnotation()
    
    @IBOutlet weak var speedLabel: UILabel!
    @IBOutlet weak var longLabel: UILabel!
    @IBOutlet weak var latLabel: UILabel!
    
    var angle = 0.0
    var speed = 0.0
    var lat = 46.145618
    var long = -1.167781
    var sensi = 10000000.0
    var barre = 1.5
   
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        let value = UIInterfaceOrientation.landscapeLeft.rawValue
        UIDevice.current.setValue(value, forKey: "orientation")
        
        map.delegate = self
        
        
        
        // Initialisation Map
        let lp = CLLocationCoordinate2D.init(latitude:45.145618,longitude:-1.167781)
        let region = MKCoordinateRegion(center: lr, span: span)
        map.setRegion(region, animated: true)
        annot.title="Bateau"
        annot.coordinate = lr
        map.addAnnotation(annot)
        
        //polyline
        var array : [CLLocationCoordinate2D]
        array = [lr]
        var line = MKPolyline(coordinates: array, count: 1)
        map.add(line)
        
        
        
        //Gyroscope
        motionManager.gyroUpdateInterval = 0.2
        
        motionManager.startGyroUpdates(to: OperationQueue.current!){(data,error) in
            if let myData=data
            {
                print(myData.rotationRate)
                let x=myData.rotationRate.x
                let y=myData.rotationRate.y
                let z=myData.rotationRate.z
                let newCoord = self.calculCoord(x: x, y: y, z: z)
                let cam = MKCoordinateRegion(center: newCoord, span: self.span)
                self.map.setRegion(cam, animated: true)
                self.annot.coordinate=newCoord
                array.append(newCoord)
               
            }
                
            }
        }

    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        let value = UIInterfaceOrientation.portrait.rawValue
        UIDevice.current.setValue(value, forKey: "orientation")
    }
    
    func calculCoord(x: Double,y: Double,z: Double) -> CLLocationCoordinate2D{
        
        if(z <= 0 && x > 0) {
            speed = z * (-60)
            speedLabel.text = String(speed)
        }else if (x < 0) {
            speed=60
        }
        if(y < -0.25) {
            barre = barre + 0.1;
            
            if(barre < 30.0) {
                barre = 30.0;
            }
        } else if(y > 0.25) {
            barre = barre - 0.1;
            
            if(barre < -30.0) {
                barre = -30.0;
            }
        }
        
        // Calcul de la latitude
        lat = lat + (cos(barre) * (speed / 0.05))/sensi
        latLabel.text = String(lat)
        // Calcul de la longitude
        long = long + (sin(barre) * ( speed / 0.05 ))/sensi
        longLabel.text = String(long)
        
        var coord = CLLocationCoordinate2D.init(latitude:lat ,longitude:long)
        return coord
    }
    
    @IBAction func retourHome(_ sender: Any) {
        let region = MKCoordinateRegion(center: lr, span: span)
        map.setRegion(region, animated: true)
        annot.coordinate=lr
        
    }
    
    @IBAction func stopUrgence(_ sender: UIButton) {
        if(sender.titleLabel!.text == "URGENCE"){
        motionManager.stopGyroUpdates()
        sender.setTitle("RELANCER", for: .normal)
        }else {
        motionManager.startGyroUpdates()
        sender.setTitle("URGENCE", for: .normal)
        }
    }
    
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
            let renderer = MKPolylineRenderer(overlay: overlay)
            renderer.strokeColor = UIColor.red
            renderer.lineWidth = 3.0
        return renderer
    }
    
   
    
    
    
    
}
