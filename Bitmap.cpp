#include "Bitmap.h"
#include <allegro5\allegro.h>
#include <allegro5\allegro_image.h>

//sprite sheet declarations
ALLEGRO_BITMAP *Bitmap::spritesheet;

void Bitmap::init(void)
{
	if(!al_init_image_addon())
		throw "Failed to initialize image addon!";

	spritesheet = al_load_bitmap("res\\donger.png");
	if(!spritesheet)
		throw "Failed to load image: donger.png";
}

void Bitmap::destroy(void)
{
	al_destroy_bitmap(spritesheet);	
}

void Bitmap::draw(ALLEGRO_BITMAP *src, float dx, float dy, float sx, float sy, float w, float h)
{
	al_draw_bitmap_region(src, sx, sy, w, h, dx, dy, 0);
}

ALLEGRO_BITMAP *Bitmap::loadImage(const char *ref)
{
	return al_load_bitmap(ref);
}
