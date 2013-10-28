#include <allegro5\allegro.h>
#include <stdio.h>

#include "MainClass.h"
#include "Screen.h"
#include "ExampleScreen.h"
#include "Input.h"
#include "Sound.h"
#include "Bitmap.h"

const int FPS = 60;

MainClass::MainClass(void)
{
	//Why am I making thorough error messages, most players won't even know what stderr is
	if(!al_init())
		throw "Could not initialize Allegro!";	//wtf language lets you throw a string, really?

	if(!al_install_keyboard())
		throw "Could not initialize keyboard!";

	if(!al_install_mouse())
		throw "Could not initialize mouse!";

	display = al_create_display(800, 600);
	if(!display)
		throw "Could not create display!";

	timer = al_create_timer(1.0 / FPS);
	if(!timer)
		throw "Could not create timer!";

	queue = al_create_event_queue();
	if(!queue)
		throw "Could not create event queue!";

	Sound::init();
	Bitmap::init();

	al_register_event_source(queue, al_get_display_event_source(display));
	al_register_event_source(queue, al_get_timer_event_source(timer));
	al_register_event_source(queue, al_get_keyboard_event_source());
	al_register_event_source(queue, al_get_mouse_event_source());

	screen = new ExampleScreen();
}

MainClass::~MainClass(void)
{
	al_destroy_event_queue(queue);
	al_destroy_display(display);
	al_destroy_timer(timer);

	Bitmap::destroy();
	Sound::destroy();
}

//Starts the main game loop
void MainClass::start(void)
{
	ALLEGRO_EVENT evt;
	bool running = true;

	al_start_timer(timer);

	while(running)
	{
		al_wait_for_event(queue, &evt);
		switch(evt.type)
		{
		case ALLEGRO_EVENT_DISPLAY_CLOSE:
			running = false;
			break;
		case ALLEGRO_EVENT_TIMER:
			tick();
			render();
			break;
		case ALLEGRO_EVENT_KEY_DOWN:
			Input::onKeyDown(evt);
			break;
		case ALLEGRO_EVENT_KEY_UP:
			Input::onKeyUp(evt);
			break;
		case ALLEGRO_EVENT_MOUSE_BUTTON_DOWN:
			Input::onMouseDown(evt);
			break;
		case ALLEGRO_EVENT_MOUSE_BUTTON_UP:
			Input::onMouseUp(evt);
			break;
		case ALLEGRO_EVENT_MOUSE_AXES:
			Input::onMouseAxes(evt);
			break;
		case ALLEGRO_EVENT_DISPLAY_SWITCH_OUT:
			Input::onFocusLost();
			break;
		}
	}
}

void MainClass::tick(void)
{
	Screen *next = screen->tick();
	if(next)
	{
		delete screen;
		screen = next;
	}

	Input::poll();
}

void MainClass::render(void)
{
	al_clear_to_color(al_map_rgb(0, 0, 0));
	screen->render();
	al_flip_display();
}

int main(int argc, char **argv)
{
	try {
		MainClass mc;
		mc.start();
	} catch (const char* err) {
		fprintf(stderr, "%s\n", err);
		return -1;
	}
	return 0;
}