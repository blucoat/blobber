#pragma once
#include "Screen.h"
class ExampleScreen : public Screen
{
private:
	float brightness;
public:
	ExampleScreen(void);
	~ExampleScreen(void);
	Screen * tick(void);
	void render(void);
};

