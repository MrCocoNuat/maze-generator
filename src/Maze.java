/*
 * Maze
 * Copyright (C) 2020-2021 Aaron Wang
 *
 * This file is part of Maze.
 *
 * Maze is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Maze is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Maze.  If not, see <http://www.gnu.org/licenses/>
 */




/*
Generates mazes made of rooms with four sides each, and optionally outputs to stdOut,
draws them, or draws their solution. h and l are the dimensions of the maze, and odc
is the chance that a door becomes opened after generation, to make loops in the maze.

Compile: javac Maze.java
Execute: java Maze h l [odc]
 */
import java.util.Stack;
import java.util.ArrayList;
import java.awt.Color;

public class Maze{

    private int height, length;
    private Room[][] rooms;
    private Door[][] vDoors, hDoors;
    
    public Maze(int h, int l, double openDoorChance){
	this.height = h;
	this.length = l;

        this.rooms = new Room[h][l];
	this.vDoors = new Door[h][l-1];
	this.hDoors = new Door[h-1][l];

	for(int i = 0; i < h; i++){
	    for(int j = 0; j < l; j++){
		this.rooms[i][j] = new Room();
	    }
	}
		
	for(int i = 0; i < h; i++){
	    for(int j = 0; j < l-1; j++){
		this.vDoors[i][j] = new Door();
	    }
	}
	for(int i = 0; i < h-1; i++){
	    for(int j = 0; j < l; j++){
		this.hDoors[i][j] = new Door();
	    }
	}

	this.generate();

	for(int i = 0; i < h; i++){
	    for(int j = 0; j < l-1; j++){
		if (Math.random() < openDoorChance) this.vDoors[i][j].open();
	    }
	}
	for(int i = 0; i < h-1; i++){
	    for(int j = 0; j < l; j++){
		if (Math.random() < openDoorChance) this.hDoors[i][j].open();
	    }
	}

    }

    public void generate(){ 
	Stack<int[]> chain = new Stack<int[]>();
	int[] start = {0,0};

	chain.push(start);
	this.rooms[0][0].gen();
	
	while (!chain.empty()){
	    int[] currentRoom = chain.peek();
	    int[][] neighbors = ungeneratedNeighbors(currentRoom[0], currentRoom[1]);
	    if (neighbors.length == 0){
		chain.pop();
		//System.out.println("back");
		continue;
	    }

	    int[] nextRoom = neighbors[(int) (Math.random() * neighbors.length)];
	    if (nextRoom[0] != currentRoom[0]){
		if (nextRoom[0] < currentRoom[0]){
		    this.hDoors[nextRoom[0]][nextRoom[1]].open(); //advancing up
		    //System.out.println("up");
		} else{
		    this.hDoors[currentRoom[0]][currentRoom[1]].open(); //advancing down
		    //System.out.println("down");
		}
	    } else{
		if (nextRoom[1] < currentRoom[1]){
		    this.vDoors[nextRoom[0]][nextRoom[1]].open(); //advancing left
		    //System.out.println("left");
		} else{
		    this.vDoors[currentRoom[0]][currentRoom[1]].open(); //advancing right
		    //System.out.println("right");
		}
	    }

	    this.rooms[nextRoom[0]][nextRoom[1]].gen();
	    int[] newCopy = {nextRoom[0],nextRoom[1]};
	    chain.push(newCopy);
	    
	}

    }

    public int[][] ungeneratedNeighbors(int y, int x){
	int[][] neighbors = new int[4][2];
	int count = 0;
	if (y > 0 && !this.rooms[y-1][x].generated()){
	    neighbors[count][0] = y-1;
	    neighbors[count][1] = x;
	    count++;
	}
	if (x > 0 && !this.rooms[y][x-1].generated()){
	    neighbors[count][0] = y;
	    neighbors[count][1] = x-1;
	    count++;
	}
	if (y < this.height - 1 && !this.rooms[y+1][x].generated()){
	    neighbors[count][0] = y+1;
	    neighbors[count][1] = x;
	    count++;
	}
	if (x < this.length - 1 && !this.rooms[y][x+1].generated()){
	    neighbors[count][0] = y;
	    neighbors[count][1] = x+1;
	    count++;
	}

	int[][] cutNeighbors = new int[count][2]; //remove unused spots
	for(int i = 0; i < count; i++){
	    cutNeighbors[i] = neighbors[i];
	}
	return cutNeighbors;
    }
    
    @Override
    public String toString(){
	String mazeString = "";

	//top outer wall
	for(int i = 0; i < 2 * this.length + 1; i++){
	    mazeString += "X";
	}
	mazeString += "\n";
	

	for(int i = 0; i < 2 * this.height - 1; i++){
	    String lineString = "X"; //left outer wall

	    if (i % 2 == 0){
		for(int j = 0; j < this.length - 1; j++){
		    lineString += (this.vDoors[i/2][j].isOpen())? "  " : " X";
		}
		lineString += " X";
 	    } else{
		for(int j = 0; j < this.length; j++){
		    lineString += (this.hDoors[i/2][j].isOpen())? " X" : "XX";
		}
	    }
	    mazeString += lineString + "\n";
	}

	//bottom outer wall
	for(int i = 0; i < 2 * this.length + 1; i++){
	    mazeString += "X";
	}
	mazeString += "\n";
	
	return mazeString;
    }

    public void draw(){
	double scaler = 1300.0 / Math.max(this.length * 2 + 1, this.height * 2 + 1); //Change the double value to your preferred maximum dimension in pixels
	StdDraw.setCanvasSize((int) (scaler *(this.length * 2 + 1)), (int) (scaler *(this.height * 2 + 1)));
	StdDraw.setYscale(- (this.height * 2 - 1) - 0.5, 1.5);
	StdDraw.setXscale(-1.5, (this.length * 2 - 1) + 0.5);
	StdDraw.enableDoubleBuffering();

	//top/bottom wall
	for(int j = -1; j < this.length * 2; j++){
	    StdDraw.filledSquare(j, 1, 0.5);
	    StdDraw.filledSquare(j, - this.height * 2 + 1, 0.5);
	}
	
	for(int i = 0; i < 2 * this.height - 1; i++){
	    StdDraw.filledSquare(-1, -i, 0.5);
	    
	    if (i % 2 == 0){
		for(int j = 0; j < this.length - 1; j++){
		    if (this.vDoors[i/2][j].isOpen()){
			//StdDraw.filledSquare(j * 2 + 1, -i, 0.1);
		    } else{
			StdDraw.filledSquare(j * 2 + 1, -i, 0.5);
		    }
		}

 	    } else{
		for(int j = 0; j < this.length; j++){
		    if (this.hDoors[i/2][j].isOpen()){
			//StdDraw.filledSquare(j * 2, -i, 0.1);
			StdDraw.filledSquare(j * 2 + 1, -i, 0.5);
		    }
		    else {
			StdDraw.filledSquare(j * 2, -i, 0.5);
			StdDraw.filledSquare(j * 2 + 1, -i, 0.5);
		    }
		}

	    }
	    
	    StdDraw.filledSquare(this.length * 2 - 1, -i, 0.5); 
	}


	//draw start and end squares
	StdDraw.setPenColor(StdDraw.RED);
	StdDraw.filledSquare(0, 0, 0.5);
	StdDraw.setPenColor(StdDraw.GREEN);
	StdDraw.filledSquare(2 * this.length - 2, - 2 * this.height + 2, 0.5);
	StdDraw.show();
    }

    public void drawSolution(){ //Dijkstra's because f u A*, way too much memory
	this.rooms[0][0].setDistance(0);
	this.rooms[0][0].visit();
	
	ArrayList<int[]> loRooms = new ArrayList<int[]>();
	int hiDistance = 1;
	ArrayList<int[]> hiRooms = new ArrayList<int[]>();

	int[] zz = {0,0};
	loRooms.add(zz);
	
	while (loRooms.size() > 0 && this.rooms[this.height-1][this.length-1].getDistance() == -1){
	    for(int[] coords : loRooms){
		int[][] adjacents = unvisitedAdjacents(coords[0], coords[1]);
		for(int[] newCoords : adjacents){
		    this.rooms[newCoords[0]][newCoords[1]].visit();
		    this.rooms[newCoords[0]][newCoords[1]].setDistance(hiDistance);
		    hiRooms.add(newCoords);
		}
	    }
	    hiDistance++;
	    
	    loRooms = hiRooms;
	    hiRooms = new ArrayList<int[]>();
	}


	// Colors every block until end is hit
	/*
	Color[] colors = {StdDraw.RED, StdDraw.ORANGE, StdDraw.YELLOW, StdDraw.GREEN, StdDraw.BLUE, StdDraw.MAGENTA};
	for(int i = 0; i < this.height; i++){
	    for(int j = 0; j < this.length; j++){
		int dist = this.rooms[i][j].getDistance();
		if (dist != -1){
		    StdDraw.setPenColor(colors[dist % 6]);
		    StdDraw.filledSquare(j * 2, - (i * 2), 0.2);
		}
	    }
	}
	*/
	

	//Only colors the path to the end
	
	StdDraw.setPenColor(StdDraw.MAGENTA);
	StdDraw.setPenRadius(0.005);
	
	int[] currentRoom = {this.height - 1, this.length - 1};
	for(int d = this.rooms[this.height - 1][this.length - 1].getDistance() - 1; d > 0; d--){

	    //StdDraw.show(); //enable for pretty
	    
	    if (currentRoom[0] > 0 && this.rooms[currentRoom[0] - 1][currentRoom[1]].getDistance() == d && this.hDoors[currentRoom[0]-1][currentRoom[1]].isOpen()){
		currentRoom[0]--;
		//StdDraw.filledSquare (currentRoom[1] * 2, - (currentRoom[0] * 2), 0.1);
		StdDraw.line(currentRoom[1] * 2 - 0.3, - (currentRoom[0] * 2 - 0.3), currentRoom[1] * 2 + 0.3,  - (currentRoom[0] * 2 - 0.3));
		StdDraw.line(currentRoom[1] * 2 - 0.3, - (currentRoom[0] * 2 - 0.3), currentRoom[1] * 2,  - (currentRoom[0] * 2 + 0.3));
		StdDraw.line(currentRoom[1] * 2 + 0.3, - (currentRoom[0] * 2 - 0.3), currentRoom[1] * 2,  - (currentRoom[0] * 2 + 0.3));
		continue; 
	    }
	    if (currentRoom[1] > 0 && this.rooms[currentRoom[0]][currentRoom[1] - 1].getDistance() == d && this.vDoors[currentRoom[0]][currentRoom[1]-1].isOpen()){
		currentRoom[1]--;
		//StdDraw.filledSquare (currentRoom[1] * 2, - (currentRoom[0] * 2), 0.2);
		StdDraw.line(currentRoom[1] * 2 - 0.3, - (currentRoom[0] * 2 - 0.3), currentRoom[1] * 2 - 0.3,  - (currentRoom[0] * 2 + 0.3));
		StdDraw.line(currentRoom[1] * 2 - 0.3, - (currentRoom[0] * 2 - 0.3), currentRoom[1] * 2 + 0.3,  - (currentRoom[0] * 2));
		StdDraw.line(currentRoom[1] * 2 - 0.3, - (currentRoom[0] * 2 + 0.3), currentRoom[1] * 2 + 0.3,  - (currentRoom[0] * 2));
		continue;
	    }
	    if (currentRoom[0] < this.height - 1 && this.rooms[currentRoom[0] + 1][currentRoom[1]].getDistance() == d && this.hDoors[currentRoom[0]][currentRoom[1]].isOpen()){
		currentRoom[0]++;
		//StdDraw.filledSquare (currentRoom[1] * 2, - (currentRoom[0] * 2), 0.3);
		StdDraw.line(currentRoom[1] * 2 - 0.3, - (currentRoom[0] * 2 + 0.3), currentRoom[1] * 2 + 0.3,  - (currentRoom[0] * 2 + 0.3));
		StdDraw.line(currentRoom[1] * 2 - 0.3, - (currentRoom[0] * 2 + 0.3), currentRoom[1] * 2,  - (currentRoom[0] * 2 - 0.3));
		StdDraw.line(currentRoom[1] * 2 + 0.3, - (currentRoom[0] * 2 + 0.3), currentRoom[1] * 2,  - (currentRoom[0] * 2 - 0.3));
		continue;
	    }
	    if (currentRoom[1] < this.length - 1 && this.rooms[currentRoom[0]][currentRoom[1] + 1].getDistance() == d && this.vDoors[currentRoom[0]][currentRoom[1]].isOpen()){
		currentRoom[1]++;
		//StdDraw.filledSquare (currentRoom[1] * 2, - (currentRoom[0] * 2), 0.4);
		StdDraw.line(currentRoom[1] * 2 + 0.3, - (currentRoom[0] * 2 - 0.3), currentRoom[1] * 2 + 0.3,  - (currentRoom[0] * 2 + 0.3));
		StdDraw.line(currentRoom[1] * 2 + 0.3, - (currentRoom[0] * 2 - 0.3), currentRoom[1] * 2 - 0.3,  - (currentRoom[0] * 2));
		StdDraw.line(currentRoom[1] * 2 + 0.3, - (currentRoom[0] * 2 + 0.3), currentRoom[1] * 2 - 0.3,  - (currentRoom[0] * 2));
		continue;
	    }
	}
	StdDraw.show();
	
    }

    public int[][] unvisitedAdjacents(int y, int x){ //yeah i know code recycling and all that
	int[][] neighbors = new int[4][2];
	int count = 0;
	if (y > 0 && !this.rooms[y-1][x].visited() && this.hDoors[y-1][x].isOpen()){
	    neighbors[count][0] = y-1;
	    neighbors[count][1] = x;
	    count++;
	}
	if (x > 0 && !this.rooms[y][x-1].visited() && this.vDoors[y][x-1].isOpen()){
	    neighbors[count][0] = y;
	    neighbors[count][1] = x-1;
	    count++;
	}
	if (y < this.height - 1 && !this.rooms[y+1][x].visited() && this.hDoors[y][x].isOpen()){
	    neighbors[count][0] = y+1;
	    neighbors[count][1] = x;
	    count++;
	}
	if (x < this.length - 1 && !this.rooms[y][x+1].visited() && this.vDoors[y][x].isOpen()){
	    neighbors[count][0] = y;
	    neighbors[count][1] = x+1;
	    count++;
	}

	int[][] cutNeighbors = new int[count][2]; //remove unused spots
	for(int i = 0; i < count; i++){
	    cutNeighbors[i] = neighbors[i];
	}
	return cutNeighbors;
    }
    
    public static void main(String[] args){
	double odc = 0; //openDoorChance
	if (args.length > 2) odc = Double.parseDouble(args[2]);
	
	Maze myMaze = new Maze(Integer.parseInt(args[0]), Integer.parseInt(args[1]), odc);
	//System.out.println(myMaze);
	myMaze.draw();
	myMaze.drawSolution();
    }
}
