#include "Input.h"

bool Input::keys[ALLEGRO_KEY_MAX] = {0};
bool Input::buttons[3] = {0};
bool Input::prevKeys[ALLEGRO_KEY_MAX] = {0};
bool Input::prevButtons[3] = {0};
int Input::x = 0;
int Input::y = 0;


void Input::poll()
{
	memcpy(prevKeys, keys, sizeof(bool) * ALLEGRO_KEY_MAX);
	memcpy(prevButtons, buttons, sizeof(bool) * 3);
}

void Input::onKeyDown(ALLEGRO_EVENT &evt)
{
	keys[evt.keyboard.keycode] = true;
}

void Input::onKeyUp(ALLEGRO_EVENT &evt)
{
	keys[evt.keyboard.keycode] = false;
}

void Input::onMouseDown(ALLEGRO_EVENT &evt)
{
	if(evt.mouse.button <= 3)
		buttons[evt.mouse.button - 1] = true;
}

void Input::onMouseUp(ALLEGRO_EVENT &evt)
{
	if(evt.mouse.button <= 3)
		buttons[evt.mouse.button - 1] = false;
}

void Input::onMouseAxes(ALLEGRO_EVENT &evt)
{
	x = evt.mouse.x;
	y = evt.mouse.y;
}

void Input::onFocusLost()
{
	memset(keys, 0, sizeof(bool) * ALLEGRO_KEY_MAX);
	memset(buttons, 0, sizeof(bool) * 3);
}

bool Input::isKeyDown(int key)
{
	return keys[key];
}

bool Input::isKeyClicked(int key)
{
	return keys[key] && !prevKeys[key];
}

bool Input::isMouseDown(int button)
{
	return buttons[button - 1];
}

bool Input::isMouseClicked(int button)
{
	return buttons[button - 1] && !prevButtons[button - 1];
}

int Input::getMouseX()
{
	return x;
}

int Input::getMouseY() 
{
	return y;
}