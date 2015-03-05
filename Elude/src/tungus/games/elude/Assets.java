package tungus.games.elude;

import java.io.IOException;
import java.io.ObjectInputStream;

import tungus.games.elude.game.client.worldrender.lastingeffects.ParticleEffectPool;
import tungus.games.elude.game.client.worldrender.lastingeffects.ParticleEffectPool.PooledEffect;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.menu.settings.Settings;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Assets {

	public static class Strings {
		public static String endless = "SURVIVAL";
		public static String[] arcadeNames;
		public static String[] finiteNames;
		public static void load() {
			try {
				ObjectInputStream in = new ObjectInputStream(Gdx.files.internal("levels/arcadenames").read());
				arcadeNames = (String[])(in.readObject());
				in.close();
				in = new ObjectInputStream(Gdx.files.internal("levels/finitenames").read());
				finiteNames = (String[])(in.readObject());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}

	public static TextureAtlas atlas;

	public static enum Tex {
		ELUDE_TITLE_ON("mainmenu/EludeOn"),
		PLAY_SINGLE_BUTTON("mainmenu/playsingle"),
		SETTINGS_BUTTON("mainmenu/settings"),
		MULTIPLAYER_BUTTON("mainmenu/multi"),
		INFO_BUTTON("mainmenu/info"),
		HALF_PLAY_PANEL("mainmenu/halfplaypanel"),

		VESSEL,
		VESSELRED,
		SHIELD,
		ROCKET,
		STANDINGENEMY,
		MOVINGENEMY,
		KAMIKAZE,
		SHARPSHOOTER,
		MACHINEGUNNER,
		SPLITTER,
		FACTORY,
		MINION,
		MINER,
		SHIELDED,
		BOSS1,
		BOSS1_BACK,
		BOSS2,

		HPBONUS,
		SPEEDBONUS,
		SHIELDBONUS,
		FREEZERBONUS,

		MINEHELP,
		LINEAR_GRADIENT_SPOT,
		LASER,

		WHITE_RECTANGLE,
		SMALL_CIRCLE,
		SWARMROCKET_SPOT,

		VIRTUALDPAD,

		PAUSE(),
		RESUME("ingamemenu/resume"),
		TO_MENU("ingamemenu/tomenu"),
		SHADOWER("ingamemenu/shadower"),
		NEXT_LEVEL("ingamemenu/nextlevel"),
		RESTART("ingamemenu/restart"),

		FRAME,
		FRAME_RED,
		FRAME_BLUE,
		FRAME_YELLOW,
		FRAME_GREEN,
		LOCK,
		PLAY_LEVEL,
		STAR_OFF, STAR_ON,
		STAR_OFF_SMALL, STAR_ON_SMALL;

		private String filename;
		public TextureRegion t = null;

		Tex(String path) {
			this.filename = path;
		}

		Tex() {
			filename = name().replace("_", "").toLowerCase();
		}

		private void load() {
			t = atlas.findRegion(filename);
		}
	}

	public static enum Particles {
		FLAME_ROCKET(40, 80),
		MATRIX_ROCKET(20, 40),
		STRAIGHT_ROCKET,
		EXPLOSION(20, 40),
		EXPLOSION_MED(5, 10),
		EXPLOSION_BIG(5, 10),
		EXPLOSION_SMALL(100, 200),
		DEBRIS(20, 40),
		DEBRIS_MED(4, 8),
		DEBRIS_BIG(1, 1),
		VESSEL_TRAILS(1, 1),
		VESSEL_TRAILS_RED(1, 1);

		private static String prefix = "particles/";
		private final int initialCapacity;
		private final int max;
		private final String filename;

		public ParticleEffectPool p;

		Particles() {
			this(10, 50);
		}

		Particles(int initial, int max) {
			this.initialCapacity = initial;
			this.max = max;
			filename = name().replace("_", "").toLowerCase();
		}

		private void load() {
			ParticleEffect particle = new ParticleEffect();
			particle.load(Gdx.files.internal(prefix + filename), Assets.atlas);
			p = new ParticleEffectPool(particle, initialCapacity, max);
		}

		public static PooledEffect debris(float[] color, float dir, int typeID) {
			PooledEffect p = (values()[typeID]).p.obtain();
			Array<ParticleEmitter> emitters = p.getEmitters();
			for (int i = 0; i < emitters.size; i++) {
				// Mod color
				float[] separateColor = color.clone(); // Color for each emitter - last one uses up the original array
				for (int j = 0; j < 3; j++) {
					// Randomly change the color slightly
					float mul = MathUtils.random() + 0.5f;
					separateColor[j] = MathUtils.clamp(color[j]*mul, 0f, 1f);
				}
				emitters.get(i).getTint().setColors(separateColor);

				// Mod angle
				if (dir == dir) { // Dir is not NaN (NaN != NaN)
					emitters.get(i).getAngle().setLow(dir);
					emitters.get(i).getAngle().setHigh(-90, 90);
				}
				else {
					emitters.get(i).getAngle().setHigh(-180, 180);
				}
			}
			return p;
		}
	}

	public static enum Sounds {
		EXPLOSION,
		MENU_BUTTON,
		LASERSHOT;

		private static final String prefix = "sounds/";
		private final String filename;

		public Sound s;

		private boolean looping = false;

		Sounds() {
			filename = name().replace("_", "").toLowerCase() + ".wav";
		}

		Sounds(String filename) {
			this.filename = filename;
		}

		Sounds(boolean looping) {
			this();
			this.looping = looping;
		}

		private void load() {
			s = Gdx.audio.newSound(Gdx.files.internal(prefix + filename));
		}

		public long play() {
			if (Settings.INSTANCE.soundOn) {
				if (looping) {
					return s.loop();
				} else {
					return s.play();
				}
			}
			else return -1;
		}
	}

	public static enum EludeMusic {

		BOSS("fireaurastart.ogg", "fireauraloop.ogg", 65350),
		INGAME("kid2willstart.ogg", "kid2willloop.ogg", 27310),
		MENU("aurorastart.ogg", "auroraloop.ogg", 83598);

		private static final String prefix = "music/";

		private final String startFilename;
		private final String loopFilename;

		Music start;
		final int startLength;
		Music loop;

		static EludeMusic currentPlaying = null;
		static boolean loopPart = false;
		static float volume;

		EludeMusic(String start, String loop, int length) {
			startFilename = start;
			loopFilename = loop;
			startLength = length;
		}

		public void load() {
			if (startFilename != null) {
				start = Gdx.audio.newMusic(Gdx.files.internal(prefix + startFilename));
			}
			loop = Gdx.audio.newMusic(Gdx.files.internal(prefix + loopFilename));
		}

		public static void set(EludeMusic m) {
			set(m, 1f);
		}

		public static void set(EludeMusic music, float vol) {
			if (!Settings.INSTANCE.musicOn) {
				music = null;
			}                    
			if (currentPlaying == music) {
				return;
			}
			new Thread(new MusicSwitcher(music, vol)).start();
		}
	}

	public static enum Shaders {
		DEFAULT,
		MINE("basicvertex", "minefragment"),
		FREEZE_ENEMY("basicvertex", "freezefragment");

		private static final String prefix = "shaders/";
		private final String vertex;
		private final String fragment;

		public ShaderProgram s;

		Shaders(String v, String f) {
			vertex = v;
			fragment = f;
		}

		Shaders() {
			vertex = fragment = null;
		}

		private void load() {
			if (this != DEFAULT) {
				s = new ShaderProgram(Gdx.files.internal(prefix + vertex), Gdx.files.internal(prefix + fragment));
				if (!s.isCompiled()) {
					Gdx.app.setLogLevel(Application.LOG_ERROR);
					Gdx.app.log(name() + " shader error", s.getLog());
					throw new GdxRuntimeException(name() + " shader not compiled");			
				}
			} else {
				s = SpriteBatch.createDefaultShader();
			}
		}

		private static void bindPhases() {
			for (RenderPhase r : RenderPhase.values()) {
				r.shader = DEFAULT.s;
			}
			RenderPhase.MINE.shader = MINE.s;
		}
	}

	public static BitmapFont font;

	public static void load() {
		atlas = new TextureAtlas(Gdx.files.internal("textures/game.atlas"));

		for (Tex t : Tex.values()) {
			t.load();
		}
		for (Particles p : Particles.values()) {
			p.load();
		}
		for (Sounds s : Sounds.values()) {
			s.load();
		}
		for (EludeMusic m : EludeMusic.values()) {
			m.load();
		}
		for (Shaders s : Shaders.values()) {
			s.load();
		}
		Shaders.bindPhases();
		Strings.load();
		loadFont();
	}

	private static void loadFont() {
		Texture fontTex = new Texture(Gdx.files.internal("font/bulletproof.png"));
		fontTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion fontRegion = new TextureRegion(fontTex);
		font = new BitmapFont(Gdx.files.internal("font/bulletproof.fnt"), fontRegion);
	}

	public static FileHandle levelFile(int levelNum) {
		return Gdx.files.internal("levels/" + levelNum + ".lvl");
	}
}
