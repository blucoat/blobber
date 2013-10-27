#include <allegro5\allegro.h>

#include "ExampleScreen.h"
#include "Input.h"
#include "Sound.h"

ExampleScreen::ExampleScreen(void)
{
	brightness = 0.0f;
}


ExampleScreen::~ExampleScreen(void)
{
}

Screen * ExampleScreen::tick(void)
{
	if(Input::isKeyDown(ALLEGRO_KEY_RIGHT))
		brightness += 0.01f;
	if(Input::isKeyClicked(ALLEGRO_KEY_LEFT))
		brightness -= 0.1f;
	if(brightness > 1.0f)
		brightness = 1.0f;
	if(brightness < 0.0f)
		brightness = 0.0f;

	if(Input::isMouseClicked(1))
		Sound::play(Sound::test);

	return NULL;
}

void ExampleScreen::render(void)
{
	al_clear_to_color(al_map_rgb_f(0, 0.5f * brightness, brightness));
}