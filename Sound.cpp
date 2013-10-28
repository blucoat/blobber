#include "Sound.h"
#include <allegro5\allegro_audio.h>
#include <allegro5\allegro_acodec.h>

//Declare sound constants
ALLEGRO_SAMPLE *Sound::test;

bool Sound::isMusicPlaying = false;
ALLEGRO_SAMPLE_ID Sound::id;

void Sound::init() 
{
	if(!al_install_audio())
		throw "Could not initialize audio!";
	if(!al_init_acodec_addon())
		throw "Could not initialize aduio codec plugin!";
	if(!al_reserve_samples(1))
		throw "Could not reserve samples!";

	test = loadSound("res\\test.wav");
}

void Sound::destroy()
{
	al_destroy_sample(test);
}

ALLEGRO_SAMPLE *Sound::loadSound(const char *ref)
{
	ALLEGRO_SAMPLE *sample = al_load_sample(ref);
	if(!sample) {
		throw "Failed to load audio sample!";
	}
	return sample;
}

void Sound::play(ALLEGRO_SAMPLE *sample)
{
	al_play_sample(sample, 1.0f, 0.0f, 1.0f, ALLEGRO_PLAYMODE_ONCE, NULL);
}

void Sound::setMusic(ALLEGRO_SAMPLE *sample)
{
	if(isMusicPlaying)
		stopMusic();
	isMusicPlaying = al_play_sample(sample, 1.0f, 0.0f, 1.0f, ALLEGRO_PLAYMODE_LOOP, &id);
}

void Sound::stopMusic()
{
	if(isMusicPlaying)
	{
		isMusicPlaying = false;
		al_stop_sample(&id);
	}
}