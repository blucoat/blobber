package com.pitchforkbunnies.blobber;

public class TestScreen extends Screen {
	
	private int t = 0;
	private Sprite sprite;
	
	public TestScreen(ResourceBundle bundle) {
		super(bundle);
		sprite = new Sprite(bundle.textures.numbers, 0, 0, 20, 20, 5, 60);
	}

	@Override
	public Screen tick() {
		sprite.tick();
		//return (t++ == 120) ? new GameScreen(new LevelTest(bundle), bundle) : null;
		return null;
	}

	@Override
	public void render(Graphics g) {
		sprite.drawAt(g, .5f, .5f);
	}

}
