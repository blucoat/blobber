#pragma once
#include "Screen.h"

class MainClass
{
private:
	ALLEGRO_DISPLAY *display;
	ALLEGRO_EVENT_QUEUE *queue;
	ALLEGRO_TIMER *timer;
	Screen *screen;

	void tick(void);
	void render(void);

public:
	MainClass(void);
	~MainClass(void);
	void start(void);
};

