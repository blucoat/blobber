#pragma once

struct ALLEGRO_SAMPLE;
struct ALLEGRO_SAMPLE_ID;

//TODO add master volume constrols
class Sound
{
private:
	static ALLEGRO_SAMPLE_ID id;
	static bool isMusicPlaying;
public:
	static void init();
	static void destroy();
	static ALLEGRO_SAMPLE *loadSound(const char *ref);
	static void play(ALLEGRO_SAMPLE *sample);		
	static void setMusic(ALLEGRO_SAMPLE *sample);
	static void stopMusic();

	//these have to be pointers so they can be initialized to null
	static ALLEGRO_SAMPLE *test;
};

