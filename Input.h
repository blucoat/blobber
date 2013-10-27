#pragma once
#include <allegro5\allegro.h>

class Input
{
private:
	static bool keys[ALLEGRO_KEY_MAX];
	static bool buttons[3];
	static bool prevKeys[ALLEGRO_KEY_MAX];
	static bool prevButtons[3];
	static int x, y;

public:
	static void poll();

	static void onKeyDown(ALLEGRO_EVENT &evt);
	static void onKeyUp(ALLEGRO_EVENT &evt);
	static void onMouseDown(ALLEGRO_EVENT &evt);
	static void onMouseUp(ALLEGRO_EVENT &evt);
	static void onMouseAxes(ALLEGRO_EVENT &evt);
	static void onFocusLost();

	static bool isKeyDown(int key);
	static bool isKeyClicked(int key);
	static bool isMouseDown(int button);
	static bool isMouseClicked(int button);
	static int getMouseX();
	static int getMouseY();
};

