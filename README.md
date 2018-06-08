# <p align='center'>JavaGameEngine</p><p align='center'><img src='https://i.imgur.com/F7ymLoY.png'/></p><p align='center'>by: Gabriel Sanchez</p>
## Table of Contents
* [Installation](#installation)
* [Documentation](#documentation)

### Installation
The JavaGameEngine is a 2D game engine built on the `JPanel` class. 
To install:
1) Download the `JavaGameEngine` folder and add it to your java project 
as a separate package. 
2) Create a class that extends `JGNode`. This will be the root node and first screen that will be passed into the game engine.
3) In your `main` method add the following: 
 ```java
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {    
                
                //Create the game window
                JFrame ex = new JGGameEngineMain();
                ex.setVisible(true);  
                
                //Set the root node
                JGGame.sharedInstance().replaceRootNode(new YOUR_ROOT_NODE_CLASS_CONSTRUCTOR_HERE());
            }
        });
```
4) Run the project and a blank window should apear with the `fps` in the bottom left corner.
<br><b>Congratulations!</b> you are now running the JavaGameEngine! 

### Documentation
Documentation will be added to our [Wiki](https://github.com/gsanchez1117/JavaGameEngine/wiki) soon!<br>
In the meantime, you may run the demo tetris applicaiton included in the repo to get an idea of how things work.<br>
To run the demo tetris application simply download/clone the repo and then open the project using Eclipse. From there you can open the RunFromHere.java file located within the Tetris package and run the project.
