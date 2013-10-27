#pragma once

class Screen
{
public:
	virtual ~Screen(void) {};
	//Does one logical tick (1/60th of a second), and returns the screen to change to, or NULL if the screen should be the same
	virtual Screen * tick(void) = 0;	
	//Renders the screen to the... screen? :P
	virtual void render(void) = 0;
};

