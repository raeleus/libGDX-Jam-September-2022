package com.ray3k.template.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.ray3k.template.*;
import com.ray3k.template.Resources.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

import static com.ray3k.template.Core.*;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher implements CrossPlatformWorker {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {
		crossPlatformWorker = new Lwjgl3Launcher();
		return new Lwjgl3Application(new Core(), getDefaultConfiguration());
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		var config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("ray3k-jam-template");
		config.setWindowedMode(1024, 576);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 3);
		config.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		return config;
	}
	
	@Override
	public Table generateDebugTable() {
		var table = new Table();
		table.defaults().space(5);
		var iter = Arrays.stream(Values.class.getFields()).iterator();
		while (iter.hasNext()) {
			var field = iter.next();
			try {
				if (field.getType() == Integer.TYPE) {
					var label = new Label(field.getName() + ":", skin);
					table.add(label).right();
					
					var textField = new TextField(Integer.toString(field.getInt(null)), skin);
					textField.setName(field.getName());
					textField.setTextFieldFilter((textField1, c) -> Character.isDigit(c) || c == '-');
					table.add(textField).expandX().left();
					textField.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							try {
								var text = textField.getText();
								field.set(null, text == null || text.equals("") || !text.matches("-?\\d+(\\.\\d+)?") ? 0 : Integer.parseInt(text));
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
				} else if (field.getType() == Boolean.TYPE) {
					var textButton = new TextButton(field.getName(), skin, "toggle");
					textButton.setName(field.getName());
					textButton.setChecked(field.getBoolean(null));
					table.add(textButton).colspan(2);
					
					textButton.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							try {
								field.set(null, textButton.isChecked());
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
				} else if (field.getType() == String.class) {
					var label = new Label(field.getName() + ":", skin);
					table.add(label).right();
					
					var textField = new TextField((String)field.get(null), skin);
					textField.setName(field.getName());
					table.add(textField).expandX().left();
					
					textField.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							try {
								field.set(null, textField.getText());
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
				} else if (field.getType() == Float.TYPE) {
					var label = new Label(field.getName() + ":", skin);
					table.add(label).right();
					
					var textField = new TextField(Float.toString(field.getFloat(null)), skin);
					textField.setName(field.getName());
					textField.setTextFieldFilter((textField1, c) -> Character.isDigit(c) || c == '-' || c == '.');
					table.add(textField).expandX().left();
					textField.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							try {
								var text = textField.getText();
								field.set(null, text == null || text.equals("") || !text.matches("-?\\d+(\\.\\d+)?") ? 0 : Float.parseFloat(text));
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			table.row();
		}
		
		return table;
	}
	
	@Override
	public void saveDebugValues(Table table) {
		var iter = Arrays.stream(Values.class.getFields()).iterator();
		var jsonValue = new JsonValue(ValueType.object);
		
		while (iter.hasNext()) {
			var field = iter.next();
			var type = field.getType();
			var name = field.getName();
			
			if (type == Integer.TYPE) {
				TextField textField = table.findActor(name);
				var value = Integer.parseInt(textField.getText());
				jsonValue.addChild(name, new JsonValue(value));
			} else if (type == Boolean.TYPE) {
				TextButton textButton = table.findActor(name);
				var value = textButton.isChecked();
				jsonValue.addChild(name, new JsonValue(value));
			} else if (type == String.class) {
				TextField textField = table.findActor(name);
				var value = textField.getText();
				jsonValue.addChild(name, new JsonValue(value));
			} else if (type == Float.TYPE) {
				TextField textField = table.findActor(name);
				var value = Float.parseFloat(textField.getText());
				jsonValue.addChild(name, new JsonValue(value));
			}
		}
		
		var file = new FileHandle(Paths.get("assets/data/values.json").toFile());
		file.writeString(jsonValue.prettyPrint(OutputType.json, 20), false, "UTF-8");
	}
}