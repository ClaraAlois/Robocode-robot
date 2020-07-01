# Robocode-robot
This is the second coursework of COMP222 at the University of Liverpool. The objective of this assignment is to design and implement a tank bot for theRobocode tank battle game.
## Game Description
Robocode is a programming game, where the goal is to develop a robot battletank to battle against other tanks.  The robot battles are running in real-timeand on-screen.  Robots can move, shoot at each other, scan for each other,and hit the walls (or other robots).  
## Coursework Task
This assignment requires students to design and implement a tank bot for theRobocode tank battle game.  students need to choose a game AI behaviour model(such as, for example, finite state machine, decision trees, behaviour trees,or any other mechanism of your choice) and implement students' robot based onthis behaviour model.
  
## Final Product Presentatiom
 ### Description of the behaviour control model
 The control model chosen was FSM (i. e. Final state machine). Here comes some general description of the FSM control model.

 Formally, the ﬁnal state machine is an abstract model of computation, which consists of a set of states, an input vocabulary, and a transition function that transfers the current state to the next state by receiving an input. Deviating from the formal deﬁnition, in the game development ﬁeld, the FSM control model contains behavior-deﬁning states (as long as an agent stays in a state, it carries on the same action), transition function divided among states and some extra information.

 In an FSM control scheme game, the agent will go through a sequence of states along with transitions. To be speciﬁc, state actions in the game are what player sees (for example, movement and animation); Transition could be either user’s input or state of the world (Therefore, the FSM method could mix user control and physical modeling). In addition, there are diﬀerent types of transitions: internal, external, immediate, and deferred.

 When it comes to implementing an FSM control model, there are diﬀerent ways: Hardcoded (switch statement), Scripted, and Hybrid Approach.

 In the practice, Hierarchical FSM is a common approach to realize the generalization, when there are multiple “levels” of behavior. Moreover, a stack could be used to store past states.
 
 There are several advantages to use FSM: it is easy to diagram, program, and debug; it is completely genera to any problem. However, there are also problems with this strategy: it may cause an explosion of states; it might be too predictable; FSM often created with ad hoc structure.

 ### Robocode bot design
  #### Robot FSM: UML Diagram
  For the diagram, see the detached file in the repository.
  
  This ﬁgure represents the FSM control model controlling the robot’s behavior. This FSM consists of ﬁve states: Move, Set Firepower, Set Scanner, Set Gun, and Fire. Besides, the    states Move, Set Firepower, and Set Scanner have their inner sub-states separately, while their transitions are controlled by diﬀerent events.

  #### Justify of design decisions
   ##### Justify decisions between states
   The transition between outer states is deﬁned by the execution sequence of them. In a single game loop, the actions(Move, Set Firepower, Set Scanner, Set Gun, and Fire) will be taken sequentially for composing the basic manners of a robot. Thus, the transitions are deﬁned by ”execution over” information. Moreover, after all the actions in a single game loop are taken, the game will proceed to the next loop. Before all actions are taken, all bullets will move and the game engine will check for collisions then alter respective energy values. Therefore, the decision after ”Fire” will be deﬁned by transition ”energy=0” or ”energy¿0”. If energy¿0, the robot will return to the ”Move” state and proceed execution. If energy=0, according to the rule, the robot will be disabled.

   ##### Justify decisions between sub-states 
 Move The state ”Move” consists of two sub-states: move in a circle and reverse direction. At the start, the robot will move circle the enemy by default. There are three diﬀerent inputs causing transition to ”change moving direction”: ”timing to change the direction”, message for ”might hit a wall if continue current moving manner”, and ”shot by bullet”.
– For justifying ”timing to change the direction”, the mechanism behind this is to change the moving direction of the robot, therefore, give the robot’s movement more randomness. The more randomly it moves, it is harder for enemies to predict its position, therefore decrease the possibility for the robot to be hit by bullets.
– For justifying ”message for ’might hit a wall if continue current moving manner’”, this function is for preventing the robot hit and stack on the wall.
– For justifying ”shot by bullet”, the reason to realize this is: if the robot is shot by any bullet, it is possible that some enemies have already tracked on it. Thus, change the direction now may break the convention in other robot’s tracking algorithms, indeed make the robot safer.


Set Firepower In this state, the transferring between states, which represents the magnitude of ﬁrepower was deﬁned by the distance between the target enemy and the robot. The heuristic here is: the closer the enemy is, it is more possible to shoot on it, thus the ﬁrepower should be increased(represented as transferred to ”increase ﬁrepower state”), and vice-versa.

Set Scanner There are two sub-states in this state: ”re-scan” and ”move the radar towards the target”, while the transition relies on any found enemy in the last 4 scan rounds. The heuristic here is: if there is no enemy scanned after 4th scanning, the robot may lose the target. Thus, it needs to adjust the degree and try to ﬁnd a new enemy from the start.


 ### Implementation
  #### Implementation of the robot class
   In this class, ﬁve states are realized as four self-deﬁned methods and a method from the library: 1. stateMove() 2. stateSetFire() 3. stateSetScanner() 4. stateSetGun() 5. ﬁre() In the while loop inside of run(), these ﬁve methods will be called in order as the process that robot transits between diﬀerent states.

 Besides, the default methods onScannedRobot(), onRobotDeath(), onHitWall(), and onHitByBullet() also helped to oﬀer the related information for robot to take transition.

 There are also several helper method for simplifying the code: NormaliseBearing(double angle) (For shortening the bearing) NormaliseHeading(double angle) (For shortening the heading) getrange( double x1,double y1, double x2,double y2 ) (For calculating the distance between two x,y coordinates) absbearing( double x1,double y1, double x2,double y2 ) (For getting the absolute bearing between to x,y coordinates)

  #### Implementation of the Enemy class
  There are 3 methods in this class, which could help to alter and store the information of enemies: 
  
  **public double guessX(long time)** The method to predict x value of the enemy after its movement 
  
  **public double guessY(long time)** The method to predict y value of the enemy after its movement 
  
  **public double calculateDistance(long time)** The method to calculate linear distance from start to the end
