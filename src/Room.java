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





public class Room{

    private int distanceFromStart;
    private boolean generated;
    private boolean visited;
    
    public Room(){
	this.distanceFromStart = -1;
	this.generated = false;
	this.visited = false;
    }

    public int getDistance(){
	return this.distanceFromStart;
    }

    public boolean generated(){
	return this.generated;
    }

    public void gen(){
	this.generated = true;
    }

    public void setDistance(int d){
	this.distanceFromStart = d;
    }

    public void visit(){
	this.visited = true;
    }

    public boolean visited(){
	return this.visited;
    }
}
