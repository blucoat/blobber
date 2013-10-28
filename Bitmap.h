#pragma once

struct ALLEGRO_BITMAP;

class Bitmap
{
private:
	static ALLEGRO_BITMAP * loadImage(const char *ref);

public:
	//sprite sheet declarations
	static ALLEGRO_BITMAP * spritesheet;

	static void init(void);
	static void destroy(void);
	static void draw(ALLEGRO_BITMAP *src, float dx, float dy, float sx, float sy, float w, float h);
};

