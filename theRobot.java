/*
 * Student name: Chen Lyu
 * Student ID number: 201448129
 */

package comp222;
import robocode.*;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * ChenTest1 - a robot by Chen Lyu
 */
public class ChenTest1 extends AdvancedRobot
{
	
	Enemy target;	//The current target enemy
	
	//Direction that robot is heading: 1 means its heading forwards; -1 = means its heading backwards
	int heading = 1;	
	double firePower;	//The shooting power

	
	public void run() 
	{
		setColors(Color.blue,Color.white,Color.yellow); // body,gun,radar
		target = new Enemy();	// Initialize an enemy
		target.dist = 99999;	//Initialize the distance to select target      
		
		//Make the gun, radar and the robot independent of each other
		setAdjustGunForRobotTurn(true);        
		setAdjustRadarForGunTurn(true);  
		
		// Make the radar turn one revolution   
		turnRadarRightRadians(2*Math.PI);            
		         
		
		while(true)         
		{       
			// The followings are the tasks need to do in every game loop
			stateMove(); 					//Move the robot            
			stateSetFire();					//Set the fire power            
			stateSetScanner();				//Move the scanner to scan      
			stateSetGun();					//Move the gun        
			
			//out.println(target.dist);                  
			
			fire(firePower);            //Fire           
			execute();                  //execute all commands   
		}
		
	}

	

    /* 
     * The method to define the moving manner.
     * Move in circle surrounding the target enemy with the heading pointing back and forward,      
     */    
	void stateMove()     
	{        
		if (getTime()%20 == 0)  
		{            
			//Change the direction after every twenty ticks to make random moving        
			heading = heading*(-1);  
		}
		
		// To avoid hitting the wall
		// If the predict position is closed to the wall, reverse the direction and move
		double head = getHeadingRadians();
		double x = getX() + (heading*300)*Math.sin(head);
		double y = getY() + (heading*300)*Math.cos(head);
		double dWidth = getBattleFieldWidth();
		double dHeight = getBattleFieldHeight();
		if(x < 30 || x > dWidth-30 || y < 30 || y > dHeight-30)
		{
			heading = heading*(-1); 
		}
		
		setAhead(heading*300);	// Move 
		
		setTurnRightRadians(target.bearing + (Math.PI/2));
	}    
	

	
	 /* 
	  * Method to set fire power based on the distance of enemy     
	  */    
	void stateSetFire()     
	{        
		//The closer the enemy is, more possible to shot on it
		//Thus, smaller distance takes stronger fire power 
		firePower = 400/target.dist;                                      
	}

	
		
	/*
	 * The method to set scanner
	 * Always move the scanner towards target to track it 
	 */    
	void stateSetScanner()     
	{        
		double offset;  //The offset of radar
      
		if (getTime() - target.cTime > 4)   
		{            
			//if there is no robot in one round, scan again from the beginning        
			offset = 360;            
		}         
		else         
		{            
			double targetAngle = absbearing(getX(),getY(),target.x,target.y);	//The angle to target enemy
			offset = getRadarHeadingRadians() - targetAngle;	//Move radar towards target  
			
			//this adds or subtracts small amounts from the bearing for the radar to produce the wobbling            
			//to make sure we don't lose the target               
			if (offset >= 0) 
			{
				offset += Math.PI/8;  
			}                      
			else 
			{
				offset -= Math.PI/8; 
			}        
		}        
		//turn the radar        
		setTurnRadarLeftRadians(NormaliseBearing(offset));
	}   
	
	
	/* 
	 * The method to set the direction of gun
	 * Turns it to the estimated position of enemy  
	 */    
	void stateSetGun()     
	{        
		//Predict the time that bullet arrives   
		long time = getTime() + (int)(target.dist/(20-(3*firePower)));        
		
		//offsets the gun by the angle to the next shot based on linear targeting       
		double gunOffset = getGunHeadingRadians() - absbearing(getX(),getY(),target.guessX(time),target.guessY(time));        setTurnGunLeftRadians(NormaliseBearing(gunOffset));  
		    
		}   
	
	   
	public void onScannedRobot(ScannedRobotEvent e)     
	{        
		//Change the target if there is a closer one      
		if ((e.getName() == target.name)||(target.dist > e.getDistance()))         
		{            
			//the next line gets the absolute bearing to the position of robot         
			double absbearing_rad = (e.getBearingRadians()+getHeadingRadians())%(2*Math.PI);      
			
			//sets all the information of target            
			target.name = e.getName();    
			target.bearing = e.getBearingRadians();            
			target.head = e.getHeadingRadians();            
			target.cTime = getTime(); 
			target.x = getX()+Math.sin(absbearing_rad)*e.getDistance();          
			target.y = getY()+Math.cos(absbearing_rad)*e.getDistance();      
			              
			//game time at which this scan was produced   
			target.speed = e.getVelocity();	// get the speed of target       
			target.dist = e.getDistance();        
			
		}    
		
	}    
	
	public void onRobotDeath(RobotDeathEvent e)     
	{        
		// restart to search for the next target
		if (e.getName() == target.name)	target.dist = 9999;    
	}    
  
	public void onHitByBullet(HitByBulletEvent e) {
		// change the move behavior to avoid enemy's tracking
		heading = heading*(-1);      
	}
	

	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	

	
	/*    
	 *The helper methods
	 */  
  
	//shorten the bearing (-pi,pi) if it is not in in this range   
	double NormaliseBearing(double angle)     
	{        
		if (angle < -Math.PI)
		{
			angle = angle + 2*Math.PI;
		}    
		if (angle > Math.PI) 
		{
			angle = angle - 2*Math.PI;  
		}	      
		return angle;    
	}    
	
	// Shorten the heading (0,2pi) if it is not in in this range
	double NormaliseHeading(double angle)     
	{      
		if (angle < 0) 
		{
			angle += 2*Math.PI; 
		}
		if (angle > 2*Math.PI) 
		{
			angle -= 2*Math.PI;  
		}        
		return angle;    
		
	}    
	
	// Calculate the distance between two x,y coordinates    
	public double getrange( double x1,double y1, double x2,double y2 )    
	{        
		double x = x2-x1;        
		double y = y2-y1;  
		       
		return Math.sqrt( x*x + y*y);       
		
	}    
	
	// Gets the absolute bearing between to x,y coordinates      
	public double absbearing( double x1,double y1, double x2,double y2 )    
	{        
		double x = x2-x1;        
		double y = y2-y1;        
		double h = getrange( x1,y1, x2,y2 );  
		
		if( x > 0 && y > 0 )        
		{          
			// The 1st quadrant 
			return Math.asin( x / h );	       
		}        
		if( x > 0 && y < 0 )        
		{            
			//The 2nd quadrant   
			return Math.PI - Math.asin( x / h );   
		}        
		if( x < 0 && y < 0 )        
		{            
			//The 3rd quadrant 
			return Math.PI + Math.asin( -x / h );     
		}        
		if( x < 0 && y > 0 )        
		{       
			 // The 4th quadrant 
			return 2.0*Math.PI - Math.asin( -x / h );       
		}        
		else
		{
			return 0;  
		}
		
	}    

	
}

// The class for enemy
// Used to store the information of enemies
class Enemy {
	
	public String name;    
	 
	public double bearing, head;     
	public double speed, dist;    
	public double x,y;    
	 
	public long cTime; // Game time that the scan was produced   
	 
	// The method to predict x value of the enemy after its movement
	public double guessX(long time)    
	{        
		double distance = calculateDistance(time);
		return x+Math.sin(head)*distance; // Use sin to calculate the difference on x
	}    
	
	// The method to predict y value of the enemy after its movement
	public double guessY(long time)    
	{        
		double distance = calculateDistance(time);
		return y+Math.cos(head)*distance;  // Use sin to calculate the difference on y
	}
	  
	// The method to calculate linear distance from start to the end
	public double calculateDistance(long time) 
	{
		long diff = time - cTime;  // The difference of time   
		double distance = speed*diff;	// Calculate the distance using the max speed
		return distance;
	}
		 
	  
}
	


