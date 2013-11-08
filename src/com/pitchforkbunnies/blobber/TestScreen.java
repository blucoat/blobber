package com.pitchforkbunnies.blobber;

public class TestScreen extends Screen {
	
	private Sprite sprite;
	
	public TestScreen(ResourceBundle bundle) {
		super(bundle);
		sprite = new Sprite(bundle.textures.numbers, 0, 0, 20, 20, 5, 60);
	}

	@Override
	public Screen tick() {
		sprite.tick();
		return null;
	}

	@Override
	public void render(Graphics g) {
		sprite.drawAt(g, .5f, .5f);
	}

}
