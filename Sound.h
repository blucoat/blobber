#pragma once
#include <allegro5\allegro_audio.h>

//TODO add master volume constrols
class Sound
{
private:
	ALLEGRO_SAMPLE *sample;

	static ALLEGRO_SAMPLE_ID id;
	static bool isMusicPlaying;
public:
	Sound(const char *ref);
	~Sound(void);

	static void init();
	static void play(Sound *sound);		
	static void setMusic(Sound *sound);
	static void stopMusic();

	//these have to be pointers so they can be initialized to null
	static Sound *test;
};

