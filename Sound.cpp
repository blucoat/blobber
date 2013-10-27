#include "Sound.h"
#include <stdio.h>
#include <allegro5\allegro_audio.h>
#include <allegro5\allegro_acodec.h>

//Declare sound constants
Sound *Sound::test;

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

	test = new Sound("res\\test.wav");
}

Sound::Sound(const char *ref)
{
	sample = al_load_sample(ref);
	if(!sample) {
		//im too lazy to append c-strings so we use fprintf
		fprintf(stderr, "Could not load file: %s\n", ref);
		exit(-1);
	}
}

Sound::~Sound(void)
{
	al_destroy_sample(sample);
}

void Sound::play(Sound *sound)
{
	al_play_sample(sound->sample, 1.0f, 0.0f, 1.0f, ALLEGRO_PLAYMODE_ONCE, NULL);
}

void Sound::setMusic(Sound *sound)
{
	if(isMusicPlaying)
		stopMusic();
	isMusicPlaying = al_play_sample(sound->sample, 1.0f, 0.0f, 1.0f, ALLEGRO_PLAYMODE_LOOP, &id);
}

void Sound::stopMusic()
{
	if(isMusicPlaying)
	{
		isMusicPlaying = false;
		al_stop_sample(&id);
	}
}